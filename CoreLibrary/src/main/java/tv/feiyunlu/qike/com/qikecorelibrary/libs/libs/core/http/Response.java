package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

import java.util.HashMap;

/**
 * Created by zhulingjun on 14-3-20.
 */
public class Response<T> {
    protected T result;
    protected int mStatus;
    private long mContentLength;
    
    private HashMap<String, String> mHeaders;
    
    public HashMap<String, String> getHeaders(){
    	return mHeaders;
    }
    
    public void setHeaders(HashMap<String, String> headers){
    	mHeaders = headers;
    }
    
    
    
    
    

    public long getContentLength() {
		return mContentLength;
	}

	public void setContentLength(long contengLength) {
		this.mContentLength = contengLength;
	}

	public int getmStatus() {
		return mStatus;
	}

	public void setmStatus(int mStatus) {
		this.mStatus = mStatus;
	}

	public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
