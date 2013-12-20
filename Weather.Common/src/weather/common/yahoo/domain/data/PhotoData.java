package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.PhotoDataParser;

public class PhotoData implements YWData {
	private String mFarm;
	private String mServer;
	private String mId;
	private String mSecret;

	public String getFarm() {
		return mFarm;
	}

	public void setFarm(String farm) {
		mFarm = farm;
	}

	public String getServer() {
		return mServer;
	}

	public void setServer(String server) {
		mServer = server;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getSecret() {
		return mSecret;
	}

	public void setSecret(String secret) {
		mSecret = secret;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return PhotoDataParser.toJson(this);
	}

}
