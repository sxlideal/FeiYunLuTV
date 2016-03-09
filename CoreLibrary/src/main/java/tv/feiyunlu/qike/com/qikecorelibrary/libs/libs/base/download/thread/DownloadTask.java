package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.domain.DownloadPaths;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpError;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpUtil;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Request;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.HttpClientStack;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.StreamRequest;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.Logger;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger.LoggerConfig;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.DirectoryUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.TaskListener;


@LoggerConfig(tag = "mzw_download")
public class DownloadTask<T> extends Task<Void, Long, DownloadResult> {
	private static final String CORE_DOWNLOAD_TASK_NAME = DownloadTask.class
			.getName();
	private static final int CORE_DOWNLOAD_POOL_SIZE = 20;

	private static final int RATE = 1000;

	private static final int STATUS_STOP = 1;
	private static final int STATUS_START = 2;
	private static final int STATUS_CANCEL = 3;

	private DownloadPaths mPath;
	private Long mLength = 0L;
	private Long mCurrentIndex = 0L;
	//	private int mErrorcode;
	private String mRemark;
	private String mSavepath;
	private AtomicInteger mStatus = new AtomicInteger(STATUS_STOP);
	private ObserverThread mObserver;
	private Request<HttpEntity> mRequest;
	private FileDownloadListener<T> mListener;
	private DownloadTaskHandler mTaskHandler;
	private T mData;
	private int mStatusCode;

	private long mRate = RATE;

	private DownloadTask(DownloadPaths path, String savepath, int corePoolSize,
			String ThreadKey) {
		super(corePoolSize, ThreadKey);
		mTaskHandler = new DownloadTaskHandler();
		setTaskListener(mTaskHandler);
		mPath = path;
		mSavepath = savepath;
	}

	public String getSavepath() {
		return mSavepath;
	}

	public void setSavepath(String mSavepath) {
		this.mSavepath = mSavepath;
	}

	public long getRate() {
		return mRate;
	}

	public void setRate(long mRate) {
		this.mRate = mRate;
	}

	public void start() {
		if (!isRunning()) {
			execute();
		}
	}

	public DownloadTask(DownloadPaths path, String savepath) {
		this(path, savepath, CORE_DOWNLOAD_POOL_SIZE, CORE_DOWNLOAD_TASK_NAME);
	}

	public void setDownloadListener(FileDownloadListener<T> listener) {
		mListener = listener;
	}

	public void setData(T data) {
		mData = data;
	}

	public T getData() {
		return mData;
	}

	public void setLength(long length) {
		mLength = length;
	}

