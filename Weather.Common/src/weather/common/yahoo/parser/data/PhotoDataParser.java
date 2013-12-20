package weather.common.yahoo.parser.data;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.data.PhotoData;
import weather.common.yahoo.parser.YWResponseParser;

public class PhotoDataParser extends YWResponseParser {
	private static final String FARM = "farm";
	private static final String SERVER = "server";
	private static final String ID = "id";
	private static final String SECRET = "secret";

	public static PhotoData parse(JSONObject jsonObj) throws JSONException,
			ParseException {
		PhotoData photoData = new PhotoData();

		if (jsonObj.has(FARM)) {
			photoData.setFarm(jsonObj.getString(FARM));
		}

		if (jsonObj.has(SERVER)) {
			photoData.setServer(jsonObj.getString(SERVER));
		}

		if (jsonObj.has(ID)) {
			photoData.setId(jsonObj.getString(ID));
		}

		if (jsonObj.has(SECRET)) {
			photoData.setSecret(jsonObj.getString(SECRET));
		}

		return photoData;
	}

	public static JSONObject toJson(PhotoData photoData) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		if (photoData.getFarm() != null) {
			jsonObj.put(FARM, photoData.getFarm());
		}

		if (photoData.getServer() != null) {
			jsonObj.put(SERVER, photoData.getServer());
		}

		if (photoData.getId() != null) {
			jsonObj.put(ID, photoData.getId());
		}

		if (photoData.getSecret() != null) {
			jsonObj.put(SECRET, photoData.getSecret());
		}

		return jsonObj;
	}
}
