/*
 * 文件名：LoggerStore.java
 * 描述：存储和获取 String
 * 版本：v1.0.0
 * 日期：2014-3-14
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl;

import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security.StoreSecurity;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.Store;


/**
 *<p>实现{@link com.muzhiwan.libs.core.store.Store}</p><br/>
 *<p>存储和获取 String <br/>
 * @since 1.0.0
 * @author xiaodong
 */
public class LoggerStore implements Store<String> {

	private String path;
	private StoreSecurity security;

	public <V extends StoreSecurity> LoggerStore(String path, V security) {

		path += File.separator;

		this.path = path;
		this.security = security;
	}

	@Override
	public void save(String obj, String key) {

		PrintWriter writer = null;
		File file = new File(path + key);
		try {
			byte[] buffer = obj.getBytes("UTF-8");
			if (security != null) {
				buffer = security.encrypt(buffer);
			}

			File parentFile = file.getParentFile();
			if (parentFile != null) {
				parentFile.mkdirs();
			}
			writer = new PrintWriter(path + key, "UTF-8");
			writer.append(obj);
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
	public String load(String key) {
		byte[] buffer = FileUtils.read(path + key);
		try {
			if (security != null) {
				buffer = security.decrypt(buffer);
			}
			String message = new String(buffer, "UTF-8");
			return message;
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
	public List<String> loadAll() {

		List<String> list = new ArrayList<String>();

		File dire = new File(path);
		File[] files = dire.listFiles();
		for (File file : files) {
			String t = load(file.getName());
			list.add(t);
		}
		return list;
	}

	@Override
	public void update(String obj, String key) {

		PrintWriter writer = null;
		try {
			byte[] buffer = obj.getBytes("UTF-8");
			if (security != null) {
				buffer = security.encrypt(buffer);
			}

			writer = new PrintWriter(new FileWriter(path + key, true));
			writer.append(System.getProperty("line.separator"));
			writer.append("----------------------------------------------------------------------");
			writer.append(System.getProperty("line.separator"));
			writer.append(obj);
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
