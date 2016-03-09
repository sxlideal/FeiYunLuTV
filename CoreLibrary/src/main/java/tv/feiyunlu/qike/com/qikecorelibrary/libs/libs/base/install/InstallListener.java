package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;


public interface InstallListener<T> {
	public void onSuccess(T bean, Boolean silent);
	public void onError(Integer code, Throwable ex, T bean);
	public void onProgress(Long progress, Long totalSize, T bean);
	public void verifyComplete(T bean);
	public void onPrepare(Boolean silent, T bean);
	public void onCancel(T bean);
	public void onLoadAttributes(T bean, InstallManifest mainifest);
	public boolean continueProcess();
	public void onInstallApk(T bean);
	public void notifyData();
	public void onLaunchSystemInstall(T bean, String apkPath);
	
}
