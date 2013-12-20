package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.PlaceDataParser;

public class PlaceData implements YWData {
	private String mName;
	private String mCountry;
	private String mWoeid;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String country) {
		mCountry = country;
	}

	public String getWoeid() {
		return mWoeid;
	}

	public void setWoeid(String woeid) {
		mWoeid = woeid;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return PlaceDataParser.toJson(this);
	}

}
