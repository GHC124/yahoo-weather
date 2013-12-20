package weather.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class WebUtil {
	public static HttpResponse executeRequest(HttpUriRequest request,
			int timeout, HttpParams httpParams,
			ClientConnectionManager connManager) throws IOException {

		DefaultHttpClient httpClient;
		if (connManager != null) {
			httpClient = httpParams != null ? new DefaultHttpClient(
					connManager, httpParams) : new DefaultHttpClient(
					connManager, new DefaultHttpClient().getParams());
		} else {
			httpClient = httpParams != null ? new DefaultHttpClient(httpParams)
					: new DefaultHttpClient();
		}

		if (timeout >= 0) {
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					timeout);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
		}

		HttpResponse response = httpClient.execute(request);

		return response;
	}

	public static InputStream getInputStream(HttpResponse response)
			throws IOException {
		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		if (contentEncoding != null
				&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(response.getEntity().getContent());
		} else {
			return response.getEntity().getContent();
		}
	}

	public static String buildUrl(String url, List<NameValuePair> params,
			boolean encode) {
		StringBuilder sb = new StringBuilder(url);
		boolean first = true;
		try {
			for (NameValuePair param : params) {
				sb.append(first ? '?' : '&');
				String name = param.getName();
				sb.append(encode ? URLEncoder.encode(name, "UTF-8") : name);
				sb.append('=');
				String value = param.getValue();
				sb.append(encode ? URLEncoder.encode(param.getValue(), "UTF-8")
						: value);
				first = false;
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}
}
