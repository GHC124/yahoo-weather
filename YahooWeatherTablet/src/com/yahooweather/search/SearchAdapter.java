package com.yahooweather.search;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yahooweather.R;

public class SearchAdapter extends ArrayAdapter<SearchItem> {
	private final Context mContext;
	private final int mLayoutResourceId;

	public SearchAdapter(Context context, int textViewResourceId,
			List<SearchItem> objects) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
		this.mLayoutResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		LocationHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			holder = new LocationHolder();
			holder.txtView = (TextView) row.findViewById(R.id.yw_tvSearchItem);
			row.setTag(holder);
		} else {
			holder = (LocationHolder) row.getTag();
		}

		SearchItem item = getItem(position);
		if (item.getHtml() != null) {
			holder.txtView.setText(Html.fromHtml(item.getHtml()));
		} else {
			holder.txtView.setText(item.getText());
		}

		return row;
	}

	private static class LocationHolder {
		TextView txtView;
	}
}
