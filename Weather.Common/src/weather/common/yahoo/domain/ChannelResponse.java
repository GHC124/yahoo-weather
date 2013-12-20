package weather.common.yahoo.domain;

import weather.common.yahoo.domain.data.ChannelData;

public class ChannelResponse extends YWResponse {
	private ChannelData mChannelData;

	public ChannelData getChannelData() {
		return mChannelData;
	}

	public void setChannelData(ChannelData channelData) {
		mChannelData = channelData;
	}

}
