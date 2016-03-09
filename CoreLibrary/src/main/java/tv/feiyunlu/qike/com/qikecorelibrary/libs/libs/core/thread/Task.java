/*
 * 文件名：Task.java
 * 描述：任务
 * 版本：v1.0.0
 * 日期：2014-3-15
 * 版权：Copyright ? 2014 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpError;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.Logger;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.LoggerConfig;


/**
 * 
 *<p>任务</p><br/>
 *<p>所有任务类可以继承此类，可以完成任务的一系列操作</p>
 * @since 1.0.0
 * @author suenxianhao
 * @param <Params> 泛型 可以传递url数组
 * @param <Progress> 泛型 进度信息
 * @param <Result > 泛型 返回类型
 */

@LoggerConfig(tag="qike_task")
public abstract class Task<Params, Progress, Result> {

	private volatile Status mStatus = Status.PENDING;
//	private static final String TAG = "Task";

	//线程池配置参数

	private static int mMaximumPoolSize = 128;//最大线程数
	private static int mKeepAlive = 10;//终止前多余的空闲线程等待新任务的最长时间

	//任务执行
	private static final int MESSAGE_POST_RESULT = 0x1;
	private static final int MESSAGE_POST_PROGRESS = 0x2;
	private static final int MESSAGE_POST_CANCEL = 0x3;
	private static final int MESSAGE_POST_FAILED_TIMEOUT = 0x4;
	private static final int MESSAGE_POST_FAILED_NORMAL = 0x5;
	private static final int MESSAGE_POST_NONE_NETWORK = 0x6;

	private static final Map<String, ThreadPoolExecutor> mTaskPoolMap = new HashMap<String, ThreadPoolExecutor>();

	private static final Map<String, Hashtable<String, FutureTask>> mThreadMap = new HashMap<String, Hashtable<String, FutureTask>>();

