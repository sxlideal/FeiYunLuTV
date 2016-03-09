package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.DownloadDatabaseProvider;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.DownloadStatus;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.DownloadWraperListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.Downloadable;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.ListenerCommand;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.creator.SavePathCreator;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread.DownloadTask;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread.FileDownloadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;


public class DownloadManager {

	
	
	
	private static final DownloadManager INSTANCE = new DownloadManager();

	private static final Object LOCK = new Object();
	private LinkedHashMap<Downloadable, TaskStatus> mDownloads = new LinkedHashMap<Downloadable, TaskStatus>();
	private LinkedHashMap<Downloadable, TaskStatus> mHistories = new LinkedHashMap<Downloadable, TaskStatus>();

	private Queue<TaskStatus> mWaitTask = new LinkedList<TaskStatus>();
	private LinkedList<TaskStatus> mRunningTask = new LinkedList<TaskStatus>();

	private DatabaseProvider mProvider;

	private int mTaskLimit = 1;

	private ExecutorService mExecutor = Executors.newCachedThreadPool();

	private String mDatabasePath;

	private List<Class<? extends Downloadable>> classes = new ArrayList<Class<? extends Downloadable>>();

	private Handler mHandler = new Handler();
	private Thread mMainThread;
	private SavePathCreator mPathCreator;

	private boolean mInited;

	private long mRate = 1500;

	private DownloadWrapper mWrapper = new DownloadWrapper();

	
	private HashMap<Class<?>, HashSet<DownloadWraperListener>> mListeners = new HashMap<Class<?>, HashSet<DownloadWraperListener>>();

	private DownloadManager() {
		mMainThread = Thread.currentThread();
	}

	public void setPathCreator(SavePathCreator mPathCreator) {
		this.mPathCreator = mPathCreator;
	}

	
	/**
	 * 
	 *<p>初始化下载管理</p><br/>
	 *<p>必须要在子线程中进行</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @throws Exception 初始化失败后抛出对应异常
	 */
	public void init(Context context) throws Exception {
		if (isMainThread()) {
			throw new DownloadInitException("can not init in main thread!!!!!");
		}
		if (classes == null || classes.size() == 0) {
			throw new DownloadInitException("classes can not null");
		}
		mProvider = new DownloadDatabaseProvider(context, mDatabasePath);
		initDatabases();
		mInited = true;

	}

	/**
	 * 
	 *<p>设置下载限制</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param limit 限制个数，建议最多到3
	 */
	public synchronized void setLimit(int limit) {
		mTaskLimit = limit;
		if (mRunningTask.size() >= limit) {
			runSubThreadCommand(new StopLimitCommand());
		} else {
			runWaitItems();
		}

	}
	
	/**
	 * 
	 *<p>设置数据库路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param mDatabasePath 数据库路径
	 */
	public void setDatabasePath(String mDatabasePath) {
		this.mDatabasePath = mDatabasePath;
	}

	
	
