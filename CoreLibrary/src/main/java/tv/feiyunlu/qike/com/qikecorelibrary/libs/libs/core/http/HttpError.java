package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

/**
 * Created by zhulingjun on 14-3-20.
 */
public class HttpError extends Exception {

	public static final int ERR_STATUS = -8001;
	public static final int ERR_METHOD = -8002;
	public static final int ERR_NORESPONSE = -8003;

	private static final long serialVersionUID = 3450132538041126083L;
	private int mStatus;
	private int mError;
	private String mErrorMsg;

	public String getErrorMsg() {
		return mErrorMsg;
	}

	public int getStatus() {
		return mStatus;
	}

	public int getError() {
		return mError;
	}

	public HttpError(int error, String errorMsg) {
		mError = error;
		mErrorMsg = errorMsg;
	}

	public HttpError(int error, int status) {
		mError = error;
		mStatus = status;
	}
}
