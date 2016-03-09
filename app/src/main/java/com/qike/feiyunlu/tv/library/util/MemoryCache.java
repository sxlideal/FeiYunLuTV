package com.qike.feiyunlu.tv.library.util;

import java.util.HashMap;

/**
 * 
 *<p>静态HashMap存储</p><br/>
 *
 * @since 2.6
 * @author sunxh
 *
 */
public class MemoryCache{
	public static String SRC_QIKE = "qike";
	public static String SRC_INTERNATIONAL = "QIKE_international";
	public static  String SOURCE = SRC_QIKE;
	public static final String PROPERTIES_LOCALE_KEY = "change_language"; 
	private static HashMap<String, Object> mCache = new HashMap<String, Object>();
	
	public static Object get(String key) {
		return mCache.get(key);
	}

	public static void put(String key, Object value) {
		mCache.put(key, value);
	}

	public static Object remove(String key) {
		return mCache.remove(key);
	}

}
