package weather.common.yahoo.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import weather.common.yahoo.manager.YWManager.RequestType;
import weather.common.yahoo.parser.ForecastResponseParser;
import weather.common.yahoo.parser.PhotosResponseParser;
import weather.common.yahoo.parser.PlacesResponseParser;
import android.app.Activity;
import android.app.Fragment;

public class YWManagerFactory {

	private static final int GET_FORECAST = 1;
	private static final String GET_FORECAST_ENDPOINT = "http://query.yahooapis.com/v1/public/yql";
	private static final String GET_FORECAST_Q_PARAM = "q";
	private static final String GET_FORECAST_FORMAT_PARAM = "format";

	public static YWManager newForecastManager(Activity activity,
			Fragment fragment, YWResponseHandler responseHandler,
			boolean initLoader, boolean forceActivityLoader, String woeid,
			String units) {

		String endpoint = GET_FORECAST_ENDPOINT;

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		if (woeid != null) {
			String query = "select * from weather.forecast where woeid=\""
					+ woeid + "\"" + " and u=\"" + units + "\"";

			params.add(new BasicNameValuePair(GET_FORECAST_Q_PARAM, query));
		}
		params.add(new BasicNameValuePair(GET_FORECAST_FORMAT_PARAM, "json"));

		return new YWManager(GET_FORECAST, activity, fragment, responseHandler,
				RequestType.GET, endpoint, params,
				new ForecastResponseParser(), null, initLoader,
				forceActivityLoader, false);
	}

	private static final int GET_PLACE = GET_FORECAST << 1;
	private static final String GET_PLACE_ENDPOINT = "http://where.yahooapis.com/v1/places.q(:TEXT);count=10";
	private static final String GET_PLACE_TEXT_VAL = ":TEXT";
	private static final String GET_PLACE_FORMAT_PARAM = "format";

	public static YWManager newGetPlacesManager(Activity activity,
			Fragment fragment, YWResponseHandler responseHandler,
			boolean initLoader, boolean forceActivityLoader, String text) {

		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String endpoint = GET_PLACE_ENDPOINT.replace(GET_PLACE_TEXT_VAL, text);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(GET_PLACE_FORMAT_PARAM, "json"));

		return new YWManager(GET_PLACE, activity, fragment, responseHandler,
				RequestType.GET, endpoint, params, new PlacesResponseParser(),
				null, initLoader, forceActivityLoader, true);
	}

	private static final int GET_PHOTO = GET_PLACE << 1;
	private static final String GET_PHOTO_ENDPOINT = "http://query.yahooapis.com/v1/public/yql";
	private static final String GET_PHOTO_Q_PARAM = "q";
	private static final String GET_PHOTO_FORMAT_PARAM = "format";

	public static YWManager newGetPhotosManager(Activity activity,
			Fragment fragment, YWResponseHandler responseHandler,
			boolean initLoader, boolean forceActivityLoader, String text) {

		String endpoint = GET_PHOTO_ENDPOINT;

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		if (text != null) {
			String query = "select * from flickr.photos.search where has_geo=\"true\" and text=\""
					+ text
					+ "\""
					+ " and api_key =\""
					+ YWManager.API_KEY
					+ "\"";

			params.add(new BasicNameValuePair(GET_PHOTO_Q_PARAM, query));
		}
		params.add(new BasicNameValuePair(GET_PHOTO_FORMAT_PARAM, "json"));

		return new YWManager(GET_PHOTO, activity, fragment, responseHandler,
				RequestType.GET, endpoint, params, new PhotosResponseParser(),
				null, initLoader, forceActivityLoader, false);
	}
}
