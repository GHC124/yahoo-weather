package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.PhotosDataParser;

public class PhotosData implements YWData {
	private PhotoData[] mPhotoData;

	public PhotoData[] getPhotoData() {
		return mPhotoData;
	}

	public void setPhotoData(PhotoData[] placeData) {
		this.mPhotoData = placeData;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return PhotosDataParser.toJson(this);
	}

}
