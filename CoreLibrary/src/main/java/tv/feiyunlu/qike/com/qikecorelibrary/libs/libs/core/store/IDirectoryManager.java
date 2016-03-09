/*
 * 文件名：IDirectoryManager.java
 * 描述：文件管理的接口
 * 版本：v1.0.0
 * 日期：2014-3-11
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store;

import java.io.File;

import android.content.Context;

/**
 *<p>文件管理的接口</p><br/>
 *<p>用于文件管理的接口</p>
 * @since 1.0.0
 * @author xiaodong
 */
public interface IDirectoryManager {
	/**
	 * 
	 *<p>根据名字获取 全局公用的文件</p><br/>
	 *<p>根据名字获取 全局公用的文件</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param directoryName 被查找文件名称 
	 * @return
	 */
	public File getGlobalDirectory(String directoryName);

	/**
	 * 获取当前APP对应的私有目录 ，没有会自动创建
	 * 
	 * @param directoryName 被查找文件名称 
	 * @return File 当前目录的文件对象 
	 * @author xiaodong
	 */
	public File getPrivateDirectory(Context context, String directoryName);

	/**
	 * 获取当前APP对应的公有目录，没有会自动创建.
	 * 
	 * @param context
	 *            Context对象
	 * @return File 当前目录的文件对象
	 * @author xiaodong
	 */
	public File getPublicDirectory(String directoryName);
	
	/**
	 *<p>根据传进来的路径返回一个File对象</p><br/>
	 *<p>根据传进来的路径，帮助创建<文件夹> 并返回一个File对象</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param path 需要创建的路径
	 * @return
	 */
	public File getDirectoryByPath(String path);

	/**
	 * 
	 * 清除当前APP对应的私有目录的文件
	 * 
	 * @param context
	 *            Context对象
	 * @return boolean 清除成功或者失败
	 */
	public boolean cleanPrivateDirectory(Context context);

	/**
	 * 清除当前APP对应的公有目录下的所有文件
	 * 
	 * @param context
	 *            Context对象
	 * @return boolean 清除成功或者失败
	 * @author xiaodong
	 */
	public boolean cleanPublicDirectory(Context context);

	/**
	 * 清除当前APP对应的公有以及私有目录下的所有文件
	 * @param context
	 *            Context对象
	 * @return boolean 清除成功或者失败
	 * @author xiaodong
	 */
	public boolean cleanAll(Context context);

}
