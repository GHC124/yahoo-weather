package weather.common.yahoo.parser;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.PlacesResponse;
import weather.common.yahoo.domain.YWResponse;
import weather.common.yahoo.domain.data.PlacesData;
import weather.common.yahoo.parser.data.PlacesDataParser;

public class PlacesResponseParser extends YWResponseParser {
	private static final String PLACES = "places";

	@Override
	public YWResponse parse(String resultString) throws JSONException,
			ParseException {
		JSONObject jsonObj = new JSONObject(resultString);

		// Get places object
		if (jsonObj.has(PLACES)) {
			jsonObj = jsonObj.getJSONObject(PLACES);
		} else {
			throw new JSONException("JSON syntax error!");
		}
		PlacesData placesData = PlacesDataParser.parse(jsonObj);

		PlacesResponse placesResponse = new PlacesResponse();
		placesResponse.setPlaceData(placesData);

		return placesResponse;
	}

}
