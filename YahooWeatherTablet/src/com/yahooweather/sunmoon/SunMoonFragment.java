package com.yahooweather.sunmoon;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import weather.common.yahoo.domain.data.AstronomyData;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.domain.data.ItemData;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yahooweather.R;

public class SunMoonFragment extends Fragment {
	private int mSunInterval = 50;

	private LinearLayout mLlSunMoon;
	private ImageView mIvSun;
	private ImageView mIvSunSpot;
	private TextView mTvSunrise;
	private TextView mTvSunset;

	private int mSunspotWidth = 0;
	private int mSunspotMaxWidth = 0;
	private int mSunMarginFLeft = 0;
	private int mSunMarginFTop = 0;
	private int mSunMarginLeft = 0;
	private int mSunMarginTop = 0;
	private int mSuncurveHeight = 0;

	private MoveSunProcessor mMoveSunProcessor;
	private Handler mSunHandler;
	private ChannelData mChannelData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_sun_moon, null);

		Activity activity = getActivity();

		mSuncurveHeight = (int) activity.getResources().getDimension(
				R.dimen.yw_height_percent_13);

		mSunMarginFLeft = (int) activity.getResources().getDimension(
				R.dimen.yw_width_percent_3p6);
		mSunMarginFTop = (int) activity.getResources().getDimension(
				R.dimen.yw_height_percent_11p3);

		mSunspotWidth = 5; // dp
		mSunspotMaxWidth = 280; // dp

		mLlSunMoon = (LinearLayout) view.findViewById(R.id.yw_llSunMoon);
		mIvSun = (ImageView) view.findViewById(R.id.yw_ivSunMoon_Sun);
		mIvSunSpot = (ImageView) view.findViewById(R.id.yw_ivSunMoon_SunSpot);
		mTvSunrise = (TextView) view.findViewById(R.id.yw_tvSunMoon_Sunrise);
		mTvSunset = (TextView) view.findViewById(R.id.yw_tvSunMoon_Sunset);

		mLlSunMoon.setVisibility(View.INVISIBLE);

		mSunHandler = new Handler();

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlSunMoon.getVisibility() != View.VISIBLE) {
			mLlSunMoon.setVisibility(View.VISIBLE);
		}
		mChannelData = channelData;
		// Reset sun
		setSunspotWidth(mSunspotWidth);
		setSunPosition(mSunMarginFLeft, mSunMarginFTop);
		// Show information
		AstronomyData astronomyData = mChannelData.getAstronomyData();
		if (astronomyData != null) {
			String sunrise = astronomyData.getSunrise();
			String sunset = astronomyData.getSunset();
			if (sunrise != null) {
				mTvSunrise.setText(sunrise);
			}
			if (sunset != null) {
				mTvSunset.setText(sunset);
			}
			// Get hours
			float iSunrise = -1;
			float iSunset = -1;
			try {
				iSunrise = getHour(sunrise);
				iSunset = getHour(sunset);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
			if (iSunrise != -1 && iSunset != -1) {
				// Use 24 hour format
				iSunset = iSunset + 12;
				// Calculate position of sun
				int space = mSunspotMaxWidth / (int) (iSunset - iSunrise);
				int currentHour = getHour();
				if (currentHour != -1 && currentHour >= iSunrise
						&& currentHour <= iSunset) {
					int sunspotMoreWidth = space
							* (int) (currentHour - iSunrise);
					// Calculate margin top
					int marginTop = mSuncurveHeight
							* mSuncurveHeight
							- ((mSunspotMaxWidth / 2 - sunspotMoreWidth) * (mSunspotMaxWidth / 2 - sunspotMoreWidth));
					Log.e("MarginTop", (int) Math.sqrt(marginTop) + "");

					mMoveSunProcessor = new MoveSunProcessor(space / 4,
							mSunspotWidth, mSunspotWidth + sunspotMoreWidth,
							mSunMarginFLeft - 10, mSunMarginFLeft
									+ sunspotMoreWidth - 10, mSunMarginFTop,
							mSuncurveHeight - (int) Math.sqrt(marginTop));
					mSunHandler.postDelayed(mMoveSunProcessor, mSunInterval);
				}
			}
		}
	}

	private class MoveSunProcessor implements Runnable {
		int mSpace = 0;
		int mSpotWidth = 0;
		int mMaxSpotWidth = 0;
		int mMarginLeft = 0;
		int mMaxMarginLeft = 0;
		int mMarginTop = 0;
		int mMaxMarginTop = 0;
		int mMinus = -1;

		public MoveSunProcessor(int space, int spotWidth, int maxSpotWidth,
				int marginLeft, int maxMarginLeft, int marginTop,
				int maxMarginTop) {
			super();
			this.mSpace = space;
			this.mMarginLeft = marginLeft;
			this.mMaxMarginLeft = maxMarginLeft;
			this.mMarginTop = marginTop;
			this.mMaxMarginTop = maxMarginTop;
			this.mSpotWidth = spotWidth;
			this.mMaxSpotWidth = maxSpotWidth;
		}

		@Override
		public void run() {
			mMarginLeft += mSpace;
			//mMarginTop += (mMinus * mSpace);
			mSpotWidth += mSpace;

			if (mMarginTop <= 0) {
				mMinus = -mMinus;
			}

			if (mSpotWidth <= mMaxSpotWidth) {
				setSunspotWidth(mSpotWidth);
			}
			if (mMarginLeft <= mMaxMarginLeft) {
				setSunPosition(mMarginLeft, mMarginTop);
			}

			if (mSpotWidth < mMaxSpotWidth || mMarginLeft < mMaxMarginLeft) {
				mSunHandler.postDelayed(mMoveSunProcessor, mSunInterval);
			}
		}
	}

	private void setSunspotWidth(int width) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIvSunSpot
				.getLayoutParams();
		lp.width = width;
		mIvSunSpot.setLayoutParams(lp);
	}

	private void setSunPosition(int marginLeft, int marginTop) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIvSun
				.getLayoutParams();
		lp.setMargins(marginLeft, marginTop, 0, 0);
		mIvSun.setLayoutParams(lp);
	}

	private float getHour(String time) throws NumberFormatException {
		float hour = -1;

		int index = time.indexOf(":");
		if (index != -1) {
			hour = Integer.parseInt(time.substring(0, index));
			// index = time.indexOf(" ");
			// if (index != -1) {
			// hour = Integer.parseInt(time.substring(0, index));
			// }
		} else {
			index = time.indexOf(" ");
			if (index != -1) {
				hour = Integer.parseInt(time.substring(0, index));
			}
		}

		return hour;
	}

	private int getHour() {
		ItemData itemData = mChannelData.getItemData();
		if (itemData != null) {
			ConditionData conditionData = itemData.getConditionData();
			if (conditionData != null) {
				Date date = conditionData.getDate();
				if (date != null) {
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTime(date);
					// gets hour in 24h format
					return calendar.get(Calendar.HOUR_OF_DAY);
				}
			}
		}
		return -1;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
