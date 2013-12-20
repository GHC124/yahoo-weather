package com.yahooweather.widget;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import weather.common.util.AsyncService;
import weather.common.util.WebUtil;
import weather.common.yahoo.domain.ChannelResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.domain.data.ItemData;
import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.parser.ForecastResponseParser;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import com.yahooweather.MainActivity;
import com.yahooweather.R;
import com.yahooweather.util.YahooUtil;

public class UpdateWidgetService extends AsyncService {
	private static final String TAG = UpdateWidgetService.class.getSimpleName();
	private static final boolean DEBUG = true;
	private static final String SHARED_PREFS_FILE = "YW_Data";
	private static final String KEY_LAST_WOEID = "last_woeid";

	public UpdateWidgetService() {
		super(TAG);
	}
	
	public UpdateWidgetService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		SharedPreferences prefs = context.getSharedPreferences(
				SHARED_PREFS_FILE, Context.MODE_PRIVATE);
		String woeid = prefs.getString(KEY_LAST_WOEID, null);
		if (woeid != null) {
			forecast(intent, woeid);
		}
	}

	private static final String GET_FORECAST_ENDPOINT = "http://query.yahooapis.com/v1/public/yql";
	private static final String GET_FORECAST_Q_PARAM = "q";
	private static final String GET_FORECAST_FORMAT_PARAM = "format";

	private void forecast(Intent intent, String woeid) {
		String units = "c";
		String endpoint = GET_FORECAST_ENDPOINT;
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		if (woeid != null) {
			String query = "select * from weather.forecast where woeid=\""
					+ woeid + "\"" + " and u=\"" + units + "\"";

			params.add(new BasicNameValuePair(GET_FORECAST_Q_PARAM, query));
		}
		params.add(new BasicNameValuePair(GET_FORECAST_FORMAT_PARAM, "json"));

		String url = WebUtil.buildUrl(endpoint, params, true);

		HttpResponse httpResponse = null;
		try {
			httpResponse = WebUtil.executeRequest(new HttpGet(url), -1, null,
					null);
		} catch (IOException e) {
			if (DEBUG) {
				Log.e(TAG, "Get failed", e);
			}
		}
		if (httpResponse == null) {
			if (DEBUG) {
				Log.e(TAG, "Get failed");
			}
			return;
		}
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		InputStream is = null;
		try {
			is = WebUtil.getInputStream(httpResponse);
			if (statusCode == HttpStatus.SC_OK) {
				ChannelResponse response = (ChannelResponse) new ForecastResponseParser()
						.parse(is);
				response.setUrl(url);
				// Download image
				ChannelData channelData = response.getChannelData();
				if (channelData != null) {
					ItemData itemData = channelData.getItemData();
					if (itemData != null) {
						Integer temp = null;
						String city = null;
						Bitmap bitmap = null;
						ConditionData conditionData = itemData
								.getConditionData();
						if (conditionData != null) {
							temp = conditionData.getTemp();
						}
						LocationData locationData = channelData
								.getLocationData();
						if (locationData.getCity() != null) {
							city = locationData.getCity();
						}
						if (conditionData.getCode() != null) {
							String imageUrl = YahooUtil
									.getConditionImageUrl(String
											.valueOf(conditionData.getCode()));

							HttpResponse imageResponse = WebUtil
									.executeRequest(new HttpGet(imageUrl), -1,
											null, null);
							HttpEntity entity = imageResponse.getEntity();
							if (entity != null) {
								is = entity.getContent();
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inSampleSize = 2;
								bitmap = BitmapFactory.decodeStream(
										new FlushedInputStream(is), null,
										options);
							}
						}
						Context context = getApplicationContext();
						AppWidgetManager appWidgetManager = AppWidgetManager
								.getInstance(context);
						int[] allWidgetIds = intent
								.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
						for (int widgetId : allWidgetIds) {
							RemoteViews remoteViews = new RemoteViews(
									context.getPackageName(),
									R.layout.yw_widget_layout);
							remoteViews.setTextViewText(R.id.yw_tvWidget_Temp,
									String.valueOf(temp));
							remoteViews.setTextViewText(R.id.yw_tvWidget_City,
									city);
							if (bitmap != null) {
								remoteViews.setImageViewBitmap(
										R.id.yw_ivWidget_Update, bitmap);
							}
							// Register an onClickListener
							Intent clickIntent = new Intent(context,
									MainActivity.class);
							PendingIntent pendingIntent = PendingIntent
									.getActivity(getApplicationContext(), 0,
											clickIntent, 0);
							remoteViews.setOnClickPendingIntent(
									R.id.yw_rlWidget, pendingIntent);
							appWidgetManager.updateAppWidget(widgetId,
									remoteViews);
						}
					}
				}
			} else { // failure
				throw new Exception("HTTP Get "
						+ " request failed - status code:" + statusCode);
			}
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "Get failed", e);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					if (DEBUG) {
						Log.e(TAG, "Get failed", e);
					}
				}
			}
		}
	}

	private class FlushedInputStream extends FilterInputStream {
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

}
