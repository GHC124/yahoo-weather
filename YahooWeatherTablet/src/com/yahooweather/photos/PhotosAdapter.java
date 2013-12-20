package com.yahooweather.photos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yahooweather.R;
import com.yahooweather.util.YWImageDownloader;

public class PhotosAdapter extends ArrayAdapter<PhotoItem> {
	private final Context mContext;
	private final int mResourceId;

	public PhotosAdapter(Context context, int resource) {
		super(context, resource);

		mContext = context;
		mResourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		PhotoHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(mResourceId, null);
			holder = new PhotoHolder();
			holder.ivItem = (ImageView) view
					.findViewById(R.id.yw_ivPhotos_Item);
			holder.pgItem = (ProgressBar) view
					.findViewById(R.id.yw_pgPhotos_Item);

			view.setTag(holder);
		} else {
			holder = (PhotoHolder) view.getTag();
		}

		PhotoItem item = getItem(position);

		if (item.getUrl() != null) {
			YWImageDownloader.download(mContext, item.getUrl(), holder.ivItem,
					holder.pgItem);
		}

		return view;
	}

	// Define components inside the item of ListView
	private static class PhotoHolder {
		ImageView ivItem;
		ProgressBar pgItem;
	}
}
