package weather.common.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 
 * @author ChungPV1
 * 
 */
public class ImageDownloader {
	private static final long RESET_INTERVAL = 10000;

	private static ImageDownloader INSTANCE = null;
	private MemoryCache mMemoryCache;

	private Map<ImageView, String> mImageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private Map<ProgressBar, String> mProgressBars = Collections
			.synchronizedMap(new WeakHashMap<ProgressBar, String>());
	private ExecutorService mExecutorService;
	// handler to display images in UI thread
	private final Handler mHandler;
	private int mStubId = -1;

	private ImageDownloader(Context context) {
		mHandler = new Handler();
		mMemoryCache = new MemoryCache();
		mExecutorService = Executors.newFixedThreadPool(5);
	}

	public static ImageDownloader getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new ImageDownloader(context);
		}
		return INSTANCE;
	}

	public static void clearInstance() {
		if (INSTANCE != null) {
			INSTANCE = null;
		}
	}

	public void setLimit(long limit) {
		mMemoryCache.setLimit(limit);
	}

	public void download(String url, ImageView imageView,
			ProgressBar progressBar) {
		// Clear cache after X milliseconds
		mHandler.removeCallbacks(mResetRunnable);
		mHandler.postDelayed(mResetRunnable, RESET_INTERVAL);

		mImageViews.put(imageView, url);
		mProgressBars.put(progressBar, url);

		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView, progressBar);
		}
	}

	public void setStubId(int stubId) {
		mStubId = stubId;
	}

	private void queuePhoto(String url, ImageView imageView,
			ProgressBar progressBar) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, progressBar);
		mExecutorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		InputStream is = null;
		try {
			Bitmap bitmap = null;
			HttpResponse response = WebUtil.executeRequest(new HttpGet(url),
					-1, null, null);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				is = entity.getContent();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is),
						null, options);
			}
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) {
				clearCache();
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar progressBar;

		public PhotoToLoad(String u, ImageView i, ProgressBar p) {
			url = u;
			imageView = i;
			progressBar = p;
		}
	}

	/**
	 * A patched InputSteam that tries harder to fully read the input stream.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L)
					break;
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				if (photoToLoad.progressBar != null)
					photoToLoad.progressBar.setVisibility(View.VISIBLE);
				Bitmap bmp = getBitmap(photoToLoad.url);
				if (bmp != null)
					mMemoryCache.put(photoToLoad.url, bmp);
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				mHandler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = mImageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				photoToLoad.imageView.setImageBitmap(bitmap);
			} else if (mStubId != -1) {
				photoToLoad.imageView.setImageResource(mStubId);
			}
			if (photoToLoad.progressBar != null)
				photoToLoad.progressBar.setVisibility(View.GONE);
		}
	}

	private Runnable mResetRunnable = new Runnable() {

		@Override
		public void run() {
			clearCache();
		}
	};

	public void clearCache() {
		mMemoryCache.clear();
	}

	public void stopLoad() {
		mExecutorService.shutdown();
	}

	public void cancel() {
		stopLoad();
		clearCache();
	}
}
