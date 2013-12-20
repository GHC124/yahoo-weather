package weather.common.yahoo;

import weather.common.yahoo.manager.YWManager;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class YWDataLoader extends AsyncTaskLoader<YWLoaderResult> {
	private YWManager mLoaderManager;

	public YWDataLoader(Context context, YWManager loaderManager) {
		super(context);
		this.mLoaderManager = loaderManager;
	}

	@Override
	public YWLoaderResult loadInBackground() {
		return mLoaderManager.loadInBackground();
	}

	// runs on the UI thread
	@Override
	public void deliverResult(YWLoaderResult result) {
		if (isStarted()) {
			super.deliverResult(result);
			onReset();
		}
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	protected void onStopLoading() {
		// attempt to cancel the current load task if possible
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();

		// ensure the loader is stopped
		onStopLoading();
	}

	@Override
	public void onCanceled(YWLoaderResult result) {
	}

}
