package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.ImageDataParser;

public class ImageData implements YWData {
	private String mTitle;
	private String mWidth;
	private String mHeight;
	private String mLink;
	private String mUrl;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getWidth() {
		return mWidth;
	}

	public void setWidth(String width) {
		mWidth = width;
	}

	public String getHeight() {
		return mHeight;
	}

	public void setHeight(String height) {
		mHeight = height;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return ImageDataParser.toJson(this);
	}

}
