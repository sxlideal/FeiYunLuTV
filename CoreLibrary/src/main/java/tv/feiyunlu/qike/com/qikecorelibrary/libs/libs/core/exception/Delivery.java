package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception;

/**
 * 
 *<p>TODO(异常监听器)</p><br/>
 *<p>TODO (捕获异常信息后用户自定义的处理逻辑)</p>
 * @since 1.0.0
 * @author wangweiqiang
 */
public interface Delivery {
	public boolean onCatch(Level level, Throwable ex);
}
