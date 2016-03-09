package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpUtil;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;


/**
 * Created by zhulingjun on 14-3-20.
 */
public class ByteRequest extends AbstractRequest<byte[]> {

	public ByteRequest(int method, String url, Map<String, Object> params,
			Map<String, String> headers) {
		super(method, url, params, headers);
	}

	public ByteRequest(String url, Map<String, Object> params,
			Map<String, String> headers) {
		super(url, params, headers);
	}

	public ByteRequest(String url, Map<String, Object> params) {
		super(url, params);
	}

	public ByteRequest(String url) {
		super(url);
	}

	@Override
	public Response<byte[]> convertResponse(HttpResponse httpResponse)
			throws Exception {
		Response<byte[]> response = new Response<byte[]>();
		response.setmStatus(httpResponse.getStatusLine().getStatusCode());
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			InputStream in = entity.getContent();
			byte[] bytes = HttpUtil.covertInputstream2Bytes(in);
			response.setResult(bytes);
		}

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
