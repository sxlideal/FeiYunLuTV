package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

public class InstallRequest {

	//包名
	private String mPackageName;
	//文件路径
	private String mFilePath;
	//是否进行校验
	private boolean mVerify;
	//是否静默安装
	private boolean mSilent;
	//唯一标记一个安装任务
	private Object mKey;

	private Object mItem;

	public Object getItem() {
		return mItem;
	}

	public void setItem(Object mItem) {
		this.mItem = mItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mKey == null) ? 0 : mKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstallRequest other = (InstallRequest) obj;
		if (mKey == null) {
			if (other.mKey != null)
				return false;
		} else if (!mKey.equals(other.mKey))
			return false;
		return true;
	}

	public Object getKey() {
		return mKey;
	}

	public void setKey(Object mKey) {
		this.mKey = mKey;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}

	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}

	public boolean isVerify() {
		return mVerify;
	}

	public void setVerify(boolean mVerify) {
		this.mVerify = mVerify;
	}

	public boolean isSilent() {
		return mSilent;
	}

	public void setSilent(boolean mSilent) {
		this.mSilent = mSilent;
	}

}
