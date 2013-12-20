package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.yahoo.domain.data.UnitsData;
import weather.common.yahoo.parser.YWResponseParser;
/**
 * Unit Data Parser
 * @author ChungPV1
 *
 */
public class UnitsDataParser extends YWResponseParser {
	private static final String TEMPERATURE = "temperature";
	private static final String DISTANCE = "distance";
	private static final String PRESSURE = "pressure";
	private static final String SPEED = "speed";

	public static UnitsData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		UnitsData unitsData = new UnitsData();

		if (jsonObj.has(TEMPERATURE)) {
			unitsData.setTemperature(jsonObj.getString(TEMPERATURE));
		}

		if (jsonObj.has(DISTANCE)) {
			unitsData.setDistance(jsonObj.getString(DISTANCE));
		}

		if (jsonObj.has(PRESSURE)) {
			unitsData.setPressure(jsonObj.getString(PRESSURE));
		}

		if (jsonObj.has(SPEED)) {
			unitsData.setSpeed(jsonObj.getString(SPEED));
		}

		return unitsData;
	}

	public static UnitsData parse(Attributes attributes){
		UnitsData unitsData = new UnitsData();

		unitsData.setTemperature(attributes.getValue(TEMPERATURE));
		unitsData.setDistance(attributes.getValue(DISTANCE));
		unitsData.setPressure(attributes.getValue(PRESSURE));
		unitsData.setSpeed(attributes.getValue(SPEED));
		
		return unitsData;
	}
	
	public static JSONObject toJson(UnitsData unitsData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (unitsData.getTemperature() != null) {
			jsonObj.put(TEMPERATURE, unitsData.getTemperature());
		}

		if (unitsData.getDistance() != null) {
			jsonObj.put(DISTANCE, unitsData.getDistance());
		}

		if (unitsData.getPressure() != null) {
			jsonObj.put(PRESSURE, unitsData.getPressure());
		}

		if (unitsData.getSpeed() != null) {
			jsonObj.put(SPEED, unitsData.getSpeed());
		}

		return jsonObj;
	}
}
