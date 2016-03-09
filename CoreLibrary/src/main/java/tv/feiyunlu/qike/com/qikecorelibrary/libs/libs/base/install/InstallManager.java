package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.ListenerCommand;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.ManagerConstants;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.impl.DatabaseProviderImpl;


/**
 * 
 *<p>安装核心控制器</p><br/>
 *<p>包含开始、取消安装等操作</p>
 * @since 1.0.0
 * @author xujiaoyong
 */
public class InstallManager {

	private static final int LIMIT = 1;
	private Queue<InstallData> waitTasks = null;
	private LinkedList<InstallData> runningTasks = null;
	private ExecutorService executor;
	private Object lock;

	private Handler handler;
	private InstallListener<Object> installListener;
	private Context context;
	private List<InstallListener<InstallData>> listeners;

	private static InstallManager INSTANCE = null;

	private String mTempPath;

	private String mDatabasePath;

	private HashMap<InstallRequest, InstallData> mDatas = new HashMap<InstallRequest, InstallData>();

	/**
	 * 
	 *<p>获取控制器单例对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param context
	 * @return 返回单例对象
	 */
	public synchronized static InstallManager getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new InstallManager(context);
		}
		return INSTANCE;
	}

	/**
	 * 
	 *<p>根据包名返回数据包路径对象，包含路径、包名、文件大小属性</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param packageName 包名
	 * @return 
	 */
	public Datapackage getDatapackage(String packageName) {
		DatabaseProvider provider = null;
		try {
			provider = new DatabaseProviderImpl(mDatabasePath);
			return provider.query(Datapackage.class, packageName);
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
		return null;
	}

	/**
	 * 
	 *<p>返回已存储所有数据包路径对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public List<Datapackage> getAllDataPackages() {
		DatabaseProvider provider = null;
		try {
			provider = new DatabaseProviderImpl(mDatabasePath);
			return provider.queryAll(Datapackage.class);
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
		return null;
	}
	
	/**
	 * 
	 *<p>删除包名对应的数据包路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param packageName 包名
	 */
	public void deleteDatapackage(String packageName){
		DatabaseProvider provider = null;
		try {
			provider = new DatabaseProviderImpl(mDatabasePath);
			Datapackage dataPackage = provider.query(Datapackage.class,packageName);
			if (dataPackage != null) {
				provider.delete(dataPackage);
			}
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
	
	
	
	/**
	 * 
	 *<p>更新数据包路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param datapackage 需要进行更新的数据包路径对象
	 */
	public void updateDatapackage(Datapackage datapackage){
		DatabaseProvider provider = null;
		try {
			provider = new DatabaseProviderImpl(mDatabasePath);
			if (datapackage != null) {
				provider.save(datapackage);
			}
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

	/**
	 * 
	 *<p>返回GPK临时路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public String getTempPath() {
		return mTempPath;
	}

	/**
	 * 
	 *<p>设置数据库路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param databasePath 数据库路径
	 */
	public void setDatabasePath(String databasePath) {
		mDatabasePath = databasePath;
	}

	/**
	 * 
	 *<p>返回数据库路径</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 数据库路径
	 */
	public String getDatabasePath() {
		return mDatabasePath;
	}

	/**
	 * 
	 *<p>设置GPK解压临时路径/p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param mTempPath 临时路径
	 */
	public void setTempPath(String mTempPath) {
		this.mTempPath = mTempPath;
	}

	private InstallManager(Context context) {
		this.context = context;
		waitTasks = new LinkedList<InstallData>();
		runningTasks = new LinkedList<InstallData>();
		handler = new Handler();
		executor = Executors.newFixedThreadPool(6);
		lock = new Object();
		listeners = new LinkedList<InstallListener<InstallData>>();
		installListener = new ManagerListener();
	}

	/**
	 * 
	 *<p>注册安装监听器</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param listener 监听器对象 {@link InstallListener}
	 */
	public void registerListener(InstallListener<InstallData> listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * 
	 *<p>移除安装监听器</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param listener 监听器对象 {@link InstallListener}
	 */
	public void unregisterListener(InstallListener<InstallData> listener) {
		listeners.remove(listener);
	}

	/**
	 * 
	 *<p>安装应用</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param request 安装对象 {@link InstallRequest} 
	 */
	public void install(InstallRequest request) {

		if (!mDatas.containsKey(request)) {
			InstallData installData = new InstallData();
			installData.setData(request);
			mDatas.put(request, installData);
			runSubThreadCommand(new InstallCommand(installData));
		}

	}

	/**
	 * 
	 *<p>取消安装</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param request 安装对象 {@link InstallRequest} 
	 */
	public void stop(InstallRequest request) {
		//		InstallData installData = new InstallData();
		//		installData.setData(request);

		InstallData installData = mDatas.remove(request);
		runSubThreadCommand(new StopCommand(installData));
	}

	private void runMainThreadCommand(Runnable runnable) {
		handler.post(runnable);
	}

	private void runSubThreadCommand(Runnable runnable) {
		executor.execute(runnable);
	}

	private class InstallCommand implements Runnable {
		private InstallData bean;

		public InstallCommand(InstallData bean) {
			this.bean = bean;
		}

		@Override
		public void run() {

			if (bean != null) {
				Log.i("mzw_installer", "onInstall wait lock");
				synchronized (lock) {
					Log.i("mzw_installer", "enter lock");
					if (runningTasks.contains(bean) || waitTasks.contains(bean)) {
						return;
					}
					bean.setStatus(InstallStatus.WAIT);
					waitTasks.offer(bean);
				}
				notifyInstall(null);
			}
		}

	}

	private String getProcessName() {

		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;

	}

	private void notifyInstall(Object bean) {
		if (bean != null) {
			Log.i("mzw_installer", "wait  running lock");
			synchronized (runningTasks) {
				runningTasks.remove(bean);

			}
		}
		String processName = getProcessName();
		if (processName.equals(context.getPackageName())) {

			Log.i("mzw_installer", "start service");
			Intent intent = new Intent(context.getPackageName()
					+ ManagerConstants.ACTION_INSTALL);
			context.startService(intent);
		} else {
			startNext();
		}

	}

	private class StopCommand implements Runnable {
		private InstallData bean;

		public StopCommand(InstallData bean) {
			this.bean = bean;
		}

		@Override
		public void run() {
			synchronized (lock) {
				if (!runningTasks.contains(bean)) {
					if (!waitTasks.contains(bean)) {
						return;
					}
					waitTasks.remove(bean);
					installListener.onCancel(bean);
					return;
				} else {
					InstallTask task = bean.getTask();
					if (task != null) {
						task.stop();
					}
				}
			}
		}

	}

	private void startNext() {
		Log.i("mzw_installer", "startnext wait lock");
		synchronized (lock) {

			while (true) {
				if (runningTasks.size() >= LIMIT) {
					break;
				}
				InstallData startBean = waitTasks.poll();
				if (startBean != null) {
					if (!runningTasks.contains(startBean)) {
						runningTasks.add(startBean);
						Log.i("mzw_installer", "runmain");
						runMainThreadCommand(new InstallThread(startBean));
					}
				} else {
					break;
				}
			}
		}
	}

	private class InstallThread implements Runnable {

		private InstallData bean;

		public InstallThread(InstallData bean) {
			this.bean = bean;
		}

		@Override
		public void run() {
			try {

				InstallRequest request = bean.getData();
				String packageName = request.getPackageName();
				String path = request.getFilePath();
				boolean verify = request.isVerify();
				boolean silent = request.isSilent();
				InstallTask task = new InstallTask(bean, path, verify,
						packageName, silent, installListener, mDatabasePath);
				bean.setTask(task);
				task.setTempPath(mTempPath);
				task.execute();
			} catch (Exception e) {
				e.printStackTrace();
				installListener.onError(ResultCode.INSTALL_EXCEPTION, e, bean);
			}
		}

	}

	private class ManagerListener implements InstallListener<Object> {

		private void notifyListener(String method, Object... args) {
			for (InstallListener<InstallData> listener : listeners) {
				try {
					runMainThreadCommand(new ListenerCommand(listener, method,
							args));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onSuccess(Object bean, Boolean silent) {

			InstallData data = (InstallData) bean;
			data.setStatus(InstallStatus.NULL);
			mDatas.remove(data.getData());

			notifyInstall(bean);
			notifyListener("onSuccess", bean, silent);
		}

		@Override
		public void onError(Integer code, Throwable ex, Object bean) {

			InstallData data = (InstallData) bean;
			data.setStatus(InstallStatus.ERROR);
			data.setError(code);
			mDatas.remove(data.getData());

			notifyInstall(bean);
			notifyListener("onError", code == null ? Integer.valueOf(0) : code,
					ex == null ? new Throwable("error") : new Throwable(ex),
					bean);
		}

		@Override
		public void onProgress(Long progress, Long totalSize, Object bean) {

			InstallData data = (InstallData) bean;
			data.setStatus(InstallStatus.UNPACKING);
			data.setLength(totalSize);
			data.setProgress(progress);
			String sizeText = Formatter.formatFileSize(context, progress) + "/"
					+ Formatter.formatFileSize(context, totalSize);
			int percent = (int) ((double) progress / (double) totalSize * 100);
			data.setSize(sizeText);
			data.setPercent(percent);

			notifyListener("onProgress", progress, totalSize, bean);
		}

		@Override
		public void verifyComplete(Object bean) {
			((InstallData) bean).setStatus(InstallStatus.UNPACKING);
			notifyListener("verifyComplete", bean);
		}

		@Override
		public void onPrepare(Boolean silent, Object bean) {
			InstallData data = (InstallData) bean;

			data.setError(0);
			String path = data.getData().getFilePath();
			if (path.endsWith(".apk")) {
				((InstallData) bean).setStatus(InstallStatus.INSTALLINGAPK);
			} else {
				((InstallData) bean).setStatus(InstallStatus.VERIFYING);
			}
			notifyListener("onPrepare", silent, bean);
		}

		@Override
		public void onCancel(Object bean) {

			InstallData data = (InstallData) bean;
			data.setStatus(InstallStatus.NULL);
			mDatas.remove(data.getData());

			notifyInstall(bean);
			notifyListener("onCancel", bean);
		}

		@Override
		public void onLoadAttributes(Object bean, InstallManifest mainifest) {
			notifyListener("onLoadAttributes", bean, mainifest);
		}

		@Override
		public boolean continueProcess() {
			return true;
		}

		@Override
		public void onInstallApk(Object bean) {
			((InstallData) bean).setStatus(InstallStatus.INSTALLINGAPK);
			notifyListener("onInstallApk", bean);
		}

		@Override
		public void notifyData() {
			notifyListener("notifyData");

		}

		@Override
		public void onLaunchSystemInstall(Object bean, String apkPath) {
			notifyListener("onLaunchSystemInstall", bean, apkPath);
			onSuccess(bean, false);
		}

	}

	/**
	 * 
	 *<p>是否正在等待安装</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param request 
	 * @return
	 */
	public boolean isWait(InstallRequest request) {
		InstallData bean = mDatas.get(request);

		InstallStatus status = bean == null ? InstallStatus.NULL : bean
				.getStatus();
		return status == InstallStatus.WAIT;
	}

	/**
	 * 
	 *<p>是否正在安装</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param request
	 * @return
	 */
	public boolean isRunning(InstallRequest request) {

		InstallData bean = mDatas.get(request);

		InstallStatus status = bean == null ? InstallStatus.NULL : bean
				.getStatus();
		return status != InstallStatus.WAIT && status != InstallStatus.ERROR
				&& status != InstallStatus.NULL;
	}

	//	private void notifyData() {
	//		installListener.notifyData();
	//	}

	/**
	 * 
	 *<p>获取正在安装任务个数</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public int getRunningCount() {
		return runningTasks.size();
	}

	/**
	 * 
	 *<p>进行下一个等待任务</p><br/>
	 *<p>安装队列每次只会有一个任务进行，如无等待任务，则不做任何事情，如有等待任务，取出顶部元素进行安装，其余依旧等待</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 */
	public void runWaitItems() {
		runSubThreadCommand(new RunWaitItemsCommand());
	}

	private class RunWaitItemsCommand implements Runnable {

		@Override
		public void run() {
			try {
				Log.i("mzw_installer", "startnext");
				startNext();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 *<p>获取正在安装项</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public InstallData[] getRunningBeans() {

		if (runningTasks != null) {

			List<InstallData> lists = runningTasks.subList(0,
					runningTasks.size());
			InstallData[] beans = new InstallData[lists.size()];
			for (int i = 0; i < lists.size(); i++) {
				beans[i] = lists.get(i);
			}
			return beans;
		}

		return new InstallData[] {};
	}

}
