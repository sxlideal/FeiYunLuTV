package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.DownloadStatus;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.Downloadable;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread.DownloadTask;

public class TaskStatus {

	private long mProgress;
	private long mLength;
	private long mSpeed;
	private int mPercent;
	private long mUseTime;
	private long mStartTime;
	private long mErrorCode;
	private Downloadable mItem;
	private DownloadStatus mStatus = DownloadStatus.STOPED;

	private DownloadTask<TaskStatus> mTask;
	
	
	
	public TaskStatus(Downloadable mItem,DownloadStatus status){
		this.mItem = mItem;
		this.mStatus = status;
	}
	
	

	public long getProgress() {
		return mProgress;
	}

	public void setProgress(long mProgress) {
		this.mProgress = mProgress;
	}

	public long getLength() {
		return mLength;
	}

	public void setLength(long mLength) {
		this.mLength = mLength;
	}

	public long getSpeed() {
		return mSpeed;
	}

	public void setSpeed(long mSpeed) {
		this.mSpeed = mSpeed;
	}

	public int getPercent() {
		return mPercent;
	}

	public void setPercent(int mPercent) {
		this.mPercent = mPercent;
	}

	public long getUseTime() {
		return mUseTime;
	}

	public void setUseTime(long mUseTime) {
		this.mUseTime = mUseTime;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public void setStartTime(long mStartTime) {
		this.mStartTime = mStartTime;
	}

	public long getErrorCode() {
		return mErrorCode;
	}

	public void setErrorCode(long mErrorCode) {
		this.mErrorCode = mErrorCode;
	}

	public DownloadTask<TaskStatus> getTask() {
		return mTask;
	}

	public void setTask(DownloadTask<TaskStatus> mTask) {
		this.mTask = mTask;
	}
	
	
	public Downloadable getItem(){
		return mItem;
	}
	
	public void setItem(Downloadable item){
		this.mItem = item;
	}

	public DownloadStatus getStatus() {
		return mStatus;
	}

	public void setStatus(DownloadStatus mStatus) {
		this.mStatus = mStatus;
	}
	
	

}
