package weather.common.yahoo.domain;

import weather.common.yahoo.domain.data.PlacesData;

public class PlacesResponse extends YWResponse {
	private PlacesData mPlaceData;

	public PlacesData getPlaceData() {
		return mPlaceData;
	}

	public void setPlaceData(PlacesData placeData) {
		mPlaceData = placeData;
	}

}
