package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;

public interface CacheLoad {

    public CacheEntry get(String key);
    public void put(String key, CacheEntry entry);
    public void setNoCache();
}
