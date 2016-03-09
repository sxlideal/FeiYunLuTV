package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/**
 * 
 *<p>配置全局属性，可加入Activity，Applicaiton上</p><br/>
 * @since 1.0.0
 * @author xujiaoyong
 */
public @interface Config {
	
	//配置全局路径，可使用占位符，$sdcard代表存储卡路径
	String globalBasePath();
	//配置应用路径，可使用占位符，$sdcard代表存储卡路径，$global代表引用globalBasePath
	String publicBasePath();
	//应用正式签名，取hashcode
	int sign();
	
}