	private static class CoreThreadFactory implements ThreadFactory {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			Logger.getLogger(this).i("创建新线程" + mCount.getAndIncrement());
			return new Thread(r, "Task #");
		}

	}

	/**
	 *(1)线程数<corePoolSize，创建新的线程<br/>
	 *(2)线程数>corePoolSize && 线程数<maximumPoolSize && 缓冲队列未满，添加到缓冲队列<br/>
	 *(3)线程数>corePoolSize && 线程数<maximumPoolSize && 缓冲队列已满，创建异步线程<br/>
	 *(4)线程数>corePoolSize && 线程数>maximumPoolSize 抛出RejectedExecutionException异常
	 *(5)线程数>corePoolSize 空闲时间为终止前多余的空闲线程等待新任务的最长时间
	 */

	private final FutureTask<Result> mFuture;
	private final WorkerRunnable<Params, Result> mWorker;
	//内部Handler，处理消息机制
	private static final InternalHandler sHandler = new InternalHandler();
	private String mTaskKey;
	private String mTaskPoolKey;
	private TaskListener<Progress, Result> mTaskListener;
	private static final Map<String, BlockingQueue> mWorkQueue = new HashMap<String, BlockingQueue>();

	public Task(String taskPoolKey) {
		mWorker = new CoreWorkerRunnable(taskPoolKey);
		mFuture = new CoreFutureTask(mWorker, taskPoolKey);

	}

	public Task(int corePoolSize, String taskPoolKey) {
		this(taskPoolKey);
		mTaskPoolKey = taskPoolKey;
		if (mTaskPoolMap.get(taskPoolKey) == null) {
			Logger.getLogger(this).i("mTaskPoolKey------------------------------" + taskPoolKey);
			Hashtable<String, FutureTask> sTasks = new Hashtable<String, FutureTask>();
			//线程池队列		
			BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>();
			mTaskPoolMap.put(taskPoolKey, new ThreadPoolExecutor(corePoolSize, mMaximumPoolSize, mKeepAlive, TimeUnit.SECONDS,
					sWorkQueue, new CoreThreadFactory())); 
			mWorkQueue.put(taskPoolKey, sWorkQueue);
			mThreadMap.put(taskPoolKey, sTasks);
		}

	}

	/**
	 * <p>任务状态</p><br/>
	 * <p>运行前，运行中，运行结束</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	public enum Status {
		PENDING, RUNNING, FINISHED, UNEXPECTED
	}

	public final Status getStatus() {

		return mStatus;
	}

	/**
	 *<p>线程池缓冲队列个数</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @return
	 */
	public final int threadPoolSize(String key) {
		return mWorkQueue.get(key).size();
	}

	/**
	 * <p>任务个数</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @return
	 */
	public static final int cacheTaskCount(String taskPoolKey) {
		return mThreadMap.get(taskPoolKey).size();
	}

	/**
	 *<p>任务是否被取消</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @return
	 */
	public final boolean isCancelled() {
		return mFuture.isCancelled();
	}

	/**
	 *<p>任务是否被取消</p><br/>
	 *<p>通过任务标示获取任务的取消状态</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param key
	 * @return
	 */
	public final boolean isCancelled(String taskPoolKey, String threadKey) {
		if (TextUtils.isEmpty(taskPoolKey) && TextUtils.isEmpty(threadKey)) {
			return false;
		}
		FutureTask<Result> task = mThreadMap.get(taskPoolKey).get(threadKey);
		return task == null ? false : task.isCancelled();
	}

	/**
	 *<p>取消指定任务</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param key
	 * @return  
	 */
	public static final boolean cancleTask(String taskPoolKey, String threadKey) {
		if (TextUtils.isEmpty(threadKey)) {
			return false;
		}
		FutureTask task = mThreadMap.get(taskPoolKey).get(threadKey);
		if (task == null) {
			return false;
		}
		return task.cancel(true);
	}

	/**
	 *<p>任务是否已经完成</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @return
	 */
	public final boolean isDone() {
		return mFuture.isDone();
	}

	/**
	 *<p>任务是否已经完成</p><br/>
	 *<p>通过任务标示获取任务的完成状态</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param key
	 * @return
	 */
	public final boolean isDone(String taskPoolKey, String threadKey) {
		if (TextUtils.isEmpty(taskPoolKey) && TextUtils.isEmpty(threadKey)) {
			return false;
		}
		FutureTask<Result> task = mThreadMap.get(taskPoolKey).get(threadKey);
		return task == null ? false : task.isDone();
	}

	/**
	 *<p>取消任务</p><br/>
	 *<p>
	 *(1)如果任务已完成、或已取消，或者由于某些其他原因而无法取消，则此尝试将失败。<br/>
	 *(2)当调用 cancel 时，如果调用成功，而此任务尚未启动，则此任务将永不运行。<br/>
	 *(3)如果任务已经启动，则 mayInterruptIfRunning 参数确定是否应该以试图停止任务的方式来中断执行此任务的线程
	 *</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param mayInterruptIfRunning 如果正在运行，是否中断
	 * @return
	 */
	public final boolean cancel(boolean mayInterruptIfRunning) {
		return mFuture.cancel(mayInterruptIfRunning);
	}
	/**
	 *<p>进度刷新方法</p><br/>
	 *<p>处理进度结果数据传入UI线程</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param result
	 */
	protected void onProgress(String taskPoolKey,Progress... progress) {
		Message message= sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(taskPoolKey,this,progress));
		message.sendToTarget();
	}

	/**
	 *<p>任务的执行部分</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected abstract Result doInBackground(Params... params) throws Exception;

	/**
	 *<p>任务执行前的相应处理</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	protected final void onTaskBegin() {
		if (mTaskListener == null) {
			return;
		}
		mTaskListener.onTaskBegin();
	}

	/**
	 *<p>任务执行完毕后的相应处理</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param result
	 */
	protected final void onTaskEnd(Result result) {
		if (mTaskListener == null) {
			return;
		}
		mTaskListener.onTaskEnd(result);
	}

	/**
	 *<p>任务取消时的相应处理</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	protected final void onTaskCancelled() {
		if (mTaskListener == null) {
			return;
		}
		mTaskListener.onTaskCancelled();
	}

	protected final void onProgressUpdate(Progress... values) {
		if (mTaskListener == null) {
			return;
		}
		mTaskListener.onProgressUpdate(values);
	}

	protected final void onTaskFailed(TaskFailed fail) {
		if (mTaskListener == null) {
			return;
		}
		mTaskListener.onTaskFailed(fail);
	}

	/**
	 *<p>核心Callable任务处理</p><br/>
	 *<p>处理数据请求及返回结果任务</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	class CoreWorkerRunnable extends WorkerRunnable<Params, Result> {
		private String mTaskPoolKey;

		public CoreWorkerRunnable(String taskPoolKey) {
			mTaskPoolKey = taskPoolKey;
		}

		@Override
		public Result call() throws Exception {
			Message message;
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			Result result = null;
			try {
				result = (Result) doInBackground(mParams);
			} catch (ConnectTimeoutException e) {
				message = sHandler.obtainMessage(MESSAGE_POST_FAILED_TIMEOUT, new AsyncTaskResult<Result>(mTaskPoolKey,
						Task.this, (Result[]) null));
				message.sendToTarget();
				mStatus = Status.UNEXPECTED;
			}catch (HttpError e) {
				message = sHandler.obtainMessage(MESSAGE_POST_NONE_NETWORK, new AsyncTaskResult<Result>(mTaskPoolKey, Task.this,
						(Result[]) null));
				message.sendToTarget();
				mStatus = Status.UNEXPECTED;
			} catch (Exception e) {
				message = sHandler.obtainMessage(MESSAGE_POST_FAILED_NORMAL, new AsyncTaskResult<Result>(mTaskPoolKey, Task.this,
						(Result[]) null));
				message.sendToTarget();
				mStatus = Status.UNEXPECTED;
			}

			return result;
		}

	}

	/**
	 *<p>任务执行</p><br/>
	 *<p>执行任务并用阻塞方法get返回结果</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	class CoreFutureTask extends FutureTask<Result> {
		private String mTaskPoolKey;

		public CoreFutureTask(Callable<Result> callable, String taskPoolKey) {
			super(callable);
			mTaskPoolKey = taskPoolKey;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
			if (mStatus == Status.UNEXPECTED) {
				return;
			}
			Message message;
			Result result = null;
			try {
				result = get();
			} catch (InterruptedException e) {
				
				Logger.getLogger(this).w( e.getMessage());
			} catch (ExecutionException e) {
				throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
			} catch (CancellationException e) {
				message = sHandler.obtainMessage(MESSAGE_POST_CANCEL, new AsyncTaskResult<Result>(mTaskPoolKey, Task.this,
						(Result[]) null));
				message.sendToTarget();
				return;
			} catch (Throwable t) {
				throw new RuntimeException("An error occured while executing " + "doInBackground()", t);
			}

			message = sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(mTaskPoolKey, Task.this, result));
			message.sendToTarget();
		}

	}

	/**
	 *<p>任务的核心部分</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param params
	 * @return
	 */
	public final Task execute(Params... params) {
		if (mStatus != Status.PENDING) {
			switch (mStatus) {
			case RUNNING:
				throw new IllegalStateException("Cannot execute task:" + " the task is already running.");
			case FINISHED:
				throw new IllegalStateException("Cannot execute task:" + " the task has already been executed "
						+ "(a task can be executed only once)");
			}
		}
		if (TextUtils.isEmpty(mTaskKey)) {
			mTaskKey = Long.toHexString(System.nanoTime());
		}
		mStatus = Status.RUNNING;
		mThreadMap.get(mTaskPoolKey).put(mTaskKey, mFuture);
		
		Logger.getLogger(this).w("execute");
		//		sTasks.put(mTaskKey, mFuture);
		onTaskBegin();
		mWorker.mParams = params;
		mTaskPoolMap.get(mTaskPoolKey).execute(mFuture);
		return this;
	}

	/**
	 *<p>内部消息机制</p><br/>
	 *<p>主要处理任务完成，取消的操作</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	private static class InternalHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
			
			Logger.getLogger(this).w("handler:"+msg.what);
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				// There is only one result
				mThreadMap.get(result.mTask.mTaskPoolKey).remove(result.mTask.mTaskKey);
				result.mTask.finish(result.mData[0]);
				break;
			case MESSAGE_POST_PROGRESS:
				result.mTask.onProgressUpdate(result.mData);
				break;
			case MESSAGE_POST_CANCEL:
				mThreadMap.get(result.mTask.mTaskPoolKey).remove(result.mTask.mTaskKey);
				result.mTask.onTaskCancelled();
				break;
			case MESSAGE_POST_FAILED_NORMAL:
				mThreadMap.get(result.mTask.mTaskPoolKey).remove(result.mTask.mTaskKey);
				result.mTask.onTaskFailed(TaskFailed.NORMAL);
				break;
			case MESSAGE_POST_FAILED_TIMEOUT:
				mThreadMap.get(result.mTask.mTaskPoolKey).remove(result.mTask.mTaskKey);
				result.mTask.onTaskFailed(TaskFailed.HTTPTIMEOUT);
				break;
			case MESSAGE_POST_NONE_NETWORK:
				mThreadMap.get(result.mTask.mTaskPoolKey).remove(result.mTask.mTaskKey);
				result.mTask.onTaskFailed(TaskFailed.NONENETWORK);
				break;
			}
		}

	}

	/**
	 *<p>实现了Callable接口，作为FutureTask的参数</p><br/>
	 *<p>类似于Runnable接口，但Runnable没有返回值，无法抛出经过检查的异常。</p>
	 *<p>FutureTask会执行实现类的call方法</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param <Params>
	 * @param <Result>
	 */
	private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
		Params[] mParams;
	}

	/**
	 *<p>获取执行结果</p>
	 *<p>线程会一直处于阻塞状态，直到计算完成</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @return
	 * @throws InterruptedException 阻塞时当前线程如果中断抛出的异常
	 * @throws ExecutionException 计算时抛出的异常
	 */
	public final Result get() throws InterruptedException, ExecutionException {
		return mFuture.get();
	}

	/**
	 *<p>获取线程执行结果</p>
	 *<p>在规定时间内，等待获取的结果，如果超时会抛出异常</p>
	 * @since 1.0.0
	 * @author suenxianhao
	  * @param timeout 取消操作前的超时时间
	 * @param unit 时间单位
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException 如果等待时间超出规定时间，则抛出此异常
	 */
	public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return mFuture.get(timeout, unit);
	}

	private static class AsyncTaskResult<Data> {
		final Task mTask;
		final Data[] mData;
		final String mTaskPoolKey;

		AsyncTaskResult(String taskPoolKey, Task task, Data... data) {
			mTask = task;
			mData = data;
			mTaskPoolKey = taskPoolKey;
		}
	}

	/**
	 *<p>结束任务</p>
	 *<p>在此处与实现类中的onPostExecute做关联，并重置任务状态</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param result
	 */
	private void finish(Result result) {
		if (isCancelled())
			result = null;
		onTaskEnd(result);
		mStatus = Status.FINISHED;
	}

	/**
	 *<p>设置任务标示</p><br/>
	 *<p>用来获取指定任务，在无法获取特定任务的时候，可以通过此值来给任务加个标志</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param mTaskKey
	 */
	public void setTaskKey(String mTaskKey) {
		this.mTaskKey = mTaskKey;
	}

	/**
	 *<p>设置任务监听器</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param taskListener
	 */
	public void setTaskListener(TaskListener<Progress, Result> taskListener) {
		this.mTaskListener = taskListener;
	}

	public enum TaskFailed {
		HTTPTIMEOUT, NORMAL, NONENETWORK
	}
}
