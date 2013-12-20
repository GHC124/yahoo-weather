package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;

import weather.common.util.ConversionsUtil;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.parser.YWResponseParser;

public class ConditionDataParser extends YWResponseParser {
	private static final String TEXT = "text";
	private static final String CODE = "code";
	private static final String TEMP = "temp";
	private static final String DATE = "date";

	public static ConditionData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		ConditionData conditionData = new ConditionData();

		if (jsonObj.has(TEXT)) {
			conditionData.setText(jsonObj.getString(TEXT));
		}

		if (jsonObj.has(CODE)) {
			conditionData.setCode(jsonObj.getInt(CODE));
		}

		if (jsonObj.has(TEMP)) {
			conditionData.setTemp(jsonObj.getInt(TEMP));
		}

		if (jsonObj.has(DATE)) {
			conditionData.setDate(ConversionsUtil
					.convertStringToRfc822Date(jsonObj.getString(DATE)));
		}

		return conditionData;
	}

	public static ConditionData parse(Attributes attributes)
			throws ParseException {
		ConditionData conditionData = new ConditionData();

		conditionData.setText(attributes.getValue(TEXT));
		String code = attributes.getValue(CODE);
		if (code != null) {
			conditionData.setCode(Integer.parseInt(code));
		}
		String temp = attributes.getValue(TEMP);
		if (temp != null) {
			conditionData.setTemp(Integer.parseInt(temp));
		}
		String date = attributes.getValue(DATE);
		if (date != null) {
			conditionData.setDate(ConversionsUtil
					.convertStringToRfc822Date(date));
		}

		return conditionData;
	}

	public static JSONObject toJson(ConditionData conditionData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (conditionData.getText() != null) {
			jsonObj.put(TEXT, conditionData.getText());
		}

		if (conditionData.getCode() != null) {
			jsonObj.put(CODE, conditionData.getCode());
		}

		if (conditionData.getTemp() != null) {
			jsonObj.put(TEMP, conditionData.getTemp());
		}

		if (conditionData.getDate() != null) {
			jsonObj.put(DATE, ConversionsUtil
					.convertRfc822DateToString(conditionData.getDate()));
		}

		return jsonObj;
	}
}
