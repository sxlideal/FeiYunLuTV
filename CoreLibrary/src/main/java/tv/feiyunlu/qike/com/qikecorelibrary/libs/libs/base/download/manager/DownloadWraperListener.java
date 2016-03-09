package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.TaskStatus;

public interface DownloadWraperListener {
	public void onStateChanged(TaskStatus status);

	public void onProgress(TaskStatus status);

	public void onError(TaskStatus status);
	
	public void onDataChanged();
	
	//	public void onDriveChanged(ManagerData bean);
	//	public void onUrlLoaded(ManagerData bean);
	
	
	public void onVCodeCall(String callback, StringBuilder vcode);
}
