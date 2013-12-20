package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.ChannelDataParser;

public class ChannelData implements YWData {
	private String mTitle;
	private String mLink;
	private String mLanguage;
	private String mDescription;
	private LocationData mLocationData;
	private UnitsData mUnitsData;
	private WindData mWindData;
	private AtmosphereData mAtmosphereData;
	private AstronomyData mAstronomyData;
	private ImageData mImageData;
	private ItemData mItemData;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getLanguage() {
		return mLanguage;
	}

	public void setLanguage(String language) {
		mLanguage = language;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public LocationData getLocationData() {
		return mLocationData;
	}

	public void setLocationData(LocationData locationData) {
		mLocationData = locationData;
	}

	public UnitsData getUnitsData() {
		return mUnitsData;
	}

	public void setUnitsData(UnitsData unitsData) {
		mUnitsData = unitsData;
	}

	public WindData getWindData() {
		return mWindData;
	}

	public void setWindData(WindData windData) {
		mWindData = windData;
	}

	public AtmosphereData getAtmosphereData() {
		return mAtmosphereData;
	}

	public void setAtmosphereData(AtmosphereData atmosphereData) {
		mAtmosphereData = atmosphereData;
	}

	public AstronomyData getAstronomyData() {
		return mAstronomyData;
	}

	public void setAstronomyData(AstronomyData astronomyData) {
		mAstronomyData = astronomyData;
	}

	public ImageData getImageData() {
		return mImageData;
	}

	public void setImageData(ImageData imageData) {
		mImageData = imageData;
	}

	public ItemData getItemData() {
		return mItemData;
	}

	public void setItemData(ItemData itemData) {
		mItemData = itemData;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return ChannelDataParser.toJson(this);
	}

}
