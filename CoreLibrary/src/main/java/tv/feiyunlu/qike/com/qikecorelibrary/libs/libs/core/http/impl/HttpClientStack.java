package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpError;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpStack;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpUtil;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Request;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.Logger;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.LoggerConfig;


/**
 * Created by zhulingjun on 14-3-19.
 */
@LoggerConfig(tag = "mzw_tag")
public class HttpClientStack implements HttpStack {

	private HttpClientStack() {
	}

	private static HttpClientStack INSTANCE;
	private static DefaultHttpClient CLIENT = null;

	public static synchronized HttpClientStack getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HttpClientStack();
		}
		return INSTANCE;
	}

	@Override
	public <T> Response<T> performRequest(Request<T> request) throws Exception {
		HttpResponse response = null;
		switch (request.getMethod()) {
		case Request.METHOD_GET:
			response = get(request);
			break;
		case Request.METHOD_POST:
			response = post(request);
			break;
		default:
			throw new HttpError(HttpError.ERR_METHOD, -1);
		}

		if (response == null) {
			throw new HttpError(HttpError.ERR_NORESPONSE, -1);
		}

		StatusLine statusLine = response.getStatusLine();
		int status = statusLine.getStatusCode();
		if (status < 200 || status >= 400) {
			throw new HttpError(HttpError.ERR_STATUS, response.getStatusLine()
					.getStatusCode());
		}

		return request.convertResponse(response);
	}

	/**
	 * HTTP-POST-表单提交
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private <T> HttpResponse post(Request<T> request) throws Exception {
		HttpPost httpPost = new HttpPost(request.getURL());
		Log.e("test", request.getURL());
		HttpEntity entity = null;
		Map<String, Object> params = request.getParams();
		Map<String, String> headers = request.getHeaders();
		
		if (HttpUtil.isUpload(params)) {
			entity = HttpUtil.convertUploadEntity(params,
					request.getUploadFileListener());
		} else {
			entity = HttpUtil.covertMap2HttpEntity(params,
					request.isMultipart());
		}
		request.setRequest(httpPost);
		httpPost.setEntity(entity);
		return httpDo(httpPost, headers);
	}

	/**
	 * HTTP-GET-表单提交
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private <T> HttpResponse get(Request<T> request) throws Exception {
		String paramString = HttpUtil.convertParams(request.getParams());
		String url = request.getURL();
		HttpGet httpGet = new HttpGet(TextUtils.isEmpty(paramString) ? url
				: url + "?" + paramString);
		request.setRequest(httpGet);

		Logger.getLogger(this).i("url:" + httpGet.getURI());
		Log.e("test", httpGet.getURI().toString());
		return httpDo(httpGet, request.getHeaders());
	}

	private HttpResponse httpDo(HttpUriRequest request,
			Map<String, String> headers) throws IOException {
		CLIENT = getClient();
		if (headers != null) {
			for (String key : headers.keySet()) {
				request.addHeader(key, headers.get(key));
			}
		}
		HttpResponse response = CLIENT.execute(request);
		return response;
	}

	private static synchronized DefaultHttpClient getClient() {
		if (CLIENT == null) {
			CLIENT = HttpUtil.getClient(3,30*1000);
		}
		return CLIENT;
	}

}
