package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.yahoo.domain.data.WindData;
import weather.common.yahoo.parser.YWResponseParser;

public class WindDataParser extends YWResponseParser {
	private static final String CHILD = "child";
	private static final String DIRECTION = "direction";
	private static final String SPEED = "speed";

	public static WindData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		WindData windData = new WindData();

		if (jsonObj.has(CHILD)) {
			windData.setChild(jsonObj.getString(CHILD));
		}

		if (jsonObj.has(DIRECTION)) {
			windData.setDirection(jsonObj.getString(DIRECTION));
		}

		if (jsonObj.has(SPEED)) {
			windData.setSpeed(jsonObj.getString(SPEED));
		}

		return windData;
	}

	public static WindData parse(Attributes attributes) {
		WindData windData = new WindData();

		windData.setChild(attributes.getValue(CHILD));
		windData.setDirection(attributes.getValue(DIRECTION));
		windData.setSpeed(attributes.getValue(SPEED));

		return windData;
	}

	public static JSONObject toJson(WindData windData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (windData.getChild() != null) {
			jsonObj.put(CHILD, windData.getChild());
		}

		if (windData.getDirection() != null) {
			jsonObj.put(DIRECTION, windData.getDirection());
		}

		if (windData.getSpeed() != null) {
			jsonObj.put(SPEED, windData.getSpeed());
		}

		return jsonObj;
	}
}
