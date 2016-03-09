/*
 * 文件名：DirectoryUtils.java
 * 描述：用于创建 删除 目录
 * 版本：v1.0.0
 * 日期：2014-3-11
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store;

import java.io.File;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

/**
 * <p>关于目录的工具类</p> <br/>
 * <p>用于创建 删除 目录 </p>
 * 
 * @since 1.0.0
 * @author xiaodong
 */
public class DirectoryUtils {

	/**
	 * 
	 * <p>删除指定文件</p><br/>
	 * <p>删除指定文件，如果文件是目录，则删除目录下所有的文件</p>
	 * 
	 * @since 1.0.0
	 * @author xiaodong
	 * @param file
	 *            需要被删除的文件
	 * @return
	 */
	public static boolean deleteFile(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null && files.length > 0) {
					for (int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}
				}
			} else {
				file.delete();
			}
		}
		return true;
	}

	/**
	 * 
	 * <p>判断SD卡是否存在</p><br/>
	 * <p>判断SD卡是否存在 判断应用是否加入了sd读写的权限 以及 sd卡是否挂载</p>
	 * 
	 * @since 1.0.0
	 * @author cxd
	 * @param context
	 * @return
	 */
	public static boolean existSDCard(Context context) {
		return haveSDCardPermission(context) && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

	/**
	 * 
	 * <p>判断SD卡是否存在</p><br/>
	 * <p>判断SD卡是否存在 只判断sd卡是否存在 不判断应用是否加入了sd读写的权限</p>
	 * 
	 * @since 1.0.0
	 * @author xiaodong
	 * @return
	 */
	public static boolean existSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

	/**
	 * 
	 *<p>获取内置Rom空间大小</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回rom空间大小
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 
	 *<p>获取存储卡空间大小</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回存储卡空间大小
	 */
	public static long getAllExternalMemorySize() {
		if (existSDCard()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long allBlocks = stat.getBlockCount();
			return allBlocks * blockSize;
		} else {
			return 0;
		}
	}

	/**
	 * 
	 *<p>获取存储卡可用空间大小</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回存储卡可用空间大小
	 */
	public static long getAvailableExternalMemorySize() {
		if (existSDCard()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return 0;
		}
	}

	public static boolean haveSDCardPermission(Context context) {
		try {
			String[] array = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
			for (String string : array) {
				if (string.equals(permission.WRITE_EXTERNAL_STORAGE)) {
					return true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * <p>
	 * 创建文件夹
	 * </p>
	 * <br/>
	 * <p>
	 * 创建文件夹
	 * </p>
	 * 
	 * @since 1.0.0
	 * @author mzw
	 * @param path
	 *            被创建的路径
	 * @return
	 */
	public static File makeDirs(String path) {

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 
	 *<p>获取目录</p><br/>
	 *<p>获取应用的 根 缓存目录</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param mContext
	 * @return
	 */
	public static String getBasePath(Context mContext) {

		long size = getAvailableExternalMemorySize();

		long availSize = size / 1024 * 1024;

		/** 100M内存空间大小 */
		if (availSize >= 100 && DirectoryUtils.existSDCard(mContext)) {
			return Environment.getExternalStorageDirectory().getPath();
		}
		return mContext.getCacheDir().getPath();
	}

}
