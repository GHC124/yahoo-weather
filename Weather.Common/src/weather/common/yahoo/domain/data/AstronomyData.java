package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.AstronomyDataParser;

public class AstronomyData implements YWData {
	private String mSunrise;
	private String mSunset;

	public String getSunrise() {
		return mSunrise;
	}

	public void setSunrise(String sunrise) {
		mSunrise = sunrise;
	}

	public String getSunset() {
		return mSunset;
	}

	public void setSunset(String sunset) {
		mSunset = sunset;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return AstronomyDataParser.toJson(this);
	}

}
