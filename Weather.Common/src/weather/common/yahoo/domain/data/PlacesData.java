package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.PlacesDataParser;

public class PlacesData implements YWData {
	private PlaceData[] mPlaceData;

	public PlaceData[] getPlaceData() {
		return mPlaceData;
	}

	public void setPlaceData(PlaceData[] placeData) {
		this.mPlaceData = placeData;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return PlacesDataParser.toJson(this);
	}

}
