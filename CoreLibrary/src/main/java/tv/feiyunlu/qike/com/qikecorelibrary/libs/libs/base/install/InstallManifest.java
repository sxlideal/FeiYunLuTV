package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import android.graphics.Bitmap;

public class InstallManifest {
	private String appName;
	private long appSize;
	private String packageName;
	private String versionName;
	private int sdkVersion;
	
	private String copyPath;
//	private long dataSize;
	private long apkCRC32;
//	private long dataCRC32;
	private String cpuType;
	private String screenDensity;

	private Bitmap icon;
	private String gpkVersion;

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    // 安装包是否损坏
    private boolean broken;
	
	
	public String getGpkVersion() {
		return gpkVersion;
	}

	public void setGpkVersion(String gpkVersion) {
		this.gpkVersion = gpkVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getAppSize() {
		return appSize;
	}

	public void setAppSize(long appSize) {
		this.appSize = appSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getCopyPath() {
		return copyPath;
	}

	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}

//	public long getDataSize() {
//		return dataSize;
//	}
//
//	public void setDataSize(long dataSize) {
//		this.dataSize = dataSize;
//	}

	public long getApkCRC32() {
		return apkCRC32;
	}

	public void setApkCRC32(long apkCRC32) {
		this.apkCRC32 = apkCRC32;
	}

//	public long getDataCRC32() {
//		return dataCRC32;
//	}
//
//	public void setDataCRC32(long dataCRC32) {
//		this.dataCRC32 = dataCRC32;
//	}

	public String getCpuType() {
		return cpuType;
	}

	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}

	public String getScreenDensity() {
		return screenDensity;
	}

	public void setScreenDensity(String screenDensity) {
		this.screenDensity = screenDensity;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public int getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(int sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	
}
