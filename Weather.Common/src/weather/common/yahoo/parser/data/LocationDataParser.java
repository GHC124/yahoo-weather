package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.parser.YWResponseParser;

public class LocationDataParser extends YWResponseParser {
	private static final String CITY = "city";
	private static final String REGION = "region";
	private static final String COUNTRY = "country";

	public static LocationData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		LocationData locationData = new LocationData();

		if (jsonObj.has(CITY)) {
			locationData.setCity(jsonObj.getString(CITY));
		}

		if (jsonObj.has(REGION)) {
			locationData.setRegion(jsonObj.getString(REGION));
		}

		if (jsonObj.has(COUNTRY)) {
			locationData.setCountry(jsonObj.getString(COUNTRY));
		}

		return locationData;
	}

	public static LocationData parse(Attributes attributes){
		LocationData locationData = new LocationData();

		locationData.setCity(attributes.getValue(CITY));
		locationData.setRegion(attributes.getValue(REGION));
		locationData.setCountry(attributes.getValue(COUNTRY));

		return locationData;
	}

	public static JSONObject toJson(LocationData locationData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (locationData.getCity() != null) {
			jsonObj.put(CITY, locationData.getCity());
		}

		if (locationData.getRegion() != null) {
			jsonObj.put(REGION, locationData.getRegion());
		}

		if (locationData.getCountry() != null) {
			jsonObj.put(COUNTRY, locationData.getCountry());
		}

		return jsonObj;
	}
}
