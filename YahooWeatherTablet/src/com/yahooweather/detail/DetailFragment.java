package com.yahooweather.detail;

import weather.common.yahoo.domain.data.AtmosphereData;
import weather.common.yahoo.domain.data.ChannelData;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yahooweather.R;

public class DetailFragment extends Fragment {
	private LinearLayout mLlDetail;
	private TextView mTvVisibility;
	private TextView mTvHumidity;

	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_detail, null);

		mLlDetail = (LinearLayout) view.findViewById(R.id.yw_llDetail);
		mTvVisibility = (TextView) view
				.findViewById(R.id.yw_tvDetail_Visibility);
		mTvHumidity = (TextView) view.findViewById(R.id.yw_tvDetail_Humidity);

		mLlDetail.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlDetail.getVisibility() != View.VISIBLE) {
			mLlDetail.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		// Show information
		AtmosphereData atmosphereData = mChannelData.getAtmosphereData();
		if (atmosphereData != null) {
			if (atmosphereData.getVisibility() != null) {
				mTvVisibility.setText(atmosphereData.getVisibility());
			} else {
				mTvVisibility.setText(getActivity().getString(
						R.string.yw_display_na));
			}
			if (atmosphereData.getHumidity() != null) {
				mTvHumidity.setText(atmosphereData.getHumidity());
			} else {
				mTvHumidity.setText(getActivity().getString(
						R.string.yw_display_na));
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
