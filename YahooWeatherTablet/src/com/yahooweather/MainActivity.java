package com.yahooweather;

import java.util.ArrayList;
import java.util.List;

import weather.common.yahoo.YWLoaderResult;
import weather.common.yahoo.domain.ChannelResponse;
import weather.common.yahoo.domain.PlacesResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.PlaceData;
import weather.common.yahoo.domain.data.PlacesData;
import weather.common.yahoo.manager.YWManagerFactory;
import weather.common.yahoo.manager.YWResponseHandler;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.yahooweather.detail.DetailFragment;
import com.yahooweather.forecast.ForecastFragment;
import com.yahooweather.info.LocationInfoFragment;
import com.yahooweather.info.MyLocationFragment;
import com.yahooweather.photos.PhotosFragment;
import com.yahooweather.photos.PhotosFragment.PhotosListener;
import com.yahooweather.search.SearchAdapter;
import com.yahooweather.search.SearchItem;
import com.yahooweather.sunmoon.SunMoonFragment;
import com.yahooweather.temp.LocationTempFragment;
import com.yahooweather.util.BitmapLruCache;
import com.yahooweather.util.EditTextViewUtil;
import com.yahooweather.util.YWImageDownloader;
import com.yahooweather.view.ErrorDialog;
import com.yahooweather.windpressure.WindPressureFragment;

public class MainActivity extends Activity {
	private static final String SHARED_PREFS_FILE = "YW_Data";
	private static final String KEY_LAST_WOEID = "last_woeid";
	/**
	 * 25% of the heap goes to image cache. Stored in bytes.
	 */
	private static final int IMAGE_CACHE_SIZE = (int) (Runtime.getRuntime()
			.maxMemory() / 4);
	private static final int AUTO_COMPLETE_DELAY = 1000;

	private final Handler mAcHandler = new Handler();

	private ScrollView mSvCenter;

	// Search
	private boolean mIsAcNext = true;
	private boolean mIsAcRunning = false;
	private AutoCompleteTextView mAcSearchBox;

	// Forecast
	private String mWoeid = null;
	private boolean mIsFcRunning = false;

	private SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPrefs = getSharedPreferences(SHARED_PREFS_FILE,
				Context.MODE_PRIVATE);
		
		setContentView(R.layout.yw_activity_main);

		// Setup some resources
		YWImageDownloader.setImageLoader(new ImageLoader(Volley
				.newRequestQueue(this), new BitmapLruCache(
				IMAGE_CACHE_SIZE / 1024)));
		YWImageDownloader.setLimit(this, IMAGE_CACHE_SIZE);

