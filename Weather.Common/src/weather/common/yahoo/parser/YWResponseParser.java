package weather.common.yahoo.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.json.JSONException;

import weather.common.util.ConversionsUtil;
import weather.common.yahoo.domain.YWResponse;

public class YWResponseParser {
	public YWResponse parse(InputStream is) throws JSONException, ParseException, IOException {
		String resultString = ConversionsUtil.convertStreamToString(is);
		return parse(resultString);
	}

	public YWResponse parse(String resultString) throws JSONException, ParseException {
		YWResponse response = new YWResponse();
		response.setJsonData(resultString);
		return response;
	}
}