	/**
	 * 
	 *<p>删除下载记录</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 被删除的下载记录
	 */
	public void deleteHistory(Downloadable item) {
		try {
			mHistories.remove(item);
			mWrapper.onDataChanged(item.getClass());
			runSubThreadCommand(new DeleteHistoryCommand(item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 *<p>判断下载项是否存在</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 判断对象
	 * @return 返回true则代表存在，返回false代表不存在
	 */
	public boolean isExists(Downloadable item) {
		return isDownloadItem(item) || isHistoryItem(item);
	}

	
	
	/**
	 * 
	 *<p>判断是否为正在下载项</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 判断对象
	 * @return 返回true则代表是，返回false代表不是
	 */
	public boolean isDownloadItem(Downloadable item) {
		return mDownloads.containsKey(item);
	}

	
	
	/**
	 * 
	 *<p>判断是否为下载历史记录</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 判断对象
	 * @return 返回true则代表是，返回false代表不是
	 */
	public boolean isHistoryItem(Downloadable item) {
		return mHistories.containsKey(item);
	}

	
	/**
	 * 
	 *<p>获取任务限制个数</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回任务限制个数
	 */
	public int getLimit() {
		return mTaskLimit;
	}

	
	/**
	 * 
	 *<p>获取下载管理单例对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回下载管理单例对象
	 */
	public static synchronized DownloadManager getInstance() {

		return INSTANCE;
	}
	
	/**
	 * 
	 *<p>获取正在下载个数</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回正在下载个数
	 */
	public int getDownloadCount() {
		return mDownloads.size();
	}

	
	
	/**
	 * 
	 *<p>返回下载记录状态</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 下载项
	 * @return 返回下载记录状态
	 */
	public TaskStatus getHistory(Downloadable item) {
		return mHistories.get(item);
	}

	private void initDatabases() {
		for (Class<? extends Downloadable> clazz : classes) {
			List<? extends Downloadable> datas = mProvider.queryAll(clazz);
			if (datas != null) {
				for (Downloadable downloadable : datas) {
					if (downloadable.getTaskType() == Downloadable.TYPE_DOWNLOAD) {
						mDownloads.put(downloadable, new TaskStatus(
								downloadable, DownloadStatus.STOPED));
					} else {
						mHistories.put(downloadable, new TaskStatus(
								downloadable, DownloadStatus.COMPLETE));
					}
				}
			}
		}
	}

	
	
	

	
	

	private class DeleteHistoryCommand implements Runnable {
		private Downloadable item;

		public DeleteHistoryCommand(Downloadable item) {
			this.item = item;
		}

		@Override
		public void run() {
			try {
				mProvider.delete(item);
				deleteFiles(item);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	
	/**
	 * 
	 *<p>增加下载历史记录项</p><br/>
	 *<p>目前只有存档用到，其它项目不需要调用</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 增加对象
	 */
	public void addHistory(Downloadable item) {
		try {
			if (!mHistories.containsKey(item)) {
				mProvider.save(item);
				TaskStatus status = new TaskStatus(item, DownloadStatus.WAIT);
				mHistories.put(item, status);
			}
			mWrapper.onDataChanged(item.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 *<p>获取下载历史记录个数</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回下载历史记录个数
	 */
	public int getHistoryCount() {
		return mHistories.size();
	}

	
	/**
	 * 
	 *<p>根据索引获取正在下载项</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param index 索引位置
	 * @return 返回正在下载项
	 */
	public Downloadable getDownloadItem(int index) {
		if (mDownloads.size() > 0) {
			int i = 0;
			for (Downloadable item : mDownloads.keySet()) {
				if ((i++) == index) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 *<p>根据索引获取下载历史记录项</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param index 索引位置
	 * @return 返回下载历史记录项
	 */
	public Downloadable getHistoryItem(int index) {
		if (mHistories.size() > 0) {
			int i = 0;
			for (Downloadable item : mHistories.keySet()) {
				if ((i++) == index) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 *<p>注册一个下载监听对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param listener 下载监听对象
	 */
	public void registerListener(Class<? extends Downloadable> clazz,DownloadWraperListener listener) {
		HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
		if (listeners == null) {
			listeners= new HashSet<DownloadWraperListener>();
			mListeners.put(clazz, listeners);
		}
		listeners.add(listener);
		
	}

	
	/**
	 * 
	 *<p>注销一个下载监听对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param listener 下载监听对象
	 */
	public void unregisterListener(DownloadWraperListener listener) {
		mListeners.remove(listener);
	}
	
	/**
	 * 
	 *<p>开始下载</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 需要进行下载的对象
	 */
	public void start(Downloadable item) {
		runSubThreadCommand(new StartCommand(item,true));
	}
	
	
	/**
	 * 
	 *<p>开始下载</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 需要进行下载的对象
	 */
	public void startNoWait(Downloadable item) {
		runSubThreadCommand(new StartCommand(item,false));
	}

	
	/**
	 * 
	 *<p>停止下载</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 需要进行停止操作的下载对象
	 */
	public void stop(Downloadable item) {
		TaskStatus bean = mDownloads.get(item);
		DownloadTask<TaskStatus> task = bean == null ? null : bean.getTask();
		if (task != null && task.isRunning()) {
			task.stop();
		} else {
			runSubThreadCommand(new RemoveWaitItemCommand(item));
		}
	}

	
	/**
	 * 
	 *<p>获取下载状态</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 需要获取状态的下载对象
	 * @return 返回任务状态
	 */
	public TaskStatus getStatus(Downloadable item) {
		TaskStatus status = mDownloads.get(item);
		if (status == null) {
			status = mHistories.get(item);
		}
		return status;
	}

	
	/**
	 * 
	 *<p>取消下载</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param item 需要进行取消下载操作的对象
	 */
	public void cancel(Downloadable item) {
		if (mDownloads.containsKey(item)) {
			TaskStatus bean = mDownloads.remove(item);
			mWrapper.onDataChanged(item.getClass());
			DownloadTask<TaskStatus> task = bean == null ? null : bean
					.getTask();
			if (task != null && task.isRunning()) {
				task.cancel();
			} else {
				mWrapper.onCanceling(bean);
			}
		} else {
			TaskStatus bean = mHistories.remove(item);
			mWrapper.onDataChanged(item.getClass());
			runSubThreadCommand(new CancelCommand(bean, true));
		}

	}

	private void runSubThreadCommand(Runnable runnable) {
		mExecutor.execute(runnable);
	}

	private boolean isMainThread() {
		return Thread.currentThread().equals(mMainThread);
	}

	
	

	
	
	private void runMainThreadCommand(Runnable runnable) {
		mHandler.post(runnable);
	}

	private class StartCommand implements Runnable {
		private Downloadable mItem;
		
		private boolean mWait;

		public StartCommand(Downloadable item,boolean wait) {
			this.mItem = item;
			this.mWait = wait;
		}

		@Override
		public void run() {
			if (!mInited) {
				throw new DownloadException("downloadManager uninited");
			}
			
			Log.i("mzw_downloadManager", "run");
			TaskStatus status = mDownloads.get(mItem);
			if (status == null) {
				status = new TaskStatus(mItem, DownloadStatus.WAIT);
				String savePath = mPathCreator.getSavePath(mItem);
				mItem.setSavePath(savePath);
				mItem.setTaskType(Downloadable.TYPE_DOWNLOAD);
				createOrUpdate(mItem);
				if (mWait) {
					runMainThreadCommand(new AddDownloadCommand(mItem, status));
				}else{
					runNoWaitItem(status);
				}
			} else {
				if (mWait) {
					addWaitTask(status);
					runWaitItems();
				}else{
					runNoWaitItem(status);
				}
				
			}

		}
	}
	
	private void runNoWaitItem(TaskStatus status){
			Log.i("mzw_downloadManager", "runNoWaitItem");
			runMainThreadCommand(new ListenerCommand(mWrapper, "onPrepare",
					status));
			status.setStatus(DownloadStatus.STARTED);
			DownloadTask<TaskStatus> task = createTask(status);
			status.setTask(task);
			task.start();
	}

	private void createOrUpdate(Downloadable item) {
		if (isStoreable(item)) {
			mProvider.save(item);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isStoreable(Downloadable item){
		try {
			if (item != null) {
				Class clazz = item.getClass();
				return clazz.isAnnotationPresent(DatabaseTable.class);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	private void addWaitTask(TaskStatus status) {
		synchronized (LOCK) {
			if (!mWaitTask.contains(status) && !mRunningTask.contains(status)) {
				status.setStatus(DownloadStatus.WAIT);
				mWaitTask.offer(status);
				runMainThreadCommand(new ListenerCommand(mWrapper, "onPrepare",
						status));
			}
		}
	}

	private class AddDownloadCommand implements Runnable {
		private Downloadable mItem;
		private TaskStatus mStatus;

		public AddDownloadCommand(Downloadable mItem, TaskStatus mStatus) {
			super();
			this.mItem = mItem;
			this.mStatus = mStatus;
		}

		@Override
		public void run() {
			mDownloads.put(mItem, mStatus);
			mWrapper.onDataChanged(mItem.getClass());
			runSubThreadCommand(new AddWaitTaskCommand(mStatus));
		}

	}

	private class AddWaitTaskCommand implements Runnable {
		private TaskStatus mStatus;

		public AddWaitTaskCommand(TaskStatus mStatus) {
			super();
			this.mStatus = mStatus;
		}

		@Override
		public void run() {
			addWaitTask(mStatus);
			runWaitItems();
		}

	}

	
	
	

	private class StopLimitCommand implements Runnable {

		@Override
		public void run() {
			try {
				synchronized (LOCK) {
					while (true) {
						if (mRunningTask.size() <= mTaskLimit) {
							break;
						}
						TaskStatus bean = mRunningTask.poll();
						if (bean != null) {
							bean.getTask().stop();
							addWaitTask(bean);
						} else {
							break;
						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}

	}

	private class DownloadWrapper implements FileDownloadListener<TaskStatus> {

		private void changeStatus(TaskStatus status) {
			
			Class<? extends Downloadable> clazz = status.getItem().getClass();
			
			HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
			if (listeners != null) {
				for (DownloadWraperListener listener : listeners) {
					listener.onStateChanged(status);
				}
			}
			
		}

		@Override
		public void onPrepare(TaskStatus data) {
			data.setErrorCode(0);
			data.setStatus(DownloadStatus.WAIT);
			changeStatus(data);
		}

		@Override
		public void onStarting(TaskStatus data) {

			data.setPercent(0);
			data.setStatus(DownloadStatus.STARTING);
			changeStatus(data);
		}

		@Override
		public void onStarted(TaskStatus data) {
			data.setStatus(DownloadStatus.STARTED);
			changeStatus(data);
		}

		@Override
		public void onLoadContentLength(long contentLength, TaskStatus data) {
			try {
				Downloadable item = data.getItem();
				item.setContentLength(contentLength);
				createOrUpdate(item);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStoping(TaskStatus data) {
			data.setStatus(DownloadStatus.STOPING);
			changeStatus(data);
			runSubThreadCommand(new RunWaitItemsCommand(data));
		}

		@Override
		public void onStopPrepare(TaskStatus data) {
			removeWaitItems(data);
		}

		@Override
		public void onStoped(TaskStatus data) {
			data.setStatus(DownloadStatus.STOPED);
			changeStatus(data);
			runSubThreadCommand(new RunWaitItemsCommand(data));
		}

		@Override
		public void onErrorPrepare(TaskStatus data) {

		}

		@Override
		public void onProgressPrepare(long progress, long len, TaskStatus data) {
			calculateProgress(data, progress, len);
		}

		@Override
		public void onProgress(long progress, long len, TaskStatus data) {
			
			
Class<? extends Downloadable> clazz = data.getItem().getClass();
			
			HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
			if (listeners != null) {
				for (DownloadWraperListener listener : listeners) {
					try {
						listener.onProgress(data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
			
			
		}

		@Override
		public void onCanceling(TaskStatus data) {
			data.setStatus(DownloadStatus.CANCELING);
			changeStatus(data);
			runSubThreadCommand(new CancelCommand(data, true));
			runSubThreadCommand(new RunWaitItemsCommand(data));
		}

		@Override
		public void onCancelPrepare(TaskStatus data) {

		}

		@Override
		public void onCanceled(TaskStatus data) {
			data.setStatus(DownloadStatus.CANCELED);
			changeStatus(data);
		}

		@Override
		public void onError(Integer errorCode, Integer status, String ex,
				TaskStatus data) {
			if (errorCode > -7000) {
				data.setStatus(DownloadStatus.ERROR);
				data.setErrorCode(errorCode);
				
				Class<? extends Downloadable> clazz = data.getItem().getClass();
				
				HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
				if (listeners != null) {
					for (DownloadWraperListener listener : listeners) {
						try {
							listener.onError(data);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
				
				
				runSubThreadCommand(new RunWaitItemsCommand(data));
			}
		}

		@Override
		public void onCompletePrepare(TaskStatus data) {
			try {
				Downloadable item = data.getItem();
				item.setTaskType(Downloadable.TYPE_HISTORY);
				createOrUpdate(item);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onComplete(TaskStatus data) {
			data.setTask(null);
			data.setErrorCode(0);
			Downloadable item = data.getItem();
			if (isStoreable(item)) {
				mDownloads.remove(item);
				if (!mHistories.containsKey(data)) {
					//				data.setType(Downloadable.TYPE_HISTORY);
					mHistories.put(data.getItem(), data);
				}
				onDataChanged(data.getItem().getClass());
			}
			
			data.setStatus(DownloadStatus.COMPLETE);
			changeStatus(data);
			runSubThreadCommand(new RunWaitItemsCommand(data));
		}

		public void onDataChanged(Class<? extends Downloadable> clazz) {
			
			HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
			if (listeners != null) {
				for (DownloadWraperListener listener : listeners) {
					listener.onDataChanged();
				}
			}
			
			
		}

		private void calculateProgress(TaskStatus data, long progress, long len) {
			try {
				int percent = (int) ((double) progress / (double) len * 100);
				data.setPercent(percent);
				data.setLength(len);
				long nowSpeed = progress - data.getProgress();
				data.setProgress(progress);
				data.setSpeed(nowSpeed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onShowVCode(TaskStatus data,String vcode, StringBuilder result) {
			
			Class<? extends Downloadable> clazz = data.getItem().getClass();
			
			HashSet<DownloadWraperListener> listeners = mListeners.get(clazz);
			if (listeners != null) {
				for (DownloadWraperListener listener : listeners) {
					try {
						listener.onVCodeCall(vcode, result);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			
		}

	}

	private class RunWaitItemsCommand implements Runnable {

		private TaskStatus mPreviousBean;

		public RunWaitItemsCommand(TaskStatus bean) {
			this.mPreviousBean = bean;
		}

		@Override
		public void run() {
			try {
				synchronized (LOCK) {

					if (mPreviousBean != null) {
						mRunningTask.remove(mPreviousBean);
					}
					while (true) {
						if (mRunningTask.size() >= mTaskLimit) {
							break;
						}
						TaskStatus bean = mWaitTask.poll();
						if (bean != null) {
							if (!mRunningTask.contains(bean)) {
								bean.setStatus(DownloadStatus.STARTED);
								mRunningTask.add(bean);
								try {
									Downloadable item = bean.getItem();
									item.setDownloadStartTime(System
											.currentTimeMillis());
									mProvider.update(item);
								} catch (Throwable e) {
									e.printStackTrace();
								}
								DownloadTask<TaskStatus> task = createTask(bean);
								bean.setTask(task);
								task.start();
							}
						} else {
							break;
						}
					}
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	public void runWaitItems() {
		runSubThreadCommand(new RunWaitItemsCommand(null));
	}

	private DownloadTask<TaskStatus> createTask(TaskStatus status) {
		Downloadable item = status.getItem();
		DownloadTask<TaskStatus> task = new DownloadTask<TaskStatus>(
				item.getRequestPath(), item.getSavePath());
		task.setData(status);
		task.setRate(mRate);
		task.setLength(item.getContentLength());
		task.setDownloadListener(mWrapper);
		return task;
	}

	private class RemoveWaitItemCommand implements Runnable {

		private Downloadable item;

		public RemoveWaitItemCommand(Downloadable item) {
			super();
			this.item = item;
		}

		@Override
		public void run() {
			TaskStatus bean = mDownloads.get(item);
			if (bean != null) {
				runMainThreadCommand(new ListenerCommand(mWrapper, "onStoping",
						bean));
				removeWaitItems(bean);
				runMainThreadCommand(new ListenerCommand(mWrapper, "onStoped",
						bean));
			}

		}

	}

	private void removeWaitItems(TaskStatus bean) {
		synchronized (LOCK) {
			mWaitTask.remove(bean);
			bean.setStatus(DownloadStatus.STOPED);
		}
	}

	private class CancelCommand implements Runnable {

		private TaskStatus bean;
		private boolean runListenerMethod;

		public CancelCommand(TaskStatus bean, boolean runListenerMethod) {
			this.bean = bean;
			this.runListenerMethod = runListenerMethod;
		}

		@Override
		public void run() {
			Downloadable deleteItem = null;
			try {
				deleteItem = bean.getItem();
				mProvider.delete(deleteItem);
				if (runListenerMethod) {
					removeWaitItems(bean);

					//TODO
					Runnable nextCommand = !runListenerMethod ? null
							: new ListenerCommand(mWrapper, "onCanceled", bean);
					runMainThreadCommand(nextCommand);
					deleteFiles(deleteItem);
				}

			} catch (Throwable e) {
				e.printStackTrace();
				//TODO
				//				try {
				//					runMainThreadCommand(new ListenerCommand(mWrapper,
				//							"onError", ManagerConstants.ERROR_REMOVE_ITEM, -1,
				//							e.getMessage(), new ManagerBean(
				//									ManagerConstants.TYPE_DOWNLOAD,
				//									DownloadStatus.ERROR, deleteItem)));
				//				} catch (Exception e1) {
				//					e1.printStackTrace();
				//				}
			}

		}

	}

	private void deleteFiles(Downloadable item) {
		try {
			String path = item.getSavePath();
			File file = new File(path);
			file.delete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void addClass(Class<? extends Downloadable> clazz) {
		if (!classes.contains(clazz)) {
			classes.add(clazz);
		}
	}

}
