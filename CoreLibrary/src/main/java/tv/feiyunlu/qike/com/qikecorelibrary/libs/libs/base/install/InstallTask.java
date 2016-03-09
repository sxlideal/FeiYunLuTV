package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.apken.ApkEN;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.impl.DatabaseProviderImpl;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.DirectoryUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.TaskListener;


@SuppressLint("DefaultLocale")
public class InstallTask extends Task<Void, Void, Integer> {

	private static final int POOLSIZE = 3;
	private static final String POOLKEY = InstallTask.class.getSimpleName();

	private String mPath;
	private String mTempPath;

	private boolean mVerify;
	private boolean mSilent;
	private InstallListener<Object> mListener;

	private boolean mInstallSdcard;
	private String mPackageName;

	private AtomicBoolean mStoped;

	private Object mData;

	private String mApkPath;

	private String mDatabasePath;

	private InstallTask() {
		super(POOLSIZE, POOLKEY);
		setTaskListener(mTaskListener);
		mStoped = new AtomicBoolean(false);

	}

	public InstallTask(InstallData bean, String path, boolean verify,
			String packageName, boolean silent,
			InstallListener<Object> listener, String databasePath) {
		this();
		mData = bean;
		mPath = path;
		mVerify = verify;
		mPackageName = packageName;
		mSilent = silent;
		mListener = listener;
		mDatabasePath = databasePath;
	}

	public boolean isStoped() {
		return mStoped.get();
	}

	public void stop() {
		mStoped.set(true);
	}

	public InstallTask setInstallFilePath(String filePath) {
		mPath = filePath;
		return this;
	}

	public InstallTask setVerify(boolean verify) {
		mVerify = verify;
		return this;
	}

	public InstallTask setTempPath(String mTempPath) {
		this.mTempPath = mTempPath;
		return this;
	}

	public InstallTask setSilent(boolean mSilent) {
		this.mSilent = mSilent;
		return this;
	}

	public InstallTask setListener(InstallListener<Object> mListener) {
		this.mListener = mListener;
		return this;
	}

	public InstallTask setInstallSdcard(boolean mInstallSdcard) {
		this.mInstallSdcard = mInstallSdcard;
		return this;
	}

