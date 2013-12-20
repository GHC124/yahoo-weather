package com.yahooweather.info;

import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.LocationData;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahooweather.R;

public class LocationInfoFragment extends Fragment {
	private RelativeLayout mRlLocation;
	private TextView mTvLocationName;
	private TextView mTvCountry;
	private LinearLayout mLlAction;
	private TextView mTvActionSign;
	private TextView mTvActionName;

	private Integer mId = null;
	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_location_info, null);

		mRlLocation = (RelativeLayout) view.findViewById(R.id.yw_rlLocationInfo);
		mTvLocationName = (TextView) view
				.findViewById(R.id.yw_tvLocationInfo_Name);
		mTvCountry = (TextView) view
				.findViewById(R.id.yw_tvLocationInfo_Country);
		mLlAction = (LinearLayout) view
				.findViewById(R.id.yw_llLocationInfo_Action);
		mTvActionSign = (TextView) view
				.findViewById(R.id.yw_tvLocationInfo_ActionSign);
		mTvActionName = (TextView) view
				.findViewById(R.id.yw_tvLocationInfo_ActionName);

		mLlAction.setVisibility(View.INVISIBLE);
		mRlLocation.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mRlLocation.getVisibility() != View.VISIBLE) {
			mRlLocation.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		// Show information
		LocationData locationData = mChannelData.getLocationData();
		if (locationData != null) {
			if (locationData.getCity() != null) {
				mTvLocationName.setText(locationData.getCity());
			}
			if (locationData.getCountry() != null) {
				mTvCountry.setText(locationData.getCountry());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setLocationId(Integer id) {
		if (mLlAction.getVisibility() != View.VISIBLE) {
			mLlAction.setVisibility(View.VISIBLE);
		}
		mId = id;
		if (mId == null) {
			mTvActionSign.setText("+");
			mTvActionName.setText(getActivity().getString(
					R.string.yw_location_save));
		} else {
			mTvActionSign.setText("-");
			mTvActionName.setText(getActivity().getString(
					R.string.yw_location_saved));
		}
		mLlAction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mId == null) {
					addLocation();
				} else {
					removeLocation();
				}
			}
		});
	}

	private void addLocation() {
		MyLocationFragment myLocationFragment = (MyLocationFragment) getActivity()
				.getFragmentManager().findFragmentById(R.id.yw_flMyLocation);
		if (myLocationFragment != null) {
			myLocationFragment.addLocation();
		}
	}

	private void removeLocation() {
		MyLocationFragment myLocationFragment = (MyLocationFragment) getActivity()
				.getFragmentManager().findFragmentById(R.id.yw_flMyLocation);
		if (myLocationFragment != null) {
			myLocationFragment.removeLocation(mId);
		}
	}

}
