package com.yahooweather.forecast;

import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ForecastData;
import weather.common.yahoo.domain.data.ItemData;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.yahooweather.R;
import com.yahooweather.util.YWImageDownloader;
import com.yahooweather.util.YahooUtil;

public class ForecastFragment extends Fragment {
	private LinearLayout mLlForecast;
	private LinearLayout mLlForecastItems;

	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_forecast, null);

		mLlForecast = (LinearLayout) view.findViewById(R.id.yw_llForecast);
		mLlForecastItems = (LinearLayout) view
				.findViewById(R.id.yw_llForecast_Items);

		mLlForecast.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlForecast.getVisibility() != View.VISIBLE) {
			mLlForecast.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		Activity activity = getActivity();
		ItemData itemData = mChannelData.getItemData();
		if (itemData != null) {
			ForecastData[] forecastsData = itemData.getForecastData();
			if (forecastsData != null) {
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				mLlForecastItems.removeAllViews();
				for (int i = 0; i < forecastsData.length - 1; i++) {
					ForecastData forecastData = forecastsData[i];
					String day = "";
					if (i == 0) {
						day = activity.getString(R.string.yw_forecast_today);
					} else if (i == 1) {
						day = activity.getString(R.string.yw_forecast_tomorrow);
					} else {
						day = YahooUtil.getDay(activity, forecastData.getDay());
					}
					forecastData.setDay(day);
					View forecastItem = createForecastItem(inflater,
							forecastData);
					mLlForecastItems.addView(forecastItem);
					View divider = new View(activity);
					divider.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, 1));
					divider.setBackgroundColor(Color.parseColor("#E5686866"));
					mLlForecastItems.addView(divider);
				}
				ForecastData forecastData = forecastsData[forecastsData.length - 1];
				forecastData.setDay(YahooUtil.getDay(activity,
						forecastData.getDay()));
				View forecastItem = createForecastItem(inflater, forecastData);
				mLlForecastItems.addView(forecastItem);
			}
		}
	}

	private View createForecastItem(LayoutInflater inflater,
			ForecastData forecastData) {
		View view = inflater.inflate(R.layout.yw_fragment_forecast_item, null);
		TextView tvDay = (TextView) view.findViewById(R.id.yw_tvForecast_Day);
		NetworkImageView ivCondition = (NetworkImageView) view
				.findViewById(R.id.yw_ivForecast_Condition);
		TextView tvTempHigh = (TextView) view
				.findViewById(R.id.yw_tvForecast_TempHigh);
		TextView tvTempLow = (TextView) view
				.findViewById(R.id.yw_tvForecast_TempLow);

		String day = forecastData.getDay();
		if (day != null) {
			tvDay.setText(day);
		}
		Integer high = forecastData.getHigh();
		if (high != null) {
			tvTempHigh.setText(String.valueOf(high));
		}
		Integer low = forecastData.getLow();
		if (low != null) {
			tvTempLow.setText(String.valueOf(low));
		}
		Integer code = forecastData.getCode();
		if (code != null) {
			String url = YahooUtil.getConditionImageUrl(String.valueOf(code));
			YWImageDownloader.download(getActivity(), url, ivCondition);
		}
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