	public boolean isRunning() {
		return mStatus.get() == STATUS_START;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getDrive( HttpClient client) {
		try {
			Class baiduDriveClazz = Class
					.forName("com.muzhiwan.libs.base.download.drive.BaiduDrive");
			Constructor constructor = baiduDriveClazz.getConstructor(HttpClient.class);
			Object baiduDrive = constructor.newInstance(client);

			return baiduDrive;

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getDownloadURL(Object drive, String url) {
		try {
			Class driveClazz = drive.getClass();
			Method method = driveClazz
					.getMethod("getDownloadUrl", String.class);
			String result = (String) method.invoke(drive, url);
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isShowVCode(Object drive) {
		try {
			Class driveClazz = drive.getClass();
			Method method = driveClazz
					.getMethod("isShowVcode");
			Boolean showVCode = (Boolean) method.invoke(drive);
			return showVCode;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String verifyVCode(Object drive,String vcode) {
		try {
			Class driveClazz = drive.getClass();
			Method method = driveClazz
					.getMethod("verifyVcode",String.class);
			String realUrl = (String) method.invoke(drive,vcode);
			return realUrl;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String loadDriveUrl(String url) {
		Object drive = getDrive(
				HttpUtil.getClient(3, 30 * 1000));
		String secondURL = null;
		if (drive != null) {
			secondURL = getDownloadURL(drive, url);
			
			Log.i("mzw_download_url", "url:" + secondURL);
			if (!TextUtils.isEmpty(secondURL)) {
				if (isShowVCode(drive)) {
					StringBuilder result = new StringBuilder();
					String vcodePath = secondURL;
					secondURL= null;
					mListener.onShowVCode(mData,vcodePath, result);
					synchronized (result) {
						try {
							Log.i("mzw_download_url", "wait");
							result.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String vcode = result.toString();
					
					Log.i("mzw_download_url", "vcode:" + vcode);
					if (!TextUtils.isEmpty(vcode)) {
						secondURL = verifyVCode(drive, vcode);
						if (TextUtils.isEmpty(secondURL)) {
							return "error vcode";
						}
						Log.i("mzw_download_url", "verify url:" + secondURL);
					}else {
						return "empty vcode";
					}
				}
			}
		}
		Log.i("mzw_download_url", "secondURL:" + secondURL);
		if (TextUtils.isEmpty(secondURL)) {
			return mPath.getBackup();
		} else {
			return secondURL;
		}
	}

	@Override
	protected DownloadResult doInBackground(Void... params) throws Exception {
		mStatus.set(STATUS_START);

		mStatusCode = 0;
		DownloadResult result = new DownloadResult();
		InputStream ins = null;
		RandomAccessFile raf = null;
		try {

			checkFileSystem();
			File file = new File(mSavepath);
			mCurrentIndex = initFileLength(file);
			boolean complete = checkFileComplete(mCurrentIndex);
			if (!complete) {
				
				String url = mPath.getUrl();
				boolean parsable = mPath.isParse();
				if (parsable) {
					
					String secondUrl = null;
					while (true) {
						if (!isRunning()) {
							DownloadException ex = new DownloadException(DownloadResult.ERROR_CODE_LOCAL_VCODE);
							throw ex;
						}
						secondUrl = loadDriveUrl(url);
						if (!secondUrl.equals("error vcode")) {
							url = secondUrl;
							break;
						}
					}
					if( secondUrl.equals("empty vcode") ){
						DownloadException ex = new DownloadException(DownloadResult.ERROR_CODE_LOCAL_VCODE);
						throw ex;
					}
				}
				
				mRequest = new StreamRequest(url);
				if (mCurrentIndex > 0) {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("Range", "bytes=" + mCurrentIndex + "-");
					mRequest.setHeaders(headers);
				}

				Response<HttpEntity> response = HttpClientStack.getInstance()
						.performRequest(mRequest);

				mStatusCode = response.getmStatus();

				HttpEntity entity = response.getResult();
				checkEntity(entity);

				ins = new BufferedInputStream(entity.getContent());
				int len = 0;
				byte[] buff = new byte[1024 * 8];
				raf = new RandomAccessFile(file, "rws");
				raf.seek(mCurrentIndex);
				mObserver = new ObserverThread(mRate);
				mListener.onStarted(mData);
				mObserver.start();

				while (isRunning() && (len = ins.read(buff)) != -1) {
					raf.write(buff, 0, len);
					synchronized (this) {
						mCurrentIndex += len;
					}

				}
				checkFileLength(mSavepath);
			}
			if (isRunning()) {
				mListener.onCompletePrepare(mData);
			}

		} catch (Throwable e) {
			processsException(result, e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (mRequest != null) {
				try {
					mRequest.cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private void checkFileLength(String savepath) throws DownloadException {
		File file = new File(savepath);
		if (mLength <= 0) {
			throw new DownloadException(
					DownloadResult.ERROR_CODE_NETWORK_NO_LENGTH);
		}
		if (file == null || !file.exists() || file.length() != mLength) {
			throw new DownloadException(
					DownloadResult.ERROR_CODE_NETWORK_NOT_FULL);
		}

	}

	private void checkEntity(HttpEntity entity) throws DownloadException {
		if (entity == null) {
			throw new DownloadException(
					DownloadResult.ERROR_CODE_NETWORK_ENTITY_NULL);
		}
		if (mCurrentIndex == 0) {
			mLength = entity.getContentLength();
			if (mLength <= 0) {
				throw new DownloadException(
						DownloadResult.ERROR_CODE_NETWORK_NO_LENGTH);
			} else {
				onLoadedLength(mLength);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void processsException(DownloadResult result, Throwable e) {
		Class clazz = e.getClass();
		int errorCode = DownloadResult.ERROR_CODE_NETWORK_OTHER;
		String errorMsg = e.getMessage();
		if (clazz == DownloadException.class) {
			DownloadException downloadException = (DownloadException) e;
			errorCode = downloadException.getError();
			errorMsg = toString();
		} else if (clazz == HttpError.class) {
			HttpError httpError = (HttpError) e;
			errorCode = checkHttpError(httpError);

		} else if (clazz == FileNotFoundException.class) {
			errorCode = DownloadResult.ERROR_CODE_NETWORK_STATUS_400;
		} else {
			String text = errorMsg.toLowerCase();
			if (text.contains("space")) {
				errorCode = DownloadResult.ERROR_CODE_LOCAL_SDCARD_NOTSPACE;
			}
			if (text.contains("reset") || text.contains("unreach")) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
			}
			if (text.contains("host")) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_NOT_HOST;
			}
			if (text.contains("out")) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_TIMEOUT;
			}
			if (text.contains("refused")) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
			}
			if (text.contains("connection")) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
			}
			if (e instanceof UnknownHostException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "unknown host ";
			} else if (e instanceof SocketTimeoutException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "socket timeout ";
			} else if (e instanceof ConnectionClosedException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "connection close ";
			} else if (e instanceof ConnectTimeoutException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "connection timeout ";
			} else if (e instanceof ConnectException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "connection exception ";
			} else if (e instanceof NoRouteToHostException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "no route to host ";
			} else if (e instanceof SocketException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "socket exception ";
			} else if (e instanceof ProtocolException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "protocol exception ";
			} else if (e instanceof ConnectionPoolTimeoutException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "connection pool timeout ";
			} else if (e instanceof HttpHostConnectException) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_CONN_ERROR;
				errorMsg = "http host connection exception ";
			}
		}

		result.setCode(errorCode);
		result.setMsg(errorMsg);
	}

	private int checkHttpError(HttpError error) {
		int errorCode = DownloadResult.ERROR_CODE_NETWORK_OTHER;
		int httpErrorCode = error.getError();
		if (httpErrorCode == HttpError.ERR_METHOD) {
			errorCode = DownloadResult.ERROR_CODE_NETWORK_ENTITY_NULL;
		} else if (httpErrorCode == HttpError.ERR_NORESPONSE) {
			errorCode = DownloadResult.ERROR_CODE_NETWORK_ENTITY_NULL;
		} else {
			int status = error.getStatus();

			mStatusCode = status;
			if (status >= 500) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_STATUS_500;
			} else if (status >= 400) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_STATUS_400;
			} else if (status < 200) {
				errorCode = DownloadResult.ERROR_CODE_NETWORK_STATUS_OTHER;
			}
		}
		return errorCode;
	}

	private void checkFileSystem() throws DownloadException {
		if (!DirectoryUtils.existSDCard()) {
			throw new DownloadException(
					DownloadResult.ERROR_CODE_LOCAL_SDCARD_UNMOUNTED);
		}
		if (DirectoryUtils.getAvailableExternalMemorySize() < 10) {

			throw new DownloadException(
					DownloadResult.ERROR_CODE_LOCAL_SDCARD_NOTSPACE);
		}
		if (TextUtils.isEmpty(mSavepath)) {
			throw new DownloadException(
					DownloadResult.ERROR_CODE_LOCAL_SAVEPATHNULL);
		}
	}

	private long initFileLength(File file) throws DownloadException {
		if (file == null) {
			throw new DownloadException(DownloadResult.ERROR_CODE_LOCAL_MKDIRS);
		}
		if (file.exists()) {
			return file.length();
		} else {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				boolean success = dir.mkdirs();
				if (!success) {
					throw new DownloadException(
							DownloadResult.ERROR_CODE_LOCAL_MKDIRS);
				}
			}
			
		}
		return 0;
	}

	private boolean checkFileComplete(long currentIndex)
			throws DownloadException {
		if (mLength > 0) {
			if (currentIndex == mLength) {
				return true;
			}
			long needSpace = mLength - currentIndex;
			if (needSpace >= DirectoryUtils.getAvailableExternalMemorySize()) {
				throw new DownloadException(
						DownloadResult.ERROR_CODE_LOCAL_SDCARD_NOTSPACE);
			}
		}
		return false;
	}

	public synchronized void stop() {
		stopObserver();
		mListener.onStoping(mData);
		mStatus.set(STATUS_STOP);
		try {
			mRequest.cancel();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public synchronized void cancel() {
		stopObserver();
		mListener.onCanceling(mData);
		mStatus.set(STATUS_CANCEL);
		try {
			mRequest.cancel();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private static class DownloadException extends Exception {
		private static final long serialVersionUID = -276319779685130527L;
		private int mError;

		public DownloadException(int error) {
			super();
			this.mError = error;
		}

		public int getError() {
			return mError;
		}
	}

	private class ObserverThread extends Thread {
		private boolean mStop;
		private long mWaittime;

		public ObserverThread(long mWaittime) {
			super();
			this.mWaittime = mWaittime;
		}

		@Override
		public void run() {
			while (!mStop) {

				try {
					mListener.onProgressPrepare(mCurrentIndex, mLength, mData);
					synchronized (DownloadTask.this) {
						onProgress(CORE_DOWNLOAD_TASK_NAME, mCurrentIndex);
					}
					Thread.sleep(mWaittime);
				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
		}

		public void cancel() {
			synchronized (DownloadTask.this) {
				mStop = true;
			}

		}
	}

	private class DownloadTaskHandler implements
			TaskListener<Long, DownloadResult> {

		@Override
		public void onTaskBegin() {
			mListener.onPrepare(mData);
			mListener.onStarting(mData);
		}

		@Override
		public void onTaskCancelled() {
			Logger.getLogger(DownloadTask.this).i("onTaskCancelled");
			stopObserver();
			mListener.onCanceled(mData);
		}

		@Override
		public void onProgressUpdate(Long... values) {
			Logger.getLogger(DownloadTask.this).i(
					"onProgressUpdate:" + values[0] + ",length:" + mLength);

			mListener.onProgress(values[0], mLength, mData);
		}

		@Override
		public void onTaskFailed(
				TaskFailed failed) {
			Logger.getLogger(DownloadTask.this).i("onTaskFailed");
			stopObserver();

			mListener.onError(DownloadResult.ERROR_CODE_TASK_FAILED,
					mStatusCode, null, mData);
		}

		@Override
		public void onTaskEnd(DownloadResult data) {
			stopObserver();
			int code = data.getCode();
			int status = mStatus.get();
			if (status == STATUS_CANCEL) {
				mListener.onStoped(mData);
				mListener.onCanceled(mData);
			} else if (status == STATUS_STOP) {
				mListener.onStoped(mData);
			} else {
				if (code != DownloadResult.SUCCESS) {
					mListener.onError(data.getCode(), mStatusCode, null, mData);
				} else {
					mListener.onComplete(mData);
				}
			}
			Logger.getLogger(DownloadTask.this).i("onTaskEnd");

		}

	}

	private void stopObserver() {
		if (mObserver != null) {
			mObserver.cancel();
		}
	}

	private void onLoadedLength(long length) {
		if (mListener != null) {
			mListener.onLoadContentLength(length, mData);
		}
	}

	@Override
	public String toString() {
		return "DownloadTask [mUrl=" + mPath.getUrl() + ", mLength=" + mLength
				+ ", mCurrentIndex=" + mCurrentIndex + ", mRemark=" + mRemark
				+ ", mSavepath=" + mSavepath + ", mData=" + mData + "]";
	}

}
