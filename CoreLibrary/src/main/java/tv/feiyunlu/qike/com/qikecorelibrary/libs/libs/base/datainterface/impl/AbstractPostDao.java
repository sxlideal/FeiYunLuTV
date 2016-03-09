package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.BaseLoadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.IDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.IResultProcessor;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.TaskListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpTask;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils.URLParamsUtils;


public abstract class AbstractPostDao<T> implements IDao<T>, IResultProcessor {

	protected String URL;
	    protected CacheLoad mCacheLoader;
	protected HttpActionProxy proxy;
	protected Task<HttpActionProxy, Long, Void> task;
	private Map<String, Object> mParamsMap;
	protected BaseLoadListener loadListener;
	private boolean mAddVerifyKey;
	private boolean mNeedDecodeResponse;
	private int retryCount = 0;
	protected String content;
	protected Result mResult;

	public AbstractPostDao(String url) {
		this.URL = url;
		proxy = new HttpActionProxy();
			proxy.setCacheTTL(Configuration.getConfiguration().getCacheTTL());

	}

	@Override
	public void asyncDoAPI() {

		verifyKey();

		mResult = new Result();
			proxy.setLoader(mCacheLoader);
		proxy.setParas(mParamsMap);
		proxy.setProcessor(this);
		proxy.setRequestType(2);
		proxy.setUrl(URL);
		// 线程池开启http任务

			if (mParamsMap != null && mParamsMap.size() != 0) {
			    proxy.setUrlCacheKey(URL + mParamsMap.hashCode());
			} else {
			    proxy.setUrlCacheKey(URL);
			}
		task = getTask() == null ? new HttpTask() : getTask();
		task.setTaskListener(new TaskListenerImpl());
		task.execute(proxy);
	}
	protected Task<HttpActionProxy, Long, Void> getTask() {
		return null;
	}
	    protected abstract CacheLoad buildCacheLoader();

	    @Override
	    public void setCacheTTL(long ttl) {
		proxy.setCacheTTL(ttl);
	    }

	@Override
	public void taskCancel() {
		task.cancel(true);
	}

	public void putParams(String key, String value) {
		if (this.mParamsMap == null) {
			this.mParamsMap = new HashMap<String, Object>();
		}
		mParamsMap.put(key, value);
	}

	public void putAllParams(Map<String, Object> paramsMap) {
		if (this.mParamsMap == null) {
			this.mParamsMap = new HashMap<String, Object>();
		}

		this.mParamsMap.putAll(paramsMap);
	}

	public void putAllParams(Object obj) {

		if (this.mParamsMap == null) {
			this.mParamsMap = new HashMap<String, Object>();
		}

		this.mParamsMap.putAll(paserParamsFromObj(obj));

	}

	@Override
	public void needVerifyKey(boolean bool) {
		this.mAddVerifyKey = bool;
	}

	@Override
	public void needDecodeResponse(boolean bool) {
		this.mNeedDecodeResponse = bool;
	}

	@Override
	public String decodeResponse(byte[] bytes) throws Throwable {

		String buff = "";

		if (mNeedDecodeResponse) {
			buff = SecurityUtils.decodeResponse(new String(bytes, "UTF-8"));
		} else {
			buff = new String(bytes, "utf-8");
		}
		return buff;
	}

	private Map<String, Object> paserParamsFromObj(Object obj) {

		Gson gson = new Gson();
		Map<String, Object> map = gson.fromJson(gson.toJson(obj), new TypeToken<Map<String, Object>>() {
		}.getType());

		return map;
	}

	protected boolean isServerDataError(HashMap<String, String> headers) {
	    
	    if(headers == null){
		return false;
	    }
	    
		String codeStr = headers.get("code");
		String msgStr = headers.get("errormsg");
		mResult = new Result();
		if (!TextUtils.isEmpty(codeStr)) {
			mResult.setCode(Integer.valueOf(codeStr));
		}
		mResult.setErrmsg(msgStr);
		return mResult.getCode() != 0;

	}

	@Override
	public void registerListener(BaseLoadListener listener) {
		this.loadListener = listener;
	}

	@Override
	public void unRegisterListener(BaseLoadListener listener) {
		this.loadListener = null;
	}

	@Override
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	private void verifyKey() {
		if (!mAddVerifyKey) {
			return;
		}
		if (mParamsMap != null && mParamsMap.size() != 0) {
			mParamsMap.put("key", URLParamsUtils.initGeneralKey(URL, mParamsMap));
		}
	}

	protected void _onTaskBegin() {
		if (loadListener != null) {
			loadListener.onPrepare();
		}
	}

	protected void _onTaskEnd() {

		if (loadListener != null) {//如果errorCode不等于空 则返回
			if (mResult != null && mResult.getCode() != 0) {
				loadListener.onError(mResult);
				return;
			}

			loadListener.onComplete(ResultType.SUCCESS);
		}
	}

	protected void _onTaskCancelled() {
		if (loadListener != null) {
			loadListener.onCancel();
		}
	}

	protected void _onTaskFailed(Task.TaskFailed failed) {

		if (retryCount > 0) {//重试机制
			retryCount--;
			asyncDoAPI();
			return;
		}

		switch (failed) {
		case HTTPTIMEOUT:

			mResult.setCode(Result.HTTPTIMEOUT);
			break;
		case NONENETWORK:

			mResult.setCode(Result.NONENETWORK);
			break;
		case NORMAL:

			mResult.setCode(Result.NORMAL);
			break;

		default:
			break;
		}

		if (loadListener != null) {
			loadListener.onError(mResult);
		}
	}
	
	protected void _onProgress(Long progress, Long length){
	    
	    if (loadListener != null) {
		loadListener.onProgress(progress, length);
	    }
	}

	private class TaskListenerImpl implements TaskListener<Long, Void> {

		@Override
		public void onTaskBegin() {
			_onTaskBegin();
		}

		@Override
		public void onTaskEnd(Void v) {
			_onTaskEnd();
		}

		@Override
		public void onTaskCancelled() {
			_onTaskCancelled();
		}

		@Override
		public void onProgressUpdate(Long... values) {
		    _onProgress(values[0], values[1]);
		}

		@Override
		public void onTaskFailed(Task.TaskFailed failed) {
			_onTaskFailed(failed);
		}

	}
	
	@Override
	    public void isDontLoadFromCache(boolean bool) {
		proxy.setCacheTTL(0);
	    }

	    @Override
	    public void setNoCache() {
		mCacheLoader.setNoCache();
	    }
	    
	    

}
