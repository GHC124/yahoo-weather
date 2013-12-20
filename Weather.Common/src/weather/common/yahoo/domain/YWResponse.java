package weather.common.yahoo.domain;
/**
 * Response from Yahoo Weather API
 * 
 * @author ChungPV1
 *
 */
public class YWResponse {
	private String mUrl;
	private String mJsonData;
	
	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public String getJsonData() {
		return mJsonData;
	}

	public void setJsonData(String jsonData) {
		this.mJsonData = jsonData;
	}
}
