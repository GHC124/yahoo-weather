package com.yahooweather.info;

import java.util.List;

import weather.common.yahoo.YWLoaderResult;
import weather.common.yahoo.domain.ChannelResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.manager.YWManagerFactory;
import weather.common.yahoo.manager.YWResponseHandler;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yahooweather.R;
import com.yahooweather.data.LocationDao;
import com.yahooweather.data.entity.Location;
import com.yahooweather.detail.DetailFragment;
import com.yahooweather.forecast.ForecastFragment;
import com.yahooweather.photos.PhotosFragment;
import com.yahooweather.sunmoon.SunMoonFragment;
import com.yahooweather.temp.LocationTempFragment;
import com.yahooweather.view.ErrorDialog;
import com.yahooweather.windpressure.WindPressureFragment;

public class MyLocationFragment extends Fragment {
	private static final String SHARED_PREFS_FILE = "YW_Data";
	private static final String KEY_LAST_WOEID = "last_woeid";

	private LinearLayout mLlMyLocation;
	private ListView mLvLocations;

	private MyLocationAdapter mLocationAdapter;
	private String mWoeid;
	private int mId;
	private ChannelData mChannelData;
	private LocationDao mLocationDao;
	private boolean mIsAcRunning = false;
	private SharedPreferences mPrefs;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPrefs = getActivity().getSharedPreferences(SHARED_PREFS_FILE,
				Context.MODE_PRIVATE);

		View view = inflater.inflate(R.layout.yw_fragment_my_location, null);

		mLlMyLocation = (LinearLayout) view.findViewById(R.id.yw_llMyLocation);
		mLvLocations = (ListView) view.findViewById(R.id.yw_lvMyLocation);

