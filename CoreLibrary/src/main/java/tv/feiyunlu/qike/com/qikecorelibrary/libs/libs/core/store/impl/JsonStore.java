/*
 * 文件名：JsonStore.java
 * 描述：存储实现{@link com.muzhiwan.libs.core.store.JsonSerialize}的对象
 * 版本：v1.0.0
 * 日期：2014-3-14
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security.StoreSecurity;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.Store;

/**
 *<p>实现{@link com.muzhiwan.libs.core.store.Store}</p><br/>
 *<p>将对象 进行存储和获取</p>
 * @since 1.0.0
 * @author xiaodong
 */
public class JsonStore implements Store<Object> {

	private String path;
	private StoreSecurity security;

	public <V extends StoreSecurity> JsonStore(String path, V security) {

		path += File.separator;

		this.path = path;
		File file = new File(this.path);

		if (!file.exists()) {
			file.mkdirs();
		}

		this.security = security;
	}

	@Override
	public void save(Object obj, String key) {

		Gson gson = new Gson();
		String json = gson.toJson(obj);
		byte[] bytes;
		try {

			File file = new File(path + key);
			File dir = file.getParentFile();
			if (dir != null && !dir.exists()) {
				dir.mkdirs();
			}

			bytes = json.getBytes("UTF-8");
			if (security != null) {
				bytes = security.encrypt(bytes);
			}

			FileUtils.write(path + key, bytes);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * 此方法不可用 请用 {@link #load(Class, String)};
	 * @return allways return null;
	 */
	@Override
	public Object load(String key) {

		return null;
	}

	/**
	 * 
	 *<p>加载数据</p><br/>
	 *<p>加载数据</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param clazz 对象的类型 {@link Class}
	 * @param key 文件名称
	 * @return
	 */
	public <T> T load(Class<T> clazz, String key) {

		byte[] buffer = FileUtils.read(path + key);

		try {
			if (security != null) {
				buffer = security.decrypt(buffer);
			}

			String json = new String(buffer, "UTF-8");

			Gson gson = new Gson();

			return gson.fromJson(json, clazz);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void delete(String key) {
		FileUtils.delete(path + key);
	}

	/**
	 * 此方法不可用 请用 {@link #loadAll(Class)};
	 * @return allways return null;
	 */
	@Override
	public List<Object> loadAll() {
		return null;
	}

	/**
	 * 
	 *<p>加载所有数据</p><br/>
	 *<p>加载所有数据</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param clazz 对象的类型 {@link Class}
	 * @return
	 */
	public <T> List<T> loadAll(Class<T> clazz) {

		List<T> list = new ArrayList<T>();

		File dire = new File(path);
		File[] files = dire.listFiles();
		if (files != null) {
			for (File file : files) {
				if (!file.isDirectory()) {
					T t = load(clazz, file.getName());
					if (t != null) {
						list.add(t);
					}
				}

			}
		}

		return list;
	}

	@Override
	public void update(Object obj, String key) {
		save(obj, key);

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
