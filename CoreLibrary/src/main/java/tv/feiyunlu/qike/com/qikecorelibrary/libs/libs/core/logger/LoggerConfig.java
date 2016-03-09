package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *<p>TODO(log配置类)</p><br/>
 *<p>TODO (定义log输出的状态，并进行配置)</p>
 * @since 1.0.0
 * @author wangweiqiang
 * @param debug : 是否是debug状态
 * 			open : 是否打开log日志
 * 			store : 是否将log日志存储到sd卡中
 * 			tag : log日志输出的tag标签
 * 			storePath: log日志本地保存的路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoggerConfig {

	public boolean debug() default true;

	public boolean open() default true;

	public boolean store() default false;

	public String tag() default "";

	public String storePath() default "";

}
