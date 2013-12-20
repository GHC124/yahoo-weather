package com.yahooweather.temp;

import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.domain.data.ForecastData;
import weather.common.yahoo.domain.data.ItemData;
import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.yahooweather.R;
import com.yahooweather.util.YWImageDownloader;
import com.yahooweather.util.YahooUtil;
import com.yahooweather.widget.UpdateWidgetService;
import com.yahooweather.widget.WidgetProvider;

public class LocationTempFragment extends Fragment {
	private LinearLayout mLlTemp;
	private NetworkImageView mIvTemp;
	private TextView mTvText;
	private TextView mTvTemp;
	private TextView mTvTempHight;
	private TextView mTvTempLow;

	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_temp_info, null);

		mLlTemp = (LinearLayout) view.findViewById(R.id.yw_llTempInfo);
		mIvTemp = (NetworkImageView) view.findViewById(R.id.yw_ivTempInfo);
		mTvText = (TextView) view.findViewById(R.id.yw_tvTempInfo_Text);
		mTvTemp = (TextView) view.findViewById(R.id.yw_tvTempInfo_Temp);
		mTvTempHight = (TextView) view
				.findViewById(R.id.yw_tvTempInfo_TempHight);
		mTvTempLow = (TextView) view.findViewById(R.id.yw_tvTempInfo_TempLow);

		mLlTemp.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlTemp.getVisibility() != View.VISIBLE) {
			mLlTemp.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		// Show information
		Integer temp = null;
		Integer code = null;
		ItemData itemData = mChannelData.getItemData();
		if (itemData != null) {
			ConditionData conditionData = itemData.getConditionData();
			if (conditionData != null) {
				temp = conditionData.getTemp();
				if (temp != null) {
					mTvTemp.setText(String.valueOf(temp));
				}
				String text = conditionData.getText();
				if (text != null) {
					mTvText.setText(text);
				}
				code = conditionData.getCode();
				if (code != null) {
					String url = YahooUtil.getConditionImageUrl(String
							.valueOf(code));
					YWImageDownloader.download(getActivity(), url, mIvTemp);
				}
			}
			ForecastData[] forecastsData = itemData.getForecastData();
			if (forecastsData != null && forecastsData.length > 0) {
				ForecastData forecastData = forecastsData[0];
				Integer tempHight = forecastData.getHigh();
				Integer tempLow = forecastData.getLow();
				if (tempHight != null) {
					if (temp != null && temp > tempHight) {
						tempHight = temp;
					}
					mTvTempHight.setText(String.valueOf(tempHight));
				}
				if (tempLow != null) {
					mTvTempLow.setText(String.valueOf(tempLow));
				}
			}
		}
		// Update widget
		Activity activity = getActivity();
		ComponentName thisWidget = new ComponentName(activity,
				WidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(activity);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		// Build the intent to call the service
		Intent intent = new Intent(activity.getApplicationContext(),
				UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		// Update the widgets via the service
		activity.startService(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