	public InstallTask setPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
		return this;
	}

	public InstallTask setStoped(AtomicBoolean mStoped) {
		this.mStoped = mStoped;
		return this;
	}

	public InstallTask setData(Object data) {
		this.mData = data;
		return this;
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected Integer doInBackground(Void... params) throws Exception {

		try {
			mListener.onPrepare(mSilent, mData);
			if (!DirectoryUtils.existSDCard()) {
				return ResultCode.SDCARD_NOT_FOUND;
			}

			File installFile = new File(mPath);
			if (!installFile.exists()) {
				return ResultCode.NOT_FOUND;
			}

			if (mPath.endsWith(".apk")) {
				mApkPath = mPath;
				return installApk(installFile);
			}

			File dir = new File(mTempPath + "/" + System.currentTimeMillis());

			if (!dir.exists()) {
				dir.mkdirs();
			}

			InstallManifest installManifest = InstallUtils.getManifest(mPath,
					dir.getAbsolutePath());
			if (isStoped()) {
				return ResultCode.INSTALL_CANCEL;
			}

			mListener.onLoadAttributes(mData, installManifest);

			if (mVerify) {
				int code = InstallUtils.checkGpk(installManifest, installFile);
				if (code != ResultCode.INSTALL_SUCCESS) {
					return code;
				}
			}
			if (isStoped()) {
				return ResultCode.INSTALL_CANCEL;
			}
			long dataLength = InstallUtils.calcuteDataSize(mPath);
			if (isStoped()) {
				return ResultCode.INSTALL_CANCEL;
			}
			InstallUtils.zipToFile(mPath, dir.getAbsolutePath(),
					InstallUtils.APPLICATION_APK, this);

			mApkPath = dir.getAbsolutePath() + "/"
					+ InstallUtils.APPLICATION_APK;
			if (isStoped()) {
				return ResultCode.INSTALL_CANCEL;
			}

			String apkPath = dir.getAbsolutePath() + "/"
					+ InstallUtils.APPLICATION_APK;

			if (ApkEN.needDecode(apkPath)) {
				ApkEN.decode(apkPath);
			}
			if (isStoped()) {
				return ResultCode.INSTALL_CANCEL;
			}
			mListener.verifyComplete(mData);

			String dataPath = installManifest.getCopyPath().replace("\\", "/");
			dataPath = dataPath.substring(0, dataPath.lastIndexOf("/"));

			//TODO 保存安装路径

			saveDatapackagePath(dataPath, dataLength);
			InstallUtils.zipToDirectory(mPath, dataPath, dataLength, this);

			if (isStoped()) {
				if (!TextUtils.isEmpty(mPackageName)
						&& dataPath.toLowerCase().contains("/android/data")
						|| dataPath.toLowerCase().contains("/android/obb")) {
					File dataDir = new File(dataPath + "/" + mPackageName);
					if (dataDir != null && dataDir.exists()) {
						FileUtils.deleteFile(dataDir);
					}
				}

				return ResultCode.INSTALL_CANCEL;
			}

			mListener.onInstallApk(mData);

			File apkFile = new File(apkPath);
			int installResult = installApk(apkFile);
			if (installResult == ResultCode.INSTALL_SUCCESS) {
				FileUtils.deleteFile(dir);
			} else {
				return installResult;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return processException(e);
		}
		return ResultCode.INSTALL_SUCCESS;
	}

	/**
	 * 
	 *<p>保存安装路径到数据库</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param datapath	数据包路径
	 * @param length	数据包大小
	 */
	private void saveDatapackagePath(String datapath, long length) {
		DatabaseProvider provider = null;
		try {
			provider = new DatabaseProviderImpl(mDatabasePath);
			Datapackage datapackage = new Datapackage();
			datapackage.setPackageName(mPackageName);
			datapackage.setSize(length);

			String lowcasePath = datapath.toLowerCase();
			if (lowcasePath.endsWith("/android/obb")
					|| lowcasePath.endsWith("/android/data")
					|| lowcasePath.endsWith("/android/obb/")
					|| lowcasePath.endsWith("/android/data/")) {
				datapath = datapath + "/" + mPackageName;
			}
			ArrayList<String> paths = new ArrayList<String>();
			if (lowcasePath.endsWith("/android") || lowcasePath.endsWith("/android/")) {
				String datapath1 =  datapath + "/obb/" + mPackageName;
				String datapath2 =  datapath + "/data/" + mPackageName;
				paths.add(datapath1);
				paths.add(datapath2);
			}else{
				paths.add(datapath);
			}

			datapackage.setPaths(paths);
			provider.save(datapackage);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (provider != null) {
				try {
					provider.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private int processException(Throwable ex) {

		if (ex instanceof InstallParseException) {
			return ResultCode.INSTALL_PARSE_ERROR;
		}
		return ResultCode.INSTALL_EXCEPTION;
	}

	private TaskListener<Void, Integer> mTaskListener = new TaskListener<Void, Integer>() {

		@Override
		public void onTaskFailed(TaskFailed failed) {
			mListener.onError(ResultCode.INSTALL_EXCEPTION, null, mData);
		}

		@Override
		public void onTaskEnd(Integer data) {
			if (data == ResultCode.INSTALL_SUCCESS) {
				mListener.onSuccess(mData, mSilent);
			} else if (data == ResultCode.INSTALL_SYSTEM) {
				mListener.onLaunchSystemInstall(mData, mApkPath);
			} else if (data == ResultCode.INSTALL_CANCEL) {
				mListener.onCancel(mData);
			} else {
				mListener.onError(data, null, mData);
			}
		}

		@Override
		public void onTaskCancelled() {

		}

		@Override
		public void onTaskBegin() {

		}

		@Override
		public void onProgressUpdate(Void... values) {

		}
	};

	private int installApk(File file) throws Exception {
		try {
			if (CmdUtils.haveRoot() && mSilent) {
				mListener.onInstallApk(mData);
				int result = InstallUtils.installSilent(file.getAbsolutePath(),
						mPackageName, true, mInstallSdcard);
				if (result != ResultCode.INSTALL_SUCCESS) {
					return ResultCode.INSTALL_SYSTEM;
				}
				return ResultCode.INSTALL_SUCCESS;
			} else {
				return ResultCode.INSTALL_SYSTEM;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void pulishProgress(long progress, long length) {
		mListener.onProgress(progress, length, mData);
	}

}
