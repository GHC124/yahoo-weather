package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.LocationDataParser;

public class LocationData implements YWData {
	private String mCity;
	private String mRegion;
	private String mCountry;

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		this.mCity = city;
	}

	public String getRegion() {
		return mRegion;
	}

	public void setRegion(String region) {
		this.mRegion = region;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String country) {
		this.mCountry = country;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return LocationDataParser.toJson(this);
	}

}
