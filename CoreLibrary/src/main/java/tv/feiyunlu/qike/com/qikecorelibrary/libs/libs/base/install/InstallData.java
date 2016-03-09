package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

public class InstallData {
	private InstallStatus mStatus;

	private InstallTask mTask;

	private int mError;

	private InstallRequest mData;

	private long mProgress;

	private long mLength;

	private String mSize;
	private int mPercent;

	public String getSize() {
		return mSize;
	}

	public void setSize(String mSize) {
		this.mSize = mSize;
	}

	public int getPercent() {
		return mPercent;
	}

	public void setPercent(int mPercent) {
		this.mPercent = mPercent;
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

	public InstallRequest getData() {
		return mData;
	}

	public void setData(InstallRequest mData) {
		this.mData = mData;
	}

	public int getError() {
		return mError;
	}

	public void setError(int mError) {
		this.mError = mError;
	}

	public InstallTask getTask() {
		return mTask;
	}

	public void setTask(InstallTask mTask) {
		this.mTask = mTask;
	}

	public void setStatus(InstallStatus status) {
		mStatus = status;
	}

	public InstallStatus getStatus() {
		return mStatus;
	}
}
