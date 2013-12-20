package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.yahoo.domain.data.AstronomyData;
import weather.common.yahoo.parser.YWResponseParser;

/**
 * Unit Data Parser
 * 
 * @author ChungPV1
 * 
 */
public class AstronomyDataParser extends YWResponseParser {
	private static final String SUNRISE = "sunrise";
	private static final String SUNSET = "sunset";

	public static AstronomyData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		AstronomyData astronomyData = new AstronomyData();

		if (jsonObj.has(SUNRISE)) {
			astronomyData.setSunrise(jsonObj.getString(SUNRISE));
		}

		if (jsonObj.has(SUNSET)) {
			astronomyData.setSunset(jsonObj.getString(SUNSET));
		}

		return astronomyData;
	}

	public static AstronomyData parse(Attributes attributes) {
		AstronomyData astronomyData = new AstronomyData();

		astronomyData.setSunrise(attributes.getValue(SUNRISE));
		astronomyData.setSunset(attributes.getValue(SUNSET));

		return astronomyData;
	}

	public static JSONObject toJson(AstronomyData astronomyData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (astronomyData.getSunrise() != null) {
			jsonObj.put(SUNRISE, astronomyData.getSunrise());
		}

		if (astronomyData.getSunset() != null) {
			jsonObj.put(SUNSET, astronomyData.getSunset());
		}

		return jsonObj;
	}
}
