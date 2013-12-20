package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.yahoo.domain.data.AtmosphereData;
import weather.common.yahoo.parser.YWResponseParser;

public class AtmosphereDataParser extends YWResponseParser {
	private static final String HUMIDITY = "humidity";
	private static final String VISIBILITY = "visibility";
	private static final String PRESSURE = "pressure";
	private static final String RISING = "rising";

	public static AtmosphereData parse(JSONObject jsonObj)
			throws JSONException, ParseException {
		AtmosphereData atmosphereData = new AtmosphereData();

		if (jsonObj.has(HUMIDITY)) {
			atmosphereData.setHumidity(jsonObj.getString(HUMIDITY));
		}

		if (jsonObj.has(VISIBILITY)) {
			atmosphereData.setVisibility(jsonObj.getString(VISIBILITY));
		}

		if (jsonObj.has(PRESSURE)) {
			atmosphereData.setPressure(jsonObj.getString(PRESSURE));
		}

		if (jsonObj.has(RISING)) {
			atmosphereData.setRising(jsonObj.getString(RISING));
		}

		return atmosphereData;
	}
	
	public static AtmosphereData parse(Attributes attributes) {
		AtmosphereData atmosphereData = new AtmosphereData();

		atmosphereData.setHumidity(attributes.getValue(HUMIDITY));
		atmosphereData.setVisibility(attributes.getValue(VISIBILITY));
		atmosphereData.setPressure(attributes.getValue(PRESSURE));
		atmosphereData.setRising(attributes.getValue(RISING));
		
		return atmosphereData;
	}

	public static JSONObject toJson(AtmosphereData atmosphereData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (atmosphereData.getHumidity() != null) {
			jsonObj.put(HUMIDITY, atmosphereData.getHumidity());
		}

		if (atmosphereData.getVisibility() != null) {
			jsonObj.put(VISIBILITY, atmosphereData.getVisibility());
		}

		if (atmosphereData.getPressure() != null) {
			jsonObj.put(PRESSURE, atmosphereData.getPressure());
		}

		if (atmosphereData.getRising() != null) {
			jsonObj.put(RISING, atmosphereData.getRising());
		}

		return jsonObj;
	}
}
