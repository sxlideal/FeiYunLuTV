package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache;

import java.util.Collections;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * 
 *<p>缓存的封装类</p><br/>
 *<p>TODO 改名叫 ResponseEntry</p>
 * @since 1.0.0
 * @author bigpie
 */
public class CacheEntry {
    
    //缓存的数据段
    public byte[] data;
    
    //缓存的图片
    public Bitmap bitmap;

    //扩展域
    public String etag;

    //服务器响应的服务器时间
    public long serverDate;

    //缓存的过期时间
    public long ttl;

    /** Immutable response headers as received from server; must be non-null. */
    public Map<String, String> responseHeaders = Collections.emptyMap();

    /** True if the entry is expired. */
    public boolean isExpired() {
        return this.ttl < System.currentTimeMillis();
    }

}
