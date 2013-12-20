package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.parser.data.WindDataParser;

public class WindData implements YWData {
	private String mChild;
	private String mDirection;
	private String mSpeed;

	public String getChild() {
		return mChild;
	}

	public void setChild(String child) {
		mChild = child;
	}

	public String getDirection() {
		return mDirection;
	}

	public void setDirection(String direction) {
		mDirection = direction;
	}

	public String getSpeed() {
		return mSpeed;
	}

	public void setSpeed(String speed) {
		mSpeed = speed;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		return WindDataParser.toJson(this);
	}

}