		final NetworkImageView mIvBackground = (NetworkImageView) findViewById(R.id.yw_imgBackground);
		mIvBackground.setBackgroundResource(R.drawable.yw_background);
		// Prepare fragments
		PhotosFragment photosFragment = new PhotosFragment();
		photosFragment.setPhotosListener(new PhotosListener() {

			@Override
			public void showImage(String url) {
				if (url != null) {
					mIvBackground
							.setDefaultImageResId(R.drawable.yw_background);
					YWImageDownloader.download(MainActivity.this, url,
							mIvBackground);
				}
			}
		});

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.yw_flLocationInfo, new LocationInfoFragment());
		ft.replace(R.id.yw_flMyLocation, new MyLocationFragment());
		ft.replace(R.id.yw_flLocationTemp, new LocationTempFragment());
		ft.replace(R.id.yw_flForecast, new ForecastFragment());
		ft.replace(R.id.yw_flPhotos, photosFragment);
		ft.replace(R.id.yw_flDetails, new DetailFragment());
		ft.replace(R.id.yw_flSunMoon, new SunMoonFragment());
		ft.replace(R.id.yw_flWinPressure, new WindPressureFragment());
		ft.commit();
		//
		mSvCenter = (ScrollView) findViewById(R.id.yw_svCenter);

		mAcSearchBox = (AutoCompleteTextView) findViewById(R.id.yw_edtSearch);
		mAcSearchBox.setThreshold(1);
		mAcSearchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					resetAutocompleteTimer();
				} else {
					clearAutocomplete();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				handleClearButton(mAcSearchBox);
			}
		});

		mAcSearchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
				mIsAcNext = false;
				SearchAdapter adapter = (SearchAdapter) p.getAdapter();
				mWoeid = adapter.getItem(pos).getWoeid();
				performSearch();
			}
		});

		Button btnSearch = (Button) findViewById(R.id.yw_btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performAutocomplete();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void clearAutocomplete() {
		if (mAcSearchBox.getAdapter() != null
				&& !mAcSearchBox.getAdapter().isEmpty()) {
			setAutocomplete(new ArrayList<SearchItem>(0));
		}
	}

	private void resetAutocompleteTimer() {
		mAcHandler.removeCallbacks(mAcTask);
		mAcHandler.postDelayed(mAcTask, AUTO_COMPLETE_DELAY);
	}

	private void handleClearButton(final EditText et) {
		if (et.length() > 0) {
			Drawable clearSearch = getResources().getDrawable(
					R.drawable.yw_btn_clear_small);
			et.setCompoundDrawablesWithIntrinsicBounds(null, null, clearSearch,
					null);
			et.setOnTouchListener(new EditTextViewUtil(et) {
				@Override
				public boolean onDrawableTouch(final MotionEvent event) {
					et.setText("");
					return false;
				}
			});
		} else {
			et.setCompoundDrawables(null, null, null, null);
		}
	}

	private void setAutocomplete(List<SearchItem> predictions) {
		SearchAdapter adapter = new SearchAdapter(this,
				R.layout.yw_search_item, predictions);
		mAcSearchBox.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		if (!predictions.isEmpty()) {
			mAcSearchBox.showDropDown();
		}
	}

	private void performAutocomplete() {
		String text = mAcSearchBox.getText().toString();
		if (!text.isEmpty()) {
			mIsAcRunning = true;
			YWManagerFactory.newGetPlacesManager(this, null,
					mPlacesResponseHandler, false, true, text).execute();
		}
	}

	private void performSearch() {
		clearAutocomplete();
		String units = "c";
		YWManagerFactory.newForecastManager(this, null,
				mForecastResponseHandler, false, true, mWoeid, units).execute();
	}

	private boolean checkSearchItem(List<SearchItem> predictions,
			SearchItem searchItem) {
		for (SearchItem item : predictions) {
			if (item.getText().equalsIgnoreCase(searchItem.getText())) {
				return false;
			}
		}
		return true;
	}

	private final Runnable mAcTask = new Runnable() {
		@Override
		public void run() {
			if (!mIsAcRunning && mIsAcNext) {
				performAutocomplete();
			} else {
				mIsAcNext = true;
			}
		}
	};

	private void saveData(String woeid) {
		Editor editor = mPrefs.edit();
		editor.putString(KEY_LAST_WOEID, woeid);
		editor.commit();
	}
	
	private YWResponseHandler mPlacesResponseHandler = new YWResponseHandler() {

		@Override
		public void processResponse(YWLoaderResult loaderResult) {
			mIsAcRunning = false;
			if (loaderResult.isSuccessful()) {
				PlacesResponse placesResponse = (PlacesResponse) loaderResult
						.getResponse();
				PlacesData placesData = placesResponse.getPlaceData();
				if (placesData.getPlaceData() != null) {
					List<SearchItem> predictions = new ArrayList<SearchItem>();
					for (PlaceData placeData : placesData.getPlaceData()) {
						SearchItem searchItem = new SearchItem();
						searchItem.setText(placeData.getName());
						searchItem.setHtml(placeData.getName());
						if (placeData.getCountry() != null
								&& !placeData.getCountry().isEmpty()) {
							searchItem.setText(searchItem.getText() + " , "
									+ placeData.getCountry());
							searchItem.setHtml(searchItem.getHtml() + " , <b>"
									+ placeData.getCountry() + "</b>");
						}
						searchItem.setWoeid(placeData.getWoeid());
						if (checkSearchItem(predictions, searchItem)) {
							predictions.add(searchItem);
						}
					}
					setAutocomplete(predictions);
				}
			} else {
				ErrorDialog.getInstance(MainActivity.this).show(
						loaderResult.getFailureReason());
			}
		}
	};

	private YWResponseHandler mForecastResponseHandler = new YWResponseHandler() {

		@Override
		public void processResponse(YWLoaderResult loaderResult) {
			mIsAcRunning = false;
			if (loaderResult.isSuccessful()) {
				ChannelResponse channelResponse = (ChannelResponse) loaderResult
						.getResponse();
				ChannelData channelData = channelResponse.getChannelData();
				if (channelData != null) {
					saveData(mWoeid);
					
					mSvCenter.setHorizontalScrollBarEnabled(false);

					Activity activity = MainActivity.this;
					// Populate data to info fragment
					LocationInfoFragment infoFragment = (LocationInfoFragment) activity
							.getFragmentManager().findFragmentById(
									R.id.yw_flLocationInfo);
					if (infoFragment != null) {
						infoFragment.setChannelData(channelData);
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
				ErrorDialog.getInstance(MainActivity.this).show(
						loaderResult.getFailureReason());
			}
		}
	};
}
