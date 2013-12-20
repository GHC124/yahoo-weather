package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.PhotoData;
import weather.common.yahoo.domain.data.PhotosData;
import weather.common.yahoo.parser.YWResponseParser;

public class PhotosDataParser extends YWResponseParser {
	private static final String PHOTO = "photo";

	public static PhotosData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		PhotosData photosData = new PhotosData();

		if (jsonObj.has(PHOTO)) {
			JSONArray jsonArray = jsonObj.getJSONArray(PHOTO);
			int length = jsonArray.length();
			PhotoData[] photoArray = new PhotoData[length];
			for (int i = 0; i < length; i++) {
				JSONObject photoObj = jsonArray.getJSONObject(i);
				photoArray[i] = PhotoDataParser.parse(photoObj);
			}
			photosData.setPhotoData(photoArray);
		}

		return photosData;
	}

	public static JSONObject toJson(PhotosData photosData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (photosData.getPhotoData() != null) {
			JSONArray jsonArray = new JSONArray();
			for (PhotoData item : photosData.getPhotoData()) {
				jsonArray.put(PhotoDataParser.toJson(item));
			}
			jsonObj.put(PHOTO, jsonArray);
		}

		return jsonObj;
	}
}
