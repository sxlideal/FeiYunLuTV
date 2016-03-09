package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger;

/**
 * 
 *<p>TODO(log输入接口)</p><br/>
 *<p>TODO (主要定义log输出的抽象方法)</p>
 * @since 1.0.0
 * @author wangweiqiang
 * 
 */
public interface ILogger {

	public void i(Object message);

	public void d(Object message);

	public void e(Object message);

	public void v(Object message);

	public void w(Object message);

}
