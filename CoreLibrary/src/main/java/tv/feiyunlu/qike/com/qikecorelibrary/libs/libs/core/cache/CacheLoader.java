package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache;

import android.os.Environment;

import java.io.File;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.impl.DiskBasedCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.AbstractMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.IMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.impl.WeakMemoryCache;


/**
 * 
 *<p>对内存缓存和磁盘缓存的二次封装</p><br/>
 *<p>一般来说拥有两个实例，磁盘缓存实例，用以保存接口数据，硬盘和内存缓存实例，用以保存图片</p>
 * @since 1.0.0
 * @author bigpie
 */
public class CacheLoader implements CacheLoad {

    public static CacheLoader cacheLoader;
	  
    private IDiskCache mDiskCache;
    private IMemoryCache<String, CacheEntry> mMemoryCache;

    private long cacheTime;
    
    
    private CacheLoader(){
	
	String cachePath = Environment.getExternalStorageDirectory() + "/muzhiwan/imageCache";
	
	mDiskCache = new DiskBasedCache(new File(cachePath));
	mMemoryCache = new WeakMemoryCache();
	cacheTime = 3*60*60*1000;
	mDiskCache.initialize();
	
    }
    
    
    
    public long getCacheTime() {
        return cacheTime;
    }



    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }



    public synchronized static CacheLoader getInstants(){
	if(cacheLoader == null){
	    cacheLoader = new CacheLoader();
	}
	return cacheLoader;
    }
    
    /**
     * 包含内存缓存和磁盘缓存的构造方法
     * @param diskCache
     * @param memoryCache
     */
    public CacheLoader(IDiskCache diskCache, IMemoryCache<String, CacheEntry> memoryCache) {
	
	this.mDiskCache = diskCache;
	this.mMemoryCache = memoryCache;
    }
    /**
     *  只有磁盘缓存的构造方法
     * @param diskCache
     */
    public CacheLoader(IDiskCache diskCache) {

	this(diskCache, null);
    }
    /**
     *  只有内存缓存的构造方法(不推荐)
     * @param diskCache
     */
    public CacheLoader(AbstractMemoryCache<String, CacheEntry> memoryCache){
	this(null, memoryCache);
    }
    
    public IMemoryCache<String, CacheEntry> getMemoryCache(){
	return mMemoryCache;
    }

    public IDiskCache getDiskCache(){
	return mDiskCache;
    }
    
    public void setNoCache(){
	mDiskCache = null;
	mMemoryCache = null;
    }
    
    /**
     * 
     *<p>缓存的获取方法</p><br/>
     *<p>缓存的获取方法，根据构造方法来判断是否拥有内存缓存功能</p>
     * @since 1.0.0
     * @author bigpie
     * @param String key 
     * @return {@link CacheEntry}缓存的包装类
     */
    public CacheEntry get(String key){
	
	CacheEntry entry = null;
	
	if(mMemoryCache!=null){
	   entry = mMemoryCache.get(key);
	}
	
	if(entry == null && mDiskCache!= null){
	    entry = mDiskCache.get(key);
	}
	//如果内存缓存和磁盘缓存都无法找到数据实体，则返回一个失效的缓存实体
	if(entry == null){
	    entry = new CacheEntry();
	    entry.ttl = 0;
	}
	
	return entry;
    }
    
    public void put(String key, CacheEntry entry){
	
	if(mMemoryCache != null){
	    mMemoryCache.put(key, entry);
	}
	
	if(mDiskCache != null){
	    mDiskCache.put(key, entry);
	}
    }
    
    
    
}
