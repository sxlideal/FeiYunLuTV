package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;


public class StreamRequest extends AbstractRequest<HttpEntity> {

	public StreamRequest(int method, String url, Map<String, Object> params,
			Map<String, String> headers) {
		super(method, url, params, headers);
	}

	public StreamRequest(String url, Map<String, Object> params,
			Map<String, String> headers) {
		super(url, params, headers);
	}

	public StreamRequest(String url, Map<String, Object> params) {
		super(url, params);
	}

	public StreamRequest(String url) {
		super(url);
	}

	@Override
	public Response<HttpEntity> convertResponse(HttpResponse httpResponse)
			throws IOException {
		Response<HttpEntity> response = new Response<HttpEntity>();
		response.setResult(httpResponse.getEntity());
		response.setmStatus(httpResponse.getStatusLine().getStatusCode());
		Header[] headers = httpResponse.getAllHeaders();
		HashMap<String, String> headerMaps = new HashMap<String, String>();
		if (headers != null) {
			for (Header header : headers) {
				headerMaps.put(header.getName(), String.valueOf(header.getValue()));
			}
		}
		response.setHeaders(headerMaps);
		return response;
	}

}
