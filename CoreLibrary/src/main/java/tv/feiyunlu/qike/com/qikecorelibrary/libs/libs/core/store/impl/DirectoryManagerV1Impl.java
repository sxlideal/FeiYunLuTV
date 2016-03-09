/*
 * 文件名：DirectoryManagerV1Impl.java 
 * 描述：TODO(一句话描述该文件做什么) 
 * 版本：v1.0.0 
 * 日期：2014-3-11
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.DirectoryUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.IDirectoryManager;


/**
 * <p>文件管理的实现类</p><br/>
 * <p>用于创建和删除全局访问目录，私有访问目录，公有目录</p>
 * 
 * @since 1.0.0
 * @author xiaodong
 */
public class DirectoryManagerV1Impl implements IDirectoryManager {

	private static DirectoryManagerV1Impl mInstance;

	private DirectoryManagerV1Impl() {
	}

	public static DirectoryManagerV1Impl getInstance() {
		return mInstance == null ? mInstance = new DirectoryManagerV1Impl() : mInstance;
	}

	/**
	 * 根据 context 参数 获取路径  <br />
	 * 若 没有 则获取默认路径 {@value #defaultGlobalPath}
	 * @param 创建的文件夹名称
	 */
	@Override
	public File getGlobalDirectory(String name) {

		Configuration config = Configuration.getConfiguration();
		String path = config.getGlobalBasePath();

		if (!TextUtils.isEmpty(path)) {
			if (!TextUtils.isEmpty(name)) {

				path += File.separator + name;

			}

			return DirectoryUtils.makeDirs(path);
		}
		return null;
	}

	@Override
	public File getPrivateDirectory(Context context, String directoryName) {

		File cacheFile = context.getCacheDir();
		DirectoryUtils.makeDirs(cacheFile.getPath());
		return cacheFile.getParentFile();
	}

	/**
	 * 根据 context 参数 获取路径  <br />
	 * 若 没有 则获取默认路径 {@value #defaultPublicPath}
	 * @param 创建的文件夹名称
	 */
	@Override
	public File getPublicDirectory(String directoryName) {

		Configuration config = Configuration.getConfiguration();
		String path = config.getPublicBasePath();
		if (!TextUtils.isEmpty(path)) {
			path += directoryName;

			return getDirectoryByPath(path);

		}
		return null;
	}

	@Override
	public File getDirectoryByPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	@Override
	public boolean cleanPrivateDirectory(Context context) {

		File cacheFile = context.getCacheDir();
		return DirectoryUtils.deleteFile(cacheFile.getParentFile());
	}

	@Override
	public boolean cleanPublicDirectory(Context context) {
		//删除SD卡中的公用目录下的文件
		Configuration config = Configuration.getConfiguration();
		String path = config.getPublicBasePath();

		File publiCacheDir = new File(path);
		if (publiCacheDir.exists()) {
			DirectoryUtils.deleteFile(publiCacheDir);
		}

		return true;
	}

	@Override
	public boolean cleanAll(Context context) {

		return cleanPublicDirectory(context) && cleanPrivateDirectory(context);
	}

	public boolean cleanPrivate(Context context, String directoryName) {

		File file = getPrivateDirectory(context, directoryName);
		return DirectoryUtils.deleteFile(file);
	}

	public boolean cleanPublic(String directoryName) {
		
		File file = getPublicDirectory(directoryName);
		return DirectoryUtils.deleteFile(file);
	}

}
