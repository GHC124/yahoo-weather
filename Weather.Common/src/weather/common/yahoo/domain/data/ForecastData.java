package weather.common.yahoo.domain.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.ForecastDataParser;

public class ForecastData implements YWData {
	private String mDay;
	private Date mDate;
	private Integer mLow;
	private Integer mHigh;
	private String mText;
	private Integer mCode;

	public String getDay() {
		return mDay;
	}

	public void setDay(String day) {
		mDay = day;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public Integer getLow() {
		return mLow;
	}

	public void setLow(Integer low) {
		mLow = low;
	}

	public Integer getHigh() {
		return mHigh;
	}

	public void setHigh(Integer high) {
		mHigh = high;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public Integer getCode() {
		return mCode;
	}

	public void setCode(Integer code) {
		mCode = code;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return ForecastDataParser.toJson(this);
	}

}
