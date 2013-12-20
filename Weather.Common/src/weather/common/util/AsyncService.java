package weather.common.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Represents service that dispatches each onStart/onStartCommand request in
 * separate thread. Use {@link #onHandleIntent(Intent)} to dispatch intent sent
 * to service. This service is implemented in a way that it will be restarted
 * whenever system has killed process and there are still unprocessed requests.
 */
public abstract class AsyncService extends Service {
	private static final String TAG = AsyncService.class.getSimpleName();
	private ExecutorService mThreadPool;
	private Queue<CommandExecutionArgs> mCommandsQueue = new ConcurrentLinkedQueue<CommandExecutionArgs>();

	/**
	 * Creates new instance of a service.
	 * 
	 * @param name
	 *            used to name threads, spawned when dispatching
	 *            {@link #onStart(Intent, int)} or
	 *            {@link #onStartCommand(Intent, int, int)}.
	 */
	public AsyncService(String name) {
		super();
		mThreadPool = Executors.newCachedThreadPool(new NamedThreadFactory(name));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// We're making sure that all threads are done with their job. This is
		// consistency check.
		if (mThreadPool.shutdownNow().size() > 0) {
			Log.w(TAG, "Service was destroyed while other tasks are scheduled to run");
		}
	}

	@Override
	public final void onStart(Intent intent, int startId) {
		CommandExecutionArgs args = new CommandExecutionArgs(intent, startId);
		mCommandsQueue.offer(args);
		mThreadPool.execute(new CommandExecutor(args));
	}

	@Override
	public final int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		// Asking system to re-deliver intent if we didn't dispatch it before
		// dying.
		return START_REDELIVER_INTENT;
	}

	/**
	 * This method is invoked on separate thread to dispatch intent that was
	 * delivered to {@link #onStart(Intent, int)} or
	 * {@link #onStartCommand(Intent, int, int)}. When all requests have been
	 * handled, the {@link AsyncService} stops itself, so you should not call
	 * {@link #stopSelf}.
	 * 
	 * @param intent
	 *            The value passed to
	 *            {@link android.content.Context#startService(Intent)}.
	 */
	protected abstract void onHandleIntent(Intent intent);

	private final class CommandExecutor implements Runnable {
		private final CommandExecutionArgs mArgs;

		public CommandExecutor(CommandExecutionArgs args) {
			mArgs = args;
		}

		@Override
		public void run() {
			onHandleIntent(mArgs.intent);
			mArgs.isFinished = true;
			synchronized (mCommandsQueue) {
				// Cleaning up commands queue up to first unfinished arg. This
				// makes sure service is stopped whenever last processed command
				// is executed.
				CommandExecutionArgs args;
				while ((args = mCommandsQueue.peek()) != null && args.isFinished) {
					// Making sure to call stopSelf in the same sequence as
					// intents were delivered. This ensures system will destroy
					// service when all requests are processed.
					stopSelf(mCommandsQueue.poll().startId);
				}
			}
		}
	}

	/**
	 * Values holder for execution.
	 * 
	 */
	private static final class CommandExecutionArgs {
		final int startId;
		final Intent intent;
		volatile boolean isFinished;

		public CommandExecutionArgs(Intent intent, int startId) {
			this.intent = intent;
			this.startId = startId;
		}
	}

}
