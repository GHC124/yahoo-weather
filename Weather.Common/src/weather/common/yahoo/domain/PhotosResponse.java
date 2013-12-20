package weather.common.yahoo.domain;

import weather.common.yahoo.domain.data.PhotosData;

public class PhotosResponse extends YWResponse {
	private PhotosData mPhotosData;

	public PhotosData getPhotosData() {
		return mPhotosData;
	}

	public void setPhotosData(PhotosData channelData) {
		mPhotosData = channelData;
	}

}
