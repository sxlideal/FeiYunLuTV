/*
 * 文件名：FileUtils.java
 * 描述：操作文件的类
 * 版本：v1.0.0
 * 日期：2014-3-12
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *<p>文件操作</p><br/>
 *<p>文件读写操作</p>
 * @since 1.0.0
 * @author xiaodong
 */
public class FileUtils {

	/**
	 * 
	 *<p>将byte数组写入指定路径中</p><br/>
	 *<p>将byte数组写入指定路径中</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param path 被写入的路径
	 * @param bytes 格式为 UTF-8 的byte数组
	 */
	public static void write(String path, byte[] bytes) {
		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(path);

			fos.write(bytes);

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {

			if (fos != null) {
				try {
					fos.flush();
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
	}

	/**
	 * 
	 *<p>读取指定路径的数据</p><br/>
	 *<p>读取指定路径的数据</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param path 被读的路径
	 * @return 读取出来的byte数组
	 */
	public static byte[] read(String path) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int length = 0;

			while ((length = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}

			return baos.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

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

	/**
	 * 
	 *<p>存储对象</p><br/>
	 *<p>将一个实现了{@link Serializable}的对象 序列化存储到指定路径中</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param object 被存储的对象
	 * @param path 对象被存储的路径
	 */
	public static void writeObject(Object object, String path) {

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(object);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

			if (oos != null) {
				try {
					oos.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 
	 *<p>获取对象</p><br/>
	 *<p>根据指定路径 获取到路径存储了实现{@link Serializable}的对象</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param path 对象被存储的路径
	 * @return T 需要被获取的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(String path) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(path));
			return (T) ois.readObject();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	/**
	 *<p>检查文件是否存在</p><br/>
	 *<p>检查文件是否存在，若不存在则会自动创建目录</p>
	 * @since 1.0.0
	 * @author xiaodong
	 * @param path
	 */
	public static void checkAndMakeDirs(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
	}

	/**
	 *<p>删除指定文件</p><br/>
	 *<p>删除指定文件</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param path
	 */
	public static void delete(String path) {

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

	}
}
