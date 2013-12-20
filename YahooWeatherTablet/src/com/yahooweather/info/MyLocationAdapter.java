package com.yahooweather.info;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yahooweather.R;
import com.yahooweather.data.entity.Location;

public class MyLocationAdapter extends ArrayAdapter<Location> {
	private final Context mContext;
	private final int mResourceId;

	public MyLocationAdapter(Context context, int resource) {
		super(context, resource);

		mContext = context;
		mResourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		MyLocationHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(mResourceId, null);
			holder = new MyLocationHolder();
			holder.tvName = (TextView) view
					.findViewById(R.id.yw_tvMyLocationItem_Name);

			view.setTag(holder);
		} else {
			holder = (MyLocationHolder) view.getTag();
		}

		Location location = getItem(position);

		if (location.getName() != null) {
			holder.tvName.setText(location.getName());
		} else {
			holder.tvName.setText(mContext.getString(R.string.yw_display_na));
		}

		return view;
	}

	// Define components inside the item of ListView
	private static class MyLocationHolder {
		TextView tvName;
	}
}
