package com.yahooweather.util;

import weather.common.yahoo.domain.data.PhotoData;
import android.content.Context;

import com.yahooweather.R;

/**
 * Yahoo util
 * 
 * @author ChungPV1
 * 
 */
public class YahooUtil {
	public static String getPhotoImageUrl(PhotoData photoData) {
		String url = "http://farm:FARM.static.flickr.com/:SERVER/:ID_:SECRET.jpg";
		url = url.replace(":FARM", photoData.getFarm());
		url = url.replace(":SERVER", photoData.getServer());
		url = url.replace(":ID", photoData.getId());
		url = url.replace(":SECRET", photoData.getSecret());
		return url;
	}

	public static String getConditionImageUrl(String code) {
		String baseUrl = "http://l.yimg.com/a/i/us/we/52/:CODE.gif";
		return baseUrl.replace(":CODE", code);
	}

	public static String getDay(Context context, String str) {
		String day = "";
		if (str.equalsIgnoreCase("Mon")) {
			day = context.getString(R.string.yw_forecast_monday);
		} else if (str.equalsIgnoreCase("Tue")) {
			day = context.getString(R.string.yw_forecast_tuesday);
		} else if (str.equalsIgnoreCase("Wed")) {
			day = context.getString(R.string.yw_forecast_webnesday);
		} else if (str.equalsIgnoreCase("Thu")) {
			day = context.getString(R.string.yw_forecast_thursday);
		} else if (str.equalsIgnoreCase("Fri")) {
			day = context.getString(R.string.yw_forecast_friday);
		} else if (str.equalsIgnoreCase("Sat")) {
			day = context.getString(R.string.yw_forecast_saturday);
		} else if (str.equalsIgnoreCase("Sun")) {
			day = context.getString(R.string.yw_forecast_sunday);
		}

		return day;
	}
}
