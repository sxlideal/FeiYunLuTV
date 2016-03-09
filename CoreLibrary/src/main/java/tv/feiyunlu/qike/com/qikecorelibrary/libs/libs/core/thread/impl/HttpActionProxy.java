/*
 * 文件名：HttpActionProxy.java
 * 描述：请求参数载体
 * 版本：v1.0.0
 * 日期：2014-3-15
 * 版权：Copyright ? 2014 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl;

import android.graphics.Bitmap;

import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Action;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.IResultProcessor;

/**
 *<p>请求参数载体</p><br/>
 *<p>封装了请求的url、具体参数、缓存对应参数、是否需要缓存、请求方式</p>
 * @since 1.0.0
 * @author sunxianhao
 */
public class HttpActionProxy extends Action {
    
    	private String url;
    	private String urlCacheKey;
	private int requestType ;
	private Map<String, Object> params;
	private IResultProcessor processor;
	private long CacheTTL;
	private CacheEntry cacheEntry;
	private CacheLoad loader;
	
	private byte[] bytes;
	
	private Bitmap bitmap;
	
	
	
	

	public CacheLoad getLoader() {
	    return loader;
	}

	public void setLoader(CacheLoad loader) {
	    this.loader = loader;
	}

	public String getUrlCacheKey() {
	    return urlCacheKey;
	}

	public void setUrlCacheKey(String urlCacheKey) {
	    this.urlCacheKey = urlCacheKey;
	}

	public CacheEntry getCacheEntry() {
	    return cacheEntry;
	}

	public void setCacheEntry(CacheEntry cacheEntry) {
	    this.cacheEntry = cacheEntry;
	}

	public long getCacheTTL() {
	    return CacheTTL;
	}

	public void setCacheTTL(long cacheTTL) {
	    CacheTTL = cacheTTL;
	}

	public byte[] getBytes() {
	    return bytes;
	}

	public void setBytes(byte[] bytes) {
	    this.bytes = bytes;
	}

	public Bitmap getBitmap() {
	    return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
	    this.bitmap = bitmap;
	}

	public IResultProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(IResultProcessor processor) {
		this.processor = processor;
	}



	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	
	public void setParas(Map<String, Object> params) {
		this.params = params;
	}


	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, Object> getParams() {
		return params;
	}

	public void putParams(String key, String value) {
		if (key != null && !"".equals(key)) {
		    params.put(key, value);
		}
	}
}
