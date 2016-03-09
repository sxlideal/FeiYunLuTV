/*
 * 文件名：ExceptionStore.java
 * 描述：对实现{@link java.lang.Throwable} 的对象进行存储和获取
 * 版本：v1.0.0
 * 日期：2014-3-14
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl;

import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security.StoreSecurity;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.Store;


/**
 *<p>实现{@link com.muzhiwan.libs.core.store.Store}</p><br/>
 *<p>对实现{@link Throwable} 的对象进行存储和获取</p>
 * @since 1.0.0
 * @author xiaodong
 */
public class ThrowableStore implements Store<Throwable> {

	private String path;

	public <V extends StoreSecurity> ThrowableStore(String path, V security) {

		path += File.separator;

		this.path = path;
	}

	@Override
	public void save(Throwable obj, String key) {

		PrintWriter writer = null;
		File file = new File(path + key);
		try {

			File parentFile = file.getParentFile();
			if (parentFile != null) {
				parentFile.mkdirs();
			}
			writer = new PrintWriter(path + key, "UTF-8");

			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sm.format(new Date());

			writer.append("时间：" + time);
			writer.append(System.getProperty("line.separator"));

			obj.printStackTrace(writer);
			writer.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public Throwable load(String key) {
		byte[] buffer = FileUtils.read(path + key);
		try {
			String message = new String(buffer, "UTF-8");
			Throwable t = new Throwable(message);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void delete(String key) {

		FileUtils.delete(path + key);

	}

	@Override
	public List<Throwable> loadAll() {

		List<Throwable> list = new ArrayList<Throwable>();

		File dire = new File(path);
		File[] files = dire.listFiles();
		for (File file : files) {
			Throwable t = load(file.getName());
			list.add(t);
		}
		return list;
	}

	@Override
	public void update(Throwable obj, String key) {

		PrintWriter writer = null;
		try {

			writer = new PrintWriter(new FileWriter(path + key, true));
			writer.append("----------------------------------------------------------------------");
			writer.append(System.getProperty("line.separator"));

			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sm.format(new Date());

			writer.append("时间：" + time);
			writer.append(System.getProperty("line.separator"));

			obj.printStackTrace(writer);
			writer.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public File getStoreFile(String key) {
		String filePath = path + key;
		if (!TextUtils.isEmpty(filePath)) {
			return new File(filePath);
		}
		return null;

	}

	@Override
	public void deleteAll() {

		File dire = new File(path);
		FileUtils.deleteFile(dire);

	}
}
