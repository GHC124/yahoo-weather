package com.yahooweather.photos;

import java.util.Random;

import weather.common.yahoo.YWLoaderResult;
import weather.common.yahoo.domain.PhotosResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.domain.data.PhotoData;
import weather.common.yahoo.domain.data.PhotosData;
import weather.common.yahoo.manager.YWManagerFactory;
import weather.common.yahoo.manager.YWResponseHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yahooweather.R;
import com.yahooweather.util.YahooUtil;
import com.yahooweather.view.ErrorDialog;
import com.yahooweather.view.HorizontalListView;

public class PhotosFragment extends Fragment {
	private LinearLayout mLlPhotos;
	private HorizontalListView mLvPhotos;
	private ProgressBar mPgLoading;

	private PhotosAdapter mPhotosAdapter;
	private PhotosListener mPhotosListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.yw_fragment_photos, null);

		mLlPhotos = (LinearLayout) view.findViewById(R.id.yw_llPhotos);
		mLvPhotos = (HorizontalListView) view
				.findViewById(R.id.yw_lvPhotos_Items);

		mPhotosAdapter = new PhotosAdapter(getActivity(),
				R.layout.yw_photos_item);
		
		mPgLoading = (ProgressBar)view.findViewById(R.id.yw_pgPhotos_Loading);
		mPgLoading.setVisibility(View.VISIBLE);
		
		mLlPhotos.setVisibility(View.INVISIBLE);

		return view;
	}

	public void setChannelData(ChannelData channelData) {
		if (mLlPhotos.getVisibility() != View.VISIBLE) {
			mLlPhotos.setVisibility(View.VISIBLE);
		}
		mPhotosAdapter.clear();
		mPhotosAdapter.notifyDataSetChanged();
		// Show information
		LocationData locationData = channelData.getLocationData();
		if (locationData != null) {
			String text = locationData.getCity();
			if(mPgLoading.getVisibility() != View.VISIBLE){
				mPgLoading.setVisibility(View.VISIBLE);
			}
			YWManagerFactory.newGetPhotosManager(getActivity(), null,
					mPhotosResponseHandler, false, true, text).execute();
		}
	}

	public void setPhotosListener(PhotosListener photosListener) {
		mPhotosListener = photosListener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private YWResponseHandler mPhotosResponseHandler = new YWResponseHandler() {

		@Override
		public void processResponse(YWLoaderResult loaderResult) {
			if (loaderResult.isSuccessful()) {
				PhotosResponse photosResponse = (PhotosResponse) loaderResult
						.getResponse();
				PhotosData photosData = photosResponse.getPhotosData();
				if (photosData.getPhotoData() != null) {
					for (PhotoData photoData : photosData.getPhotoData()) {
						PhotoItem photoItem = new PhotoItem();
						photoItem.setUrl(YahooUtil.getPhotoImageUrl(photoData));
						mPhotosAdapter.add(photoItem);
					}
					if(mPgLoading.getVisibility() != View.INVISIBLE){
						mPgLoading.setVisibility(View.INVISIBLE);
					}
					mLvPhotos.setAdapter(mPhotosAdapter);
					mPhotosAdapter.notifyDataSetChanged();
					//mLvPhotos.setScrollToTop(true);
					randomImage();
				}
			} else {
				ErrorDialog.getInstance(getActivity()).show(
						loaderResult.getFailureReason());
			}
		}
	};

	private void randomImage() {
		int size = mPhotosAdapter.getCount();
		Random r = new Random();
		int index = r.nextInt(size - 1);
		if (index >= 0) {
			PhotoItem item = mPhotosAdapter.getItem(index);
			if (mPhotosListener != null) {
				mPhotosListener.showImage(item.getUrl());
			}
		}
	}

	public interface PhotosListener {
		void showImage(String url);
	}
}
