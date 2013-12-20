package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weather.common.util.ConversionsUtil;
import weather.common.yahoo.domain.data.ConditionData;
import weather.common.yahoo.domain.data.ForecastData;
import weather.common.yahoo.domain.data.ItemData;
import weather.common.yahoo.parser.YWResponseParser;

public class ItemDataParser extends YWResponseParser {
	private static final String TITLE = "title";
	private static final String GEO_LAT = "lat";
	private static final String GEO_LONG = "long";
	private static final String LINK = "link";
	private static final String PUBDATE = "pubDate";
	private static final String DESCRIPTION = "description";
	private static final String CONDITION = "condition";
	private static final String FORECAST = "forecast";

	public static ItemData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		ItemData itemData = new ItemData();

		if (jsonObj.has(TITLE)) {
			itemData.setTitle(jsonObj.getString(TITLE));
		}

		if (jsonObj.has(GEO_LAT)) {
			itemData.setGeoLat(jsonObj.getString(GEO_LAT));
		}

		if (jsonObj.has(GEO_LONG)) {
			itemData.setGeoLong(jsonObj.getString(GEO_LONG));
		}

		if (jsonObj.has(LINK)) {
			itemData.setLink(jsonObj.getString(LINK));
		}
		if (jsonObj.has(PUBDATE)) {
			itemData.setPubDate(ConversionsUtil
					.convertStringToRfc822Date(jsonObj.getString(PUBDATE)));
		}
		if (jsonObj.has(DESCRIPTION)) {
			itemData.setDescription(jsonObj.getString(DESCRIPTION));
		}
		if (jsonObj.has(CONDITION)) {
			ConditionData conditionData = ConditionDataParser.parse(jsonObj
					.getJSONObject(CONDITION));
			itemData.setConditionData(conditionData);
		}
		if (jsonObj.has(FORECAST)) {
			JSONArray jsonArray = jsonObj.getJSONArray(FORECAST);
			int length = jsonArray.length();
			if (length > 0) {
				ForecastData[] forecastDataArray = new ForecastData[length];
				for (int i = 0; i < length; i++) {
					JSONObject jsonItem = jsonArray.getJSONObject(i);
					forecastDataArray[i] = ForecastDataParser.parse(jsonItem);
				}
				itemData.setForecastData(forecastDataArray);
			}
		}

		return itemData;
	}

	public static JSONObject toJson(ItemData itemData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (itemData.getTitle() != null) {
			jsonObj.put(TITLE, itemData.getTitle());
		}

		if (itemData.getGeoLat() != null) {
			jsonObj.put(GEO_LAT, itemData.getGeoLat());
		}

		if (itemData.getGeoLong() != null) {
			jsonObj.put(GEO_LONG, itemData.getGeoLong());
		}

		if (itemData.getLink() != null) {
			jsonObj.put(LINK, itemData.getLink());
		}

		if (itemData.getDescription() != null) {
			jsonObj.put(DESCRIPTION, itemData.getDescription());
		}

		if (itemData.getPubDate() != null) {
			jsonObj.put(PUBDATE, ConversionsUtil
					.convertRfc822DateToString(itemData.getPubDate()));
		}

		if (itemData.getConditionData() != null) {
			jsonObj.put(CONDITION, itemData.getConditionData().toJson());
		}

		if (itemData.getForecastData() != null
				&& itemData.getForecastData().length > 0) {
			JSONArray jsonArray = new JSONArray();
			for (ForecastData item : itemData.getForecastData()) {
				jsonArray.put(ForecastDataParser.toJson(item));
			}
			jsonObj.put(FORECAST, jsonArray);
		}

		return jsonObj;
	}
}
