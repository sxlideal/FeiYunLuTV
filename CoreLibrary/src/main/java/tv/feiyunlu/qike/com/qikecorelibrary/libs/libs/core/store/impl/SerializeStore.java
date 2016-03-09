/*
 * 文件名：SerializeStore.java
 * 描述：对对象进行存储和获取
 * 版本：v1.0.0
 * 日期：2014-3-14
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security.StoreSecurity;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.Store;


/**
 *<p>对对象进行存储和获取</p><br/>
 *<p>对实现{@link Serializable}的对象 进行存储和获取</p>
 * @since 1.0.0
 * @author xiaodong
 */
public class SerializeStore implements Store<Serializable> {
	private String path;
	private StoreSecurity security;

	public <V extends StoreSecurity> SerializeStore(String path, V security) {

		path += File.separator;

		this.path = path;
		File file = new File(this.path);

		if (!file.exists()) {
			file.mkdirs();
		}

		this.security = security;
	}

	@Override
	public void save(Serializable obj, String key) {
		FileOutputStream os = null;
		try {
			ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
			ObjectOutputStream objectOs = new ObjectOutputStream(byteOs);
			objectOs.writeObject(obj);
			objectOs.close();

			byte[] buff = byteOs.toByteArray();
			if (security != null) {

				buff = security.encrypt(buff);
			}

			File file = new File(path + key);
			File dir = file.getParentFile();
			if (dir != null && !dir.exists()) {
				dir.mkdirs();
			}
			os = new FileOutputStream(file);
			os.write(buff);

			FileUtils.write(path + key, buff);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Override
	public Serializable load(String key) {
		byte[] buffer = FileUtils.read(path + key);
		ObjectInputStream ois = null;
		try {
			if (security != null) {

				buffer = security.decrypt(buffer);
			}

			ByteArrayInputStream bis = new ByteArrayInputStream(buffer);

			ois = new ObjectInputStream(bis);
			return (Serializable) ois.readObject();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public void delete(String key) {
		FileUtils.delete(path + key);
	}

	@Override
	public void update(Serializable obj, String key) {
		save(obj, key);
	}

	@Override
	public List<Serializable> loadAll() {

		List<Serializable> list = new ArrayList<Serializable>();

		File dire = new File(path);
		File[] files = dire.listFiles();
		for (File file : files) {
			Serializable t = load(file.getName());
			list.add(t);
		}
		return list;
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
