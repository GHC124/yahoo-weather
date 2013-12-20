package weather.common.yahoo.parser;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import weather.common.yahoo.domain.ChannelResponse;
import weather.common.yahoo.domain.YWResponse;
import weather.common.yahoo.domain.data.ChannelData;
import weather.common.yahoo.parser.data.ChannelDataParser;

public class ForecastResponseParser extends YWResponseParser {
	private static final String QUERY = "query";
	private static final String RESULTS = "results";
	private static final String CHANNEL = "channel";

	@Override
	public YWResponse parse(String resultString) throws JSONException,
			ParseException {
		JSONObject jsonObj = new JSONObject(resultString);

		// Get channel object
		if (jsonObj.has(QUERY)) {
			jsonObj = jsonObj.getJSONObject(QUERY);
			if (jsonObj.has(RESULTS)) {
				jsonObj = jsonObj.getJSONObject(RESULTS);
				if (jsonObj.has(CHANNEL)) {
					jsonObj = jsonObj.getJSONObject(CHANNEL);
				} else {
					throw new JSONException("JSON syntax error!");
				}
			} else {
				throw new JSONException("JSON syntax error!");
			}
		} else {
			throw new JSONException("JSON syntax error!");
		}

		ChannelData channelData = ChannelDataParser.parse(jsonObj);

		ChannelResponse channelResponse = new ChannelResponse();
		channelResponse.setChannelData(channelData);

		return channelResponse;
	}
}
