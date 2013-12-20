package weather.common.yahoo.domain.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base data for Yahoo Weather data
 * 
 * @author ChungPV1
 * 
 */
public interface YWData {
	public JSONObject toJson() throws JSONException;
}
