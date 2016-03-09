package com.qike.feiyunlu.tv.module.network;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>JSON解析</p><br/>
 * @since 5.0.0
 * @author suenxianhao
 */
public class JsonAPI {

/**
*<p>JSON转换成模型对象</p>
 * @since 5.0.0
 * @author suenxianhao
 * @param target
 * @param json
 * @return
 * @throws Exception
 */
	public static <T> T jsonToObject(Class<T> target, JSONObject json) throws Exception {
		Gson gson = new Gson();
		return gson.fromJson(json.toString(), target);
	}

/**
 * 
 * JSON转换成模型对象
 * @since 5.0.0
 * @author suenxianhao
 * @param clazz
 * @param list2
 * @return
 * @throws Exception
 */
	public static <T> List<T> jsonToList(Class<T> clazz, JSONArray list2) throws Exception {

		List<T> list = new ArrayList<T>();

		for (int i = 0; i < list2.length(); i++) {
			JSONObject json = (JSONObject) list2.getJSONObject(i);
			list.add(jsonToObject(clazz, json));
		}
		return list;
	}
}
