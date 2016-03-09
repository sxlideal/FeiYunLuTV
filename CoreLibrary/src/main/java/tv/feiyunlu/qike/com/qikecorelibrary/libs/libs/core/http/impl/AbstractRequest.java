package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl;

import org.apache.http.client.methods.HttpUriRequest;

import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Request;


abstract public class AbstractRequest<T> implements Request<T> {

	private String mUrl;
	private Map<String, Object> mParams;
	private Map<String, String> mHeaders;
	private int mMethod;
	private boolean mMultipart;
	private HttpUriRequest mRequest;
	private UploadFileListener mListener;

	@Override
	public HttpUriRequest getRequest() {
		return mRequest;
	}

	@Override
	public void setRequest(HttpUriRequest mRequest) {
		this.mRequest = mRequest;
	}

	public boolean isMultipart() {
		return mMultipart;
	}

	public void setMultipart(boolean multipart) {
		this.mMultipart = multipart;
	}

	public AbstractRequest(String url, Map<String, Object> params,
			Map<String, String> headers) {
		this(Request.METHOD_GET, url, params, headers);
	}

	public AbstractRequest(String url, Map<String, Object> params) {
		this(Request.METHOD_GET, url, params, null);
	}

	public AbstractRequest(String url) {
		this(Request.METHOD_GET, url, null, null);
	}

	public AbstractRequest(int method, String url, Map<String, Object> params,
			Map<String, String> headers) {
		mMethod = method;
		mUrl = url;
		mParams = params;
		mHeaders = headers;
	}

	@Override
	public String getURL() {
		return mUrl;
	}

	@Override
	public Map<String, Object> getParams() {
		return mParams;
	}

	@Override
	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	@Override
	public void setURL(String url) {
		mUrl = url;
	}

	@Override
	public void setParams(Map<String, Object> params) {
		mParams = params;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		mHeaders = headers;
	}

	@Override
	public int getMethod() {
		return mMethod;
	}

	@Override
	public void setMethod(int method) {
		mMethod = method;
	}

	@Override
	public void cancel() {
		if (mRequest != null) {
			mRequest.abort();
		}
	}

	@Override
	public void setUploadFileListener(UploadFileListener fileListener) {
		mListener = fileListener;
	}

	@Override
	public UploadFileListener getUploadFileListener() {
		return mListener;
	}

	
	
}
