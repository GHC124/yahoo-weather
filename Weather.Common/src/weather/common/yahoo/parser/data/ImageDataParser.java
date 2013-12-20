package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.ImageData;
import weather.common.yahoo.parser.YWResponseParser;

public class ImageDataParser extends YWResponseParser {
	private static final String TITLE = "title";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String LINK = "link";
	private static final String URL = "url";

	public static ImageData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		ImageData imageData = new ImageData();

		if (jsonObj.has(TITLE)) {
			imageData.setTitle(jsonObj.getString(TITLE));
		}

		if (jsonObj.has(WIDTH)) {
			imageData.setWidth(jsonObj.getString(WIDTH));
		}

		if (jsonObj.has(HEIGHT)) {
			imageData.setHeight(jsonObj.getString(HEIGHT));
		}

		if (jsonObj.has(LINK)) {
			imageData.setLink(jsonObj.getString(LINK));
		}

		if (jsonObj.has(URL)) {
			imageData.setUrl(jsonObj.getString(URL));
		}

		return imageData;
	}

	public static JSONObject toJson(ImageData imageData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (imageData.getTitle() != null) {
			jsonObj.put(TITLE, imageData.getTitle());
		}

		if (imageData.getWidth() != null) {
			jsonObj.put(WIDTH, imageData.getWidth());
		}

		if (imageData.getHeight() != null) {
			jsonObj.put(HEIGHT, imageData.getHeight());
		}

		if (imageData.getLink() != null) {
			jsonObj.put(LINK, imageData.getLink());
		}

		if (imageData.getUrl() != null) {
			jsonObj.put(URL, imageData.getUrl());
		}

		return jsonObj;
	}
}
