package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;

/**
 * 
 *<p>基础get请求</p><br/>
 *<p>对应无分页需求的普通get请求</p>
 * @since 1.0.0
 * @author bigpie
 */
public class BasicGetDao<T> extends AbstractGetDao<T> {

	private Class<T> clazz = null;
	private T t;

	public BasicGetDao(String url, Class<T> clazz) {
		super(url);
		this.clazz = clazz;
		
		if (mCacheLoader == null) {
		    mCacheLoader = buildCacheLoader();
		}

	}

	/**
	 * 这个构造函数只返回String
	 * @param url
	 */
	public BasicGetDao(String url) {
		super(url);
		
		if (mCacheLoader == null) {
		    mCacheLoader = buildCacheLoader();
		}

	}

	@Override
	protected void _onTaskCancelled() {
		super._onTaskCancelled();
	}

	@Override
	protected void _onTaskFailed(Task.TaskFailed failed) {
		super._onTaskFailed(failed);
	}

	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action,
			HashMap<String, String> headers) {
		if (isServerDataError(headers)) {
			return;
		}
			CacheEntry entry = action.getCacheEntry();
		try {

			content = decodeResponse(entry.data);
		} catch (Throwable e) {
			e.printStackTrace();
			mResult.setCode(ParamsContants.ERROR_DECODE);
		}

		if (!TextUtils.isEmpty(content)) {

			try {

				if (clazz!= null && clazz.equals(String.class)) {
					T instants = (T) content;
					t = instants;
					return;
				}

				if (clazz != null) {
					T instants = new Gson().fromJson(content, clazz);
					t = instants;
				}
			} catch (Exception e) {
				e.printStackTrace();
				mResult.setCode(ParamsContants.ERROR_PARSE);
			}
		} else {
			mResult.setCode(ParamsContants.ERROR_PARSE);
		}

	}

	public T getItem() {
		return t;
	}

	@Override
        protected CacheLoad buildCacheLoader() {
	    
	    Configuration config = Configuration.getConfiguration();
	    CacheLoad cacheLoad = new CacheLoader(config.getDiskCache());
	    return cacheLoad;
        }

}
