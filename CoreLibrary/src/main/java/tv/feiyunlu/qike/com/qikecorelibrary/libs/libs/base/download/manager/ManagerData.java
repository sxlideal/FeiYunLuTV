package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import android.os.PowerManager.WakeLock;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread.DownloadTask;


public class ManagerData {

	private DownloadTask<ManagerData> mTask;

	private String mSize;
	private String mPercent;
	private String mSpeed;
	private WakeLock mWakelock;
	private String mErrorMsg;
	private DownloadStatus mStatus;
	private Downloadable mData;
	private int mTaskType;
	
	private int mError;
	
	private int mProgress;
	
	
	
	
	
	
	
	
	

	public int getProgress() {
		return mProgress;
	}



	public void setProgress(int mProgress) {
		this.mProgress = mProgress;
	}



	public int getError() {
		return mError;
	}



	public void setError(int mError) {
		this.mError = mError;
	}



	public ManagerData(int mTaskType,DownloadStatus mStatus,Downloadable mData) {
		super();
		this.mStatus = mStatus;
		this.mData = mData;
		this.mTaskType = mTaskType;
	}
	
	

	public int getTaskType() {
		return mTaskType;
	}



	public void setTaskType(int mTaskType) {
		this.mTaskType = mTaskType;
	}



	public DownloadStatus getStatus() {
		return mStatus;
	}

	public void setStatus(DownloadStatus mStatus) {
		this.mStatus = mStatus;
	}

	public Downloadable getItem() {
		return mData;
	}

	public void setItem(Downloadable mData) {
		this.mData = mData;
	}

	public DownloadTask<ManagerData> getTask() {
		return mTask;
	}

	public void setTask(DownloadTask<ManagerData> mTask) {
		this.mTask = mTask;
	}

	public String getSize() {
		return mSize;
	}

	public void setSize(String mSize) {
		this.mSize = mSize;
	}

	public String getPercent() {
		return mPercent;
	}

	public void setPercent(String mPercent) {
		this.mPercent = mPercent;
	}

	public String getSpeed() {
		return mSpeed;
	}

	public void setSpeed(String mSpeed) {
		this.mSpeed = mSpeed;
	}

	public WakeLock getWakelock() {
		return mWakelock;
	}

	public void setWakelock(WakeLock mWakelock) {
		this.mWakelock = mWakelock;
	}

	public String getErrorMsg() {
		return mErrorMsg;
	}

	public void setErrorMsg(String mErrorMsg) {
		this.mErrorMsg = mErrorMsg;
	}

}
