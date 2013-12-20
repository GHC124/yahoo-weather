package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.PlaceData;
import weather.common.yahoo.domain.data.PlacesData;
import weather.common.yahoo.parser.YWResponseParser;

public class PlacesDataParser extends YWResponseParser {
	private static final String PLACE = "place";

	public static PlacesData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		PlacesData placesData = new PlacesData();

		if (jsonObj.has(PLACE)) {
			JSONArray jsonArray = jsonObj.getJSONArray(PLACE);
			int length = jsonArray.length();
			PlaceData[] placeArray = new PlaceData[length];
			for (int i = 0; i < length; i++) {
				JSONObject placeObj = jsonArray.getJSONObject(i);
				placeArray[i] = PlaceDataParser.parse(placeObj);
			}
			placesData.setPlaceData(placeArray);
		}

		return placesData;
	}

	public static JSONObject toJson(PlacesData placesData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (placesData.getPlaceData() != null) {
			JSONArray jsonArray = new JSONArray();
			for (PlaceData item : placesData.getPlaceData()) {
				jsonArray.put(PlaceDataParser.toJson(item));
			}
			jsonObj.put(PLACE, jsonArray);
		}

		return jsonObj;
	}
}
