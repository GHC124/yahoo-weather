package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.UnitsDataParser;

/**
 * Unit Data
 * 
 * @author ChungPV1
 * 
 */
public class UnitsData implements YWData {
	private String mTemperature;
	private String mDistance;
	private String mPressure;
	private String mSpeed;

	public String getTemperature() {
		return mTemperature;
	}

	public void setTemperature(String temperature) {
		mTemperature = temperature;
	}

	public String getDistance() {
		return mDistance;
	}

	public void setDistance(String distance) {
		mDistance = distance;
	}

	public String getPressure() {
		return mPressure;
	}

	public void setPressure(String pressure) {
		mPressure = pressure;
	}

	public String getSpeed() {
		return mSpeed;
	}

	public void setSpeed(String speed) {
		mSpeed = speed;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return UnitsDataParser.toJson(this);
	}

}
