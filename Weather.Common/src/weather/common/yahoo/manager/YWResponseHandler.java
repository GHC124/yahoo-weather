package weather.common.yahoo.manager;

import weather.common.yahoo.YWLoaderResult;

public interface YWResponseHandler {
	void processResponse(YWLoaderResult loaderResult);
}
