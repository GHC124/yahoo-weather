package weather.common.yahoo;

import weather.common.yahoo.domain.YWResponse;

public class YWLoaderResult {
	private final YWResponse mResponse;
	private final Exception mFailureReason;
	private final boolean mSuccess;

	private YWLoaderResult(YWResponse response, Exception failureReason,
			boolean success) {
		mResponse = response;
		mFailureReason = failureReason;
		mSuccess = success;
	}

	public YWResponse getResponse() {
		return mResponse;
	}

	public Exception getFailureReason() {
		return mFailureReason;
	}

	public boolean isSuccessful() {
		return mSuccess;
	}
	
	public static YWLoaderResult createSuccessful(YWResponse response) {
		return new YWLoaderResult(response, null, true);
	}

	public static YWLoaderResult createFailed(Exception failureReason) {
		return createFailed(null, failureReason);
	}

	public static YWLoaderResult createFailed(YWResponse response) {
		return createFailed(response, null);
	}

	public static YWLoaderResult createFailed(YWResponse response, Exception failureReason) {
		return new YWLoaderResult(response, failureReason, false);
	}
}
