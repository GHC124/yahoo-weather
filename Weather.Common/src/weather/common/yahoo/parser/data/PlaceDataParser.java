package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.PlaceData;
import weather.common.yahoo.parser.YWResponseParser;

public class PlaceDataParser extends YWResponseParser {
	private static final String NAME = "name";
	private static final String COUNTRY = "country";
	private static final String WOEID = "woeid";

	public static PlaceData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		PlaceData PlaceData = new PlaceData();

		if (jsonObj.has(NAME)) {
			PlaceData.setName(jsonObj.getString(NAME));
		}

		if (jsonObj.has(COUNTRY)) {
			PlaceData.setCountry(jsonObj.getString(COUNTRY));
		}

		if (jsonObj.has(WOEID)) {
			PlaceData.setWoeid(jsonObj.getString(WOEID));
		}

		return PlaceData;
	}

	public static JSONObject toJson(PlaceData PlaceData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (PlaceData.getName() != null) {
			jsonObj.put(NAME, PlaceData.getName());
		}

		if (PlaceData.getCountry() != null) {
			jsonObj.put(COUNTRY, PlaceData.getCountry());
		}

		if (PlaceData.getWoeid() != null) {
			jsonObj.put(WOEID, PlaceData.getWoeid());
		}

		return jsonObj;
	}
}
