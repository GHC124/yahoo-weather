package com.yahooweather.util;

import weather.common.util.ImageDownloader;
import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class YWImageDownloader {
	private static ImageLoader mImageLoader;

	public static void setImageLoader(ImageLoader imageLoader) {
		mImageLoader = imageLoader;
	}
	
	public static void setLimit(Context context, long limit) {
		ImageDownloader.getInstance(context).setLimit(limit);
	}

	public static void download(Context context, String url, ImageView imageView) {
		download(context, url, imageView, null);
	}

	public static void download(Context context, String url,
			ImageView imageView, ProgressBar progressBar) {
		if (imageView instanceof NetworkImageView) {
			// download using google volley library
			((NetworkImageView) imageView).setImageUrl(url, mImageLoader);
		} else {
			ImageDownloader.getInstance(context).download(url, imageView,
					progressBar);
		}
	}
}
