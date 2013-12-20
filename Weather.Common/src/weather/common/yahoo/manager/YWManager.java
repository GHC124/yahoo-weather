package weather.common.yahoo.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import weather.common.util.WebUtil;
import weather.common.yahoo.YWDataLoader;
import weather.common.yahoo.YWLoaderResult;
import weather.common.yahoo.domain.YWResponse;
import weather.common.yahoo.domain.data.YWData;
import weather.common.yahoo.parser.YWResponseParser;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class YWManager implements LoaderCallbacks<YWLoaderResult> {
	protected static final boolean DEBUG = true;
	protected static final String TAG = YWManager.class.getSimpleName();
	protected static final int HTTP_REQUEST_TIMEOUT = -1;

	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY = 10000;

	private static final String PARAM_APP_ID = "appid";
	public static final String PARAM_API_KEY = "api_key";

	private int mRetryCount;
	private boolean mRetryEnabled = true;
	private final Handler mRetryHandler = new Handler();

	private final String APP_ID = "QZsJ_FHV34EbiIULXpmOdNW1bzyVPU4_hirBtO9QCn.uikk5SU4554BU0OKkEMu1GfqT6iI-";
	public static final String API_KEY = "c2a2c370f22bc4bf59d2785cc8ac6ea6";

	protected int mId;
	protected final Activity mActivity;
	protected final Fragment mFragment;
	protected final YWResponseHandler mResponseHandler;
	protected final RequestType mRequestType;
	protected final String mEndpoint;
	protected final List<NameValuePair> mParams;
	protected final YWResponseParser mResponseParser;
	protected final YWData mBodyData;
	protected final boolean mInitLoader;
	protected final boolean mForceActivityLoader;
	protected final boolean mRequiresToken;

	public enum RequestType {
		GET, POST, PUT, DELETE
	};

	public YWManager(int id, Activity activity, Fragment fragment,
			YWResponseHandler responseHandler, RequestType requestType,
			String endpoint, List<NameValuePair> params,
			YWResponseParser responseParser, YWData bodyData,
			boolean initLoader, boolean forceActivityLoader,
			boolean requiresToken) {
		mId = id;
		mActivity = activity;
		mFragment = fragment;
		mResponseHandler = responseHandler;
		mRequestType = requestType;
		mEndpoint = endpoint;
		mParams = params;
		mResponseParser = responseParser;
		mBodyData = bodyData;
		mInitLoader = initLoader;
		mForceActivityLoader = forceActivityLoader;
		mRequiresToken = requiresToken;
	}

	public void execute() {
		cancelRetry();
		mRetryCount = 0;
		LoaderManager lm = getLoaderManager();
		if (lm != null) {
			if (mInitLoader) {
				lm.initLoader(mId, null, this);
			} else {
				lm.restartLoader(mId, null, this);
			}
		}
	}

	public YWManager enableRetry(boolean enabled) {
		mRetryEnabled = enabled;
		return this;
	}

	public YWLoaderResult loadInBackground() {
		String authUrl = getRequestUrl(mEndpoint, mParams, mRequiresToken);
		return executeRequest(mRequestType, authUrl, mResponseParser, mBodyData);
	}

	@Override
	public Loader<YWLoaderResult> onCreateLoader(int id, Bundle args) {
		return new YWDataLoader(mActivity, this);
	}

	@Override
	public void onLoadFinished(Loader<YWLoaderResult> loader,
			YWLoaderResult result) {
		// if the fragment has been removed, skip sending back the result and
		// stop retry
		if (isFragmentRemoved()) {
			return;
		}

		if (!mRetryEnabled || result.isSuccessful()
				|| (++mRetryCount > MAX_RETRIES)) {
			if (DEBUG) {
				if (mRetryEnabled) {
					if (!result.isSuccessful()) {
						Log.d(TAG, "Retries exhausted, returning error "
								+ mEndpoint);
					} else if (mRetryCount > 0) {
						Log.d(TAG, "Retry succeeded " + mEndpoint);
					}
				}
			}
			LoaderManager lm = getLoaderManager();
			if (lm != null) {
				getLoaderManager().destroyLoader(mId);
			}
			mResponseHandler.processResponse(result);
		} else {
			retryRequest(RETRY_DELAY);
		}
	}

	@Override
	public void onLoaderReset(Loader<YWLoaderResult> arg0) {
		if (DEBUG) {
			Log.d(TAG, "onLoaderReset " + mEndpoint);
		}
		cancelRetry();
	}

	protected void retryRequest(int delay) {
		if (DEBUG) {
			Log.d(TAG, "Retry request in " + delay + "ms " + mEndpoint);
		}
		cancelRetry();
		mRetryHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				restartLoader();
			}
		}, delay);
	}

	protected void cancelRetry() {
		mRetryHandler.removeCallbacksAndMessages(null);
	}

	protected void restartLoader() {
		LoaderManager lm = getLoaderManager();
		if (lm != null) {
			lm.restartLoader(mId, null, this);
		}
	}

	protected LoaderManager getLoaderManager() {
		if (!mForceActivityLoader && mFragment != null) {
			return isFragmentRemoved() ? null : mFragment.getLoaderManager();
		} else {
			return mActivity.getLoaderManager();
		}
	}

	protected boolean isFragmentRemoved() {
		if (mFragment != null && !mFragment.isAdded()) {
			if (DEBUG) {
				Log.d(TAG, "Fragment removed");
			}
			return true;
		}
		return false;
	}

	private String getRequestUrl(String endpointUrl,
			List<NameValuePair> params, boolean requiresToken) {
		if (params == null) {
			params = new LinkedList<NameValuePair>();
		} else {
			// create copy so we don't modify original
			params = new LinkedList<NameValuePair>(params);
		}
		if (requiresToken) {
			params.add(new BasicNameValuePair(PARAM_APP_ID, APP_ID));
		}
		return WebUtil.buildUrl(endpointUrl, params, true);
	}

	private YWLoaderResult executeRequest(RequestType requestType,
			String authUrl, YWResponseParser parser, YWData bodyData) {
		YWResponse errorResponse = null;

		HttpResponse httpResponse = null;
		try {
			if (requestType == RequestType.GET) {
				if (DEBUG) {
					Log.i(TAG, "Get " + authUrl);
				}
				httpResponse = WebUtil.executeRequest(new HttpGet(authUrl),
						HTTP_REQUEST_TIMEOUT, null, null);
			}
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, requestType + " failed", e);
			}
		}
		if (httpResponse == null) {
			if (DEBUG) {
				Log.e(TAG, requestType + " failed");
			}
			return YWLoaderResult.createFailed(errorResponse);
		}
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		InputStream is = null;
		try {
			is = WebUtil.getInputStream(httpResponse);
			if (statusCode == HttpStatus.SC_OK) {
				YWResponse response = parser.parse(is);
				response.setUrl(authUrl);
				return YWLoaderResult.createSuccessful(response);
			} else { // failure
				errorResponse = new YWResponseParser().parse(is);
				String message = errorResponse.getJsonData();
				throw new Exception("HTTP " + requestType
						+ " request failed - status code:" + statusCode
						+ " response:" + message);
			}
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, requestType + " failed", e);
			}
			return YWLoaderResult.createFailed(errorResponse, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
