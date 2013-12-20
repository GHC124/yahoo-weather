package weather.common.yahoo.parser;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.PhotosResponse;
import weather.common.yahoo.domain.YWResponse;
import weather.common.yahoo.domain.data.PhotosData;
import weather.common.yahoo.parser.data.PhotosDataParser;

public class PhotosResponseParser extends YWResponseParser {
	private static final String QUERY = "query";
	private static final String RESULTS = "results";

	@Override
	public YWResponse parse(String resultString) throws JSONException,
			ParseException {
		JSONObject jsonObj = new JSONObject(resultString);

		// Get photos object
		if (jsonObj.has(QUERY)) {
			jsonObj = jsonObj.getJSONObject(QUERY);
			if (jsonObj.has(RESULTS)) {
				jsonObj = jsonObj.getJSONObject(RESULTS);
			} else {
				throw new JSONException("JSON syntax error!");
			}
		} else {
			throw new JSONException("JSON syntax error!");
		}

		PhotosData photosData = PhotosDataParser.parse(jsonObj);

		PhotosResponse photosResponse = new PhotosResponse();
		photosResponse.setPhotosData(photosData);

		return photosResponse;
	}
}
