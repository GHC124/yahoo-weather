package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.AtmosphereDataParser;

public class AtmosphereData implements YWData {
	private String mHumidity;
	private String mVisibility;
	private String mPressure;
	private String mRising;

	public String getHumidity() {
		return mHumidity;
	}

	public void setHumidity(String humidity) {
		mHumidity = humidity;
	}

	public String getVisibility() {
		return mVisibility;
	}

	public void setVisibility(String visibility) {
		mVisibility = visibility;
	}

	public String getPressure() {
		return mPressure;
	}

	public void setPressure(String pressure) {
		mPressure = pressure;
	}

	public String getRising() {
		return mRising;
	}

	public void setRising(String rising) {
		mRising = rising;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return AtmosphereDataParser.toJson(this);
	}
}
