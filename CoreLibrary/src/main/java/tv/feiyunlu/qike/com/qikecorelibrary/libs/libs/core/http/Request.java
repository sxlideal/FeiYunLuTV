package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface Request<T> {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;

	public Response<T> convertResponse(HttpResponse httpResponse)
			throws Exception;

	public String getURL();

	public Map<String, Object> getParams();

	public Map<String, String> getHeaders();

	public void setURL(String url);

	public void setParams(Map<String, Object> params);

	public void setHeaders(Map<String, String> headers);

	public int getMethod();

	public void setMethod(int method);

	public boolean isMultipart();

	public void setMultipart(boolean multipart);

	public void cancel();

	public HttpUriRequest getRequest();

	public void setRequest(HttpUriRequest mRequest);

	public void setUploadFileListener(UploadFileListener fileListener);

	public UploadFileListener getUploadFileListener();

	public static interface UploadFileListener {
		public void onProgress(long progress, long length);
	}

}
