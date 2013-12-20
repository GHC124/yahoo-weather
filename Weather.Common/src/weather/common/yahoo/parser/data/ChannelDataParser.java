package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.AstronomyData;
import weather.common.yahoo.domain.data.AtmosphereData;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.domain.data.ImageData;
import weather.common.yahoo.domain.data.ItemData;
import weather.common.yahoo.domain.data.LocationData;
import weather.common.yahoo.domain.data.UnitsData;
import weather.common.yahoo.domain.data.WindData;
import weather.common.yahoo.parser.YWResponseParser;

public class ChannelDataParser extends YWResponseParser {
	private static final String TITLE = "title";
	private static final String LINK = "link";
	private static final String DESCRIPTION = "description";
	private static final String LANGUAGE = "lauguage";
	private static final String LOCATION = "location";
	private static final String UNITS = "units";
	private static final String WIND = "wind";
	private static final String ATMOSPHERE = "atmosphere";
	private static final String ASTRONOMY = "astronomy";
	private static final String IMAGE = "image";
	private static final String ITEM = "item";

	public static ChannelData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		ChannelData channelData = new ChannelData();

		if (jsonObj.has(TITLE)) {
			channelData.setTitle(jsonObj.getString(TITLE));
		}

		if (jsonObj.has(LINK)) {
			channelData.setLink(jsonObj.getString(LINK));
		}

		if (jsonObj.has(DESCRIPTION)) {
			channelData.setDescription(jsonObj.getString(DESCRIPTION));
		}

		if (jsonObj.has(LANGUAGE)) {
			channelData.setLanguage(jsonObj.getString(LANGUAGE));
		}

		if (jsonObj.has(LOCATION)) {
			LocationData locationData = LocationDataParser.parse(jsonObj
					.getJSONObject(LOCATION));
			channelData.setLocationData(locationData);
		}
		if (jsonObj.has(UNITS)) {
			UnitsData unitsData = UnitsDataParser.parse(jsonObj
					.getJSONObject(UNITS));
			channelData.setUnitsData(unitsData);
		}
		if (jsonObj.has(WIND)) {
			WindData windData = WindDataParser.parse(jsonObj
					.getJSONObject(WIND));
			channelData.setWindData(windData);
		}
		if (jsonObj.has(ATMOSPHERE)) {
			AtmosphereData atmosphereData = AtmosphereDataParser.parse(jsonObj
					.getJSONObject(ATMOSPHERE));
			channelData.setAtmosphereData(atmosphereData);
		}
		if (jsonObj.has(ASTRONOMY)) {
			AstronomyData astronomyData = AstronomyDataParser.parse(jsonObj
					.getJSONObject(ASTRONOMY));
			channelData.setAstronomyData(astronomyData);
		}
		if (jsonObj.has(IMAGE)) {
			ImageData imageData = ImageDataParser.parse(jsonObj
					.getJSONObject(IMAGE));
			channelData.setImageData(imageData);
		}
		if (jsonObj.has(ITEM)) {
			ItemData itemData = ItemDataParser.parse(jsonObj
					.getJSONObject(ITEM));
			channelData.setItemData(itemData);
		}
		return channelData;
	}

	public static JSONObject toJson(ChannelData channelData)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (channelData.getTitle() != null) {
			jsonObj.put(TITLE, channelData.getTitle());
		}

		if (channelData.getLink() != null) {
			jsonObj.put(LINK, channelData.getLink());
		}

		if (channelData.getDescription() != null) {
			jsonObj.put(DESCRIPTION, channelData.getDescription());
		}

		if (channelData.getLanguage() != null) {
			jsonObj.put(LANGUAGE, channelData.getLanguage());
		}

		if (channelData.getLocationData() != null) {
			jsonObj.put(LOCATION, channelData.getLocationData().toJson());
		}

		if (channelData.getUnitsData() != null) {
			jsonObj.put(UNITS, channelData.getUnitsData().toJson());
		}

		if (channelData.getWindData() != null) {
			jsonObj.put(WIND, channelData.getWindData().toJson());
		}

		if (channelData.getAtmosphereData() != null) {
			jsonObj.put(ATMOSPHERE, channelData.getAtmosphereData().toJson());
		}

		if (channelData.getAstronomyData() != null) {
			jsonObj.put(ASTRONOMY, channelData.getAstronomyData().toJson());
		}

		if (channelData.getImageData() != null) {
			jsonObj.put(IMAGE, channelData.getImageData().toJson());
		}

		if (channelData.getItemData() != null) {
			jsonObj.put(ITEM, channelData.getItemData().toJson());
		}
		return jsonObj;
	}
}
