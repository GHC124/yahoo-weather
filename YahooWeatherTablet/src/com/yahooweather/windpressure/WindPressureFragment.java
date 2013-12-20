package com.yahooweather.windpressure;

import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.WindData;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yahooweather.R;

public class WindPressureFragment extends Fragment {
	private LinearLayout mLlWindPressure;
	private ImageView mIvBigWind;
	private ImageView mIvSmallWind;
	private TextView mTvWindSpeed;
	private Animation mBigWindAnimation;
	private Animation mSmallWindAnimation;

	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_wind_pressure, null);

		mLlWindPressure = (LinearLayout) view
				.findViewById(R.id.yw_llWindPressure);
		mTvWindSpeed = (TextView) view
				.findViewById(R.id.yw_tvWindPressure_Wind);
		mIvBigWind = (ImageView) view
				.findViewById(R.id.yw_ivWindPressure_BigWind);
		mIvSmallWind = (ImageView) view
				.findViewById(R.id.yw_ivWindPressure_SmallWind);

		mBigWindAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.yw_rotate_infinite);

		mSmallWindAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.yw_rotate_infinite);

		mLlWindPressure.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlWindPressure.getVisibility() != View.VISIBLE) {
			mLlWindPressure.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		mIvBigWind.startAnimation(mBigWindAnimation);
		mIvSmallWind.startAnimation(mSmallWindAnimation);

		WindData windData = mChannelData.getWindData();
		if (windData != null) {
			String wind = windData.getSpeed();
			if (wind != null) {
				mTvWindSpeed.setText(wind);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