		mLocationAdapter = new MyLocationAdapter(getActivity(),
				R.layout.yw_my_location_item);
		mLvLocations.setAdapter(mLocationAdapter);
		mLvLocations.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Location location = mLocationAdapter.getItem(position);
				if (!mIsAcRunning) {
					mIsAcRunning = true;
					mWoeid = location.getWoeid();
					mId = location.getId();
					saveData(location.getWoeid());
					forecast(location.getWoeid());
				}
			}

		});

		mLocationDao = new LocationDao(getActivity());

		loadLocations();
		
		String woeid = mPrefs.getString(KEY_LAST_WOEID, null);
		if (woeid != null) {
			forecast(woeid);
		}

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setChannelData(String woeid, ChannelData channelData) {
		if (mLlMyLocation.getVisibility() != View.VISIBLE) {
			mLlMyLocation.setVisibility(View.VISIBLE);
		}
		mWoeid = woeid;
		mChannelData = channelData;

		LocationInfoFragment locationInfoFragment = (LocationInfoFragment) getActivity()
				.getFragmentManager().findFragmentById(R.id.yw_flLocationInfo);
		if (locationInfoFragment != null) {
			locationInfoFragment.setLocationId(null);
		}
		for (int i = 0; i < mLocationAdapter.getCount(); i++) {
			Location location = mLocationAdapter.getItem(i);
			// Check woeid existed
			if (location != null && location.getWoeid() != null && location.getWoeid().equalsIgnoreCase(mWoeid)
					&& locationInfoFragment != null) {
				locationInfoFragment.setLocationId(location.getId());
				break;
			}
		}
	}

	public void addLocation() {
		new AddLocationLoader().execute();
	}

	private void loadLocations() {
		new LoadLocationLoader().execute();
	}

	public void removeLocation(int id) {
		new RemoveLocationLoader().execute(id);
	}

	private void saveData(String woeid) {
		Editor editor = mPrefs.edit();
		editor.putString(KEY_LAST_WOEID, woeid);
		editor.commit();
	}

	private void forecast(String woeid) {
		String units = "c";
		YWManagerFactory.newForecastManager(getActivity(), null,
				mForecastResponseHandler, false, true, woeid, units).execute();
	}

	private class AddLocationLoader extends AsyncTask<Void, Void, Location> {

		@Override
		protected Location doInBackground(Void... params) {
			LocationData locationData = mChannelData.getLocationData();

			String woeid = mWoeid;
			String name = locationData.getCity() != null ? locationData
					.getCity() : "N/A";
			Location location = new Location();
			location.setWoeid(woeid);
			location.setName(name);

			mLocationDao.insertLocation(location);

			return location;
		}

		@Override
		protected void onPostExecute(Location location) {
			if (location != null) {
				if (location.getId() > 0) {
					mLocationAdapter.add(location);
					mLocationAdapter.notifyDataSetChanged();
					LocationInfoFragment locationInfoFragment = (LocationInfoFragment) getActivity()
							.getFragmentManager().findFragmentById(
									R.id.yw_flLocationInfo);
					if (locationInfoFragment != null) {
						locationInfoFragment.setLocationId(location.getId());
					}
				} else {
					ErrorDialog.getInstance(getActivity()).show(
							"Fail to add Location!");
				}
			}
		}
	}

	private class RemoveLocationLoader extends
			AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			Integer id = params[0];
			if (id != null) {
				int rows = mLocationDao.deleteLocation(id);
				if (rows <= 0) {
					id = -1;
				}
			}
			return id;
		}

		@Override
		protected void onPostExecute(Integer id) {
			if (id != null) {
				if (id > 0) {
					for (int i = 0; i < mLocationAdapter.getCount(); i++) {
						Location location = mLocationAdapter.getItem(i);
						if (location.getId() == id) {
							mLocationAdapter.remove(location);
							mLocationAdapter.notifyDataSetChanged();
							LocationInfoFragment locationInfoFragment = (LocationInfoFragment) getActivity()
									.getFragmentManager().findFragmentById(
											R.id.yw_flLocationInfo);
							if (locationInfoFragment != null) {
								locationInfoFragment.setLocationId(null);
							}
							break;
						}
					}
				} else {
					ErrorDialog.getInstance(getActivity()).show(
							"Fail to remove Location!");
				}
			}
		}
	}

	private class LoadLocationLoader extends
			AsyncTask<Void, Void, List<Location>> {

		@Override
		protected List<Location> doInBackground(Void... params) {
			List<Location> locations = mLocationDao.getListLocation();
			return locations;
		}

		@Override
		protected void onPostExecute(List<Location> locations) {
			if (locations != null) {
				for (Location location : locations) {
					mLocationAdapter.add(location);
				}
				mLocationAdapter.notifyDataSetChanged();
			}
		}
	}

	private YWResponseHandler mForecastResponseHandler = new YWResponseHandler() {

		@Override
		public void processResponse(YWLoaderResult loaderResult) {
			mIsAcRunning = false;
			if (loaderResult.isSuccessful()) {
				ChannelResponse channelResponse = (ChannelResponse) loaderResult
						.getResponse();
				ChannelData channelData = channelResponse.getChannelData();
				if (channelData != null) {
					Activity activity = getActivity();
					// Populate data to info fragment
					LocationInfoFragment infoFragment = (LocationInfoFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flLocationInfo);
					if (infoFragment != null) {
						infoFragment.setChannelData(channelData);
						infoFragment.setLocationId(mId);
					}
					// Populate data to my location fragment
					MyLocationFragment myLocationFragment = (MyLocationFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flMyLocation);
					if (myLocationFragment != null) {
						myLocationFragment.setChannelData(mWoeid, channelData);
					}
					// Populate data to info fragment
					LocationTempFragment tempFragment = (LocationTempFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flLocationTemp);
					if (tempFragment != null) {
						tempFragment.setChannelData(channelData);
					}
					// Populate data to forecast fragment
					ForecastFragment forecastFragment = (ForecastFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flForecast);
					if (forecastFragment != null) {
						forecastFragment.setChannelData(channelData);
					}
					// Populate data to photos fragment
					PhotosFragment photosFragment = (PhotosFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flPhotos);
					if (photosFragment != null) {
						photosFragment.setChannelData(channelData);
					}
					// Populate data to detail fragment
					DetailFragment detailFragment = (DetailFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flDetails);
					if (detailFragment != null) {
						detailFragment.setChannelData(channelData);
					}
					// Populate data to sun & moon fragment
					SunMoonFragment sunMoonFragment = (SunMoonFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flSunMoon);
					if (sunMoonFragment != null) {
						sunMoonFragment.setChannelData(channelData);
					}
					// Populate data to win & pressure fragment
					WindPressureFragment winPressureFragment = (WindPressureFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flWinPressure);
					if (winPressureFragment != null) {
						winPressureFragment.setChannelData(channelData);
					}

				}
			} else {
				ErrorDialog.getInstance(getActivity()).show(
						loaderResult.getFailureReason());
			}
		}
	};
}
