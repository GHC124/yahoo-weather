package weather.common.yahoo.domain.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.ItemDataParser;

public class ItemData implements YWData {
	private String mTitle;
	private String mGeoLat;
	private String mGeoLong;
	private String mLink;
	private Date mPubDate;
	private String mDescription;
	private ConditionData mConditionData;
	private ForecastData[] mForecastData;
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getGeoLat() {
		return mGeoLat;
	}

	public void setGeoLat(String geoLat) {
		mGeoLat = geoLat;
	}

	public String getGeoLong() {
		return mGeoLong;
	}

	public void setGeoLong(String geoLong) {
		mGeoLong = geoLong;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public Date getPubDate() {
		return mPubDate;
	}

	public void setPubDate(Date pubDate) {
		mPubDate = pubDate;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public ConditionData getConditionData() {
		return mConditionData;
	}

	public void setConditionData(ConditionData conditionData) {
		mConditionData = conditionData;
	}

	public ForecastData[] getForecastData() {
		return mForecastData;
	}

	public void setForecastData(ForecastData[] forecastData) {
		mForecastData = forecastData;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return ItemDataParser.toJson(this);
	}

}
