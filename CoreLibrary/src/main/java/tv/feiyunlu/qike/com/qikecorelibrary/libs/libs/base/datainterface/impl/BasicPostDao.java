package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


public class BasicPostDao<T> extends AbstractPostDao<T> {

    private Class<T>        clazz;
    private T t;

    public BasicPostDao(String url) {
	super(url);
	if (mCacheLoader == null) {
	    mCacheLoader = buildCacheLoader();
	}
    }

    public BasicPostDao(String url, Class<T> clazz) {
	super(url);
	this.clazz = clazz;
	if (mCacheLoader == null) {
	    mCacheLoader = buildCacheLoader();
	}
    }

    @Override
    public void putParams(String key, String value) {
	super.putParams(key, value);
    }

    @Override
    public void putAllParams(Map<String, Object> paramsMap) {
	super.putAllParams(paramsMap);
    }

    @Override
    public void putAllParams(Object obj) {
	super.putAllParams(obj);
    }

    @Override
    public void onDoInBackgroundProcess(HttpActionProxy action, HashMap<String, String> headers) {

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
		
		
		if(clazz.equals(String.class)){
		    T instants = (T) content;
		    t = instants;
		    return;
		}
		
		if (clazz != null) {
		    T instants = new Gson().fromJson(content, clazz);
		    t = instants;
		}

	    } catch (Throwable e) {
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
