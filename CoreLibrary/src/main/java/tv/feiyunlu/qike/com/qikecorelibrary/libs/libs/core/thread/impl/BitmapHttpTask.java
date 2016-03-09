package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.image.ImageDownloader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.IResultProcessor;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;

public class BitmapHttpTask extends Task<HttpActionProxy, Long, Void> {

    private static final int    CORE_POOL_MAX  = 20;
    private static final String CORE_POOL_NAME = BitmapHttpTask.class.getName();

    public BitmapHttpTask() {
	super(CORE_POOL_MAX, CORE_POOL_NAME);
    }

    @Override
    protected Void doInBackground(HttpActionProxy... params) throws Exception {
		
	Void result = null;

	    HttpActionProxy proxy = params[0];
	    
	    CacheLoad cacheLoader = proxy.getLoader();
	    IResultProcessor processor = proxy.getProcessor();
	    CacheEntry cacheEntry = cacheLoader.get(proxy.getUrlCacheKey());
	    
	    cacheEntry.etag = proxy.getUrlCacheKey();

	    if (cacheEntry.ttl != 0 && !cacheEntry.isExpired()) {
		
		proxy.setCacheEntry(cacheEntry);//如果请求的是bitmap， 内存中取到的话，返回entry.bitmap;如果磁盘取到的话，返回entry.data
		processor.onDoInBackgroundProcess(proxy,null);
		
	    } else {
		
	    /*******************************       缓存无效，请求网络部分(包括本地图片)      *********************************/		
		
		Response<byte[]> response = ImageDownloader.getImage(proxy.getUrl());
		
		cacheEntry.ttl = proxy.getCacheTTL() + System.currentTimeMillis();//设置缓存失效时间
		cacheEntry.data = (response.getResult());
		
		proxy.setCacheEntry(cacheEntry);//缓存包装对象 给数据bean对象，供传递解析用
		cacheLoader.put(proxy.getUrlCacheKey(), cacheEntry);//保存缓存实体，如果为bitmap则为空实现，不保存完整缓存，只在解析后保存裁剪大小之后的缓存
		processor.onDoInBackgroundProcess(proxy,response.getHeaders());//对字节的解析，如果为bitmap则在此处保存缓存
	    }
	
	
	return result;
    }

}
