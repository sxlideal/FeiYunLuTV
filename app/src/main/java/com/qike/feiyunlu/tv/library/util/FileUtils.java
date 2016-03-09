package com.qike.feiyunlu.tv.library.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * <p>文件工具类<p/>
 * @author zhulingjun
 */
public class FileUtils {

	public static void saveImage(final Bitmap bitmap, final String filepath) {

		BufferedOutputStream bos;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(filepath));
			bitmap.compress(CompressFormat.PNG, 90, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	private static final String userIcon = "usericon";

	public static File getImageDir(Context context) {

		if (isExternalPathExist(context)) {

			//			String directoryPath = Environment.getExternalStorageDirectory().getPath() + suffixPath;
			String directoryPath = context.getExternalFilesDir(userIcon).getAbsolutePath();
			File dirFile = new File(directoryPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			return dirFile;

		} else {
			File cachedir = context.getCacheDir().getAbsoluteFile();
			return cachedir;
		}

	}

	public static boolean isExternalPathExist(Context context) {
		return haveMountedPermission(context) && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static boolean haveMountedPermission(Context context) {

		try {
			String[] array = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;

			for (String permi : array) {
				if (permi.equals(permission.WRITE_EXTERNAL_STORAGE))
					return true;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * <p>读取文件到String<p/>
	 * @param filePath
	 * @return 字符串
	 * @author zhulingjun
	 */
	public static String readFile(String filePath) {
		ByteArrayOutputStream bos = null;
		FileInputStream fis = null;
		File file = new File(filePath);
		try {
			bos = new ByteArrayOutputStream();
			fis = new FileInputStream(file);
			byte[] buff = new byte[2048];
			for (int len = 0; (len = fis.read(buff)) != -1;) {
				bos.write(buff, 0, len);
			}
			String result = bos.toString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	/**
	 * 
	 *<p>TODO(文件另存为)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param src 
	 * @param des
	 */
	public static void fileSaveAs(String src, String des) {

		copyFile(src, des);
		deleteFile(new File(src));

	}

	/**
	 * 
	 *<p>TODO()</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param file
	 */
	public static void deleteFile1(File file) {

		boolean result = false;
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile1(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}

		} else {
			System.out.println("文件不存在！" + "\n");
		}
	}

	/**
	 * 
	 *<p>TODO(复制文件)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param src 源文件
	 * @param des 复制文件
	 * @return true 成功  false 失败
	 */
	public static boolean copyFile(String src, String des) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(des);
			byte[] bt = new byte[1024];
			int readNum = 0;
			while ((readNum = fis.read(bt)) != -1) {
				fos.write(bt, 0, bt.length);
			}
			fis.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException f) {
				// TODO
			}
		}

	}

	/**
	* 加载本地图片
	* 
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * <p>删除文件<p/>
	 * @param file
	 * @author zhulingjun
	 */
	public static void deleteFile(File file) {
		try {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}
				}
			}
			file.delete();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容  
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 80f;//  
		float ww = 48f;//  
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置采样率  

		newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
		newOpts.inPurgeable = true;// 同时设置才会有效  
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		//	      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩  
		//其实是无效的,大家尽管尝试  
		return bitmap;
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
			baos.reset();//重置baos即清空baos  
			image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10  
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
		return bitmap;
	}

	/**
	 * <p>删除非apk的文件<p/>
	 * @param file
	 * @author zhulingjun
	 */
	public static void deleteFileNotApk(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					deleteFileNotApk(files[i]);
				}
			}
		} else {
			String name = file.getName();
			if (!name.endsWith(".apk"))
				file.delete();
		}

	}
}
