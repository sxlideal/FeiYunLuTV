package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread;

public interface FileDownloadListener<T> {
	public void onPrepare(T data);
	public void onStarting(T data);
	public void onStarted(T data);
	public void onLoadContentLength(long contentLength, T data);
	public void onStoping(T data);
	public void onStopPrepare(T data);
	public void onStoped(T data);
	public void onErrorPrepare(T data);
	public void onProgressPrepare(long progress, long len, T data);
	public void onProgress(long progress, long len, T data);
	public void onCanceling(T data);
	public void onCancelPrepare(T data);
	public void onCanceled(T data);
	public void onError(Integer errorCode, Integer status, String ex, T data);
	public void onCompletePrepare(T data);
	public void onComplete(T data);
//	public void onDriveChange(T data);
//	public void onUrlLoaded(T data,String url);
	
	public void onShowVCode(T data, String vcode, StringBuilder result);
}
