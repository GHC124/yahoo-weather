package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.util.ConversionsUtil;
import weather.common.yahoo.domain.data.ForecastData;
import weather.common.yahoo.parser.YWResponseParser;

public class ForecastDataParser extends YWResponseParser {
	private static final String DAY = "day";
	private static final String DATE = "date";
	private static final String LOW = "low";
	private static final String HIGH = "high";
	private static final String TEXT = "text";
	private static final String CODE = "code";

	public static ForecastData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		ForecastData forecastData = new ForecastData();

		if (jsonObj.has(DAY)) {
			forecastData.setDay(jsonObj.getString(DAY));
		}

		if (jsonObj.has(DATE)) {
			forecastData.setDate(ConversionsUtil
					.convertStringToDdMmmYyyy(jsonObj.getString(DATE)));
		}

		if (jsonObj.has(LOW)) {
			forecastData.setLow(jsonObj.getInt(LOW));
		}

		if (jsonObj.has(HIGH)) {
			forecastData.setHigh(jsonObj.getInt(HIGH));
		}

		if (jsonObj.has(TEXT)) {
			forecastData.setText(jsonObj.getString(TEXT));
		}

		if (jsonObj.has(CODE)) {
			forecastData.setCode(jsonObj.getInt(CODE));
		}

		return forecastData;
	}

	public static ForecastData parse(Attributes attributes)
			throws ParseException {
		ForecastData forecastData = new ForecastData();

		forecastData.setDay(attributes.getValue(DAY));
		String date = attributes.getValue(DATE);
		if (date != null) {
			forecastData
					.setDate(ConversionsUtil.convertStringToDdMmmYyyy(date));
		}
		String low = attributes.getValue(LOW);
		if (low != null) {
			forecastData.setLow(Integer.parseInt(low));
		}
		String high = attributes.getValue(HIGH);
		if (high != null) {
			forecastData.setHigh(Integer.parseInt(high));
		}
		forecastData.setText(attributes.getValue(TEXT));
		String code = attributes.getValue(CODE);
		if (code != null) {
			forecastData.setCode(Integer.parseInt(code));
		}

		return forecastData;
	}

	public static JSONObject toJson(ForecastData forecastData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (forecastData.getDay() != null) {
			jsonObj.put(DAY, forecastData.getDay());
		}

		if (forecastData.getDate() != null) {
			jsonObj.put(DATE, ConversionsUtil
					.convertDdMmmYyyyToString(forecastData.getDate()));
		}

		if (forecastData.getLow() != null) {
			jsonObj.put(LOW, forecastData.getLow());
		}

		if (forecastData.getHigh() != null) {
			jsonObj.put(HIGH, forecastData.getHigh());
		}

		if (forecastData.getText() != null) {
			jsonObj.put(TEXT, forecastData.getText());
		}

		if (forecastData.getCode() != null) {
			jsonObj.put(CODE, forecastData.getCode());
		}

		return jsonObj;
	}
}
