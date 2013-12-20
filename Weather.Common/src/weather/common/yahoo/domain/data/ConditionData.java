package weather.common.yahoo.domain.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.ConditionDataParser;

public class ConditionData implements YWData {
	private String mText;
	private Integer mCode;
	private Integer mTemp;
	private Date mDate;

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

	public Integer getTemp() {
		return mTemp;
	}

	public void setTemp(Integer temp) {
		mTemp = temp;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return ConditionDataParser.toJson(this);
	}

}
