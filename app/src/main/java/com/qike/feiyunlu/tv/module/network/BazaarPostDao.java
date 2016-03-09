package com.qike.feiyunlu.tv.module.network;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.BasicPostDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ParamsContants;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;

/**
 * 
 *<p>post请求封装类</p><br/>
 * @since 1.0.0
 * @author xianhao
 * @param <T>
 */
public class BazaarPostDao<T> extends BasicPostDao {
	public static final int ARRAY_DATA = 0;
	public static final int ARRAY_DATA_CHUNK = 1;
	public static final int ARRAY_DATA_LOOP = 2;
	private Class<T> clazz = null;
	private String mStrResult;
	private int mDataType;
	private DLResultData<T> mData;
	private DLResultData<String> mErrorData;
	private String mUrl;
	private T mLoopData;
//	private Map<String, Object> mParams = new HashMap<String, Object>();
//	private CommonalityParams mCommonality = new CommonalityParams();

	public BazaarPostDao(String url, Class<T> clazz, int type) {
		super(url);
		mDataType = type;
		mData = new DLResultData<T>();
		mUrl = url;
		this.clazz = clazz;
		if (mCacheLoader == null) {
			mCacheLoader = buildCacheLoader();
		}
	}

	public void JSONpackaging(String str, Object obj) {
		String json = JSON.toJSONString(obj);
		Log.i(BazaarPostDao.class.getName(), json);
		
		putParams(str, json);
		asyncDoAPI();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action, HashMap headers) {
		if (isServerDataError(headers)) {
			return;
		}
		CacheEntry entry = action.getCacheEntry();

		try {

			content = decodeResponse(entry.data);
			// content = decodeResponse(action.getBytes());
		} catch (Throwable e) {
			e.printStackTrace();
			mResult.setCode(ParamsContants.ERROR_DECODE);
		}

		if (!TextUtils.isEmpty(content)) {
			JSONObject object = JSONObject.parseObject(content);
			mData = JSONObject.parseObject(content, DLResultData.class);
			
			if (mData.getCode() == 200) {
				try {

					if (clazz != null && clazz.equals(String.class)) {
						mStrResult = (String) content;
						return;
					}


					switch (mDataType) {
					case ARRAY_DATA:
						mData.setDataList(JSONArray.parseArray(object.get("data").toString(), clazz));

						break;

					case ARRAY_DATA_CHUNK:
						if(object.get("data") != null) {
							mData.setData(JSONObject.parseObject(object.get("data").toString(), clazz));
						}

						break;
					case ARRAY_DATA_LOOP:
						Gson gson = new Gson();
						mLoopData = gson.fromJson(content, clazz);
						break;
					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
					mResult.setCode(ParamsContants.ERROR_PARSE);
				}
			}
			
			else{
				mErrorData = JSONObject.parseObject(content, DLResultData.class);
			}
		} else {
			mResult.setCode(ParamsContants.ERROR_PARSE);
		}
	}

	/**
	 * 
	 *<p>post单独请求</p><br/>
	 * @since 1.0.0
	 * @author xianhao
	 * @return String json
	 */
	public static String post(String url, Map<String, String> param) {
		StringBuffer buffer = null;
		PrintWriter out = null;
		BufferedReader reader = null;
		try {
			URL postUrl = new URL(url);

			URLConnection connection = postUrl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			out = new PrintWriter(connection.getOutputStream());
			out.print(param);
			out.flush();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inResut;
			buffer = new StringBuffer();
			while ((inResut = reader.readLine()) != null) {
				buffer.append(inResut);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			out.close();
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

	public List<T> getList() {

		return mData.getDataList();

	}

	public T getLoopData() {
		return mLoopData;
	}

	public DLResultData<String> getErrorData(){
		return mErrorData;
	}
	
	public String getStringResult() {
		return mStrResult;
	}

	public T getData() {
		return mData.getData();
	}

	public int getStatus() {
		return mData.getCode();
	}

//	public Page getPage() {
//		return mData.getPage();
//	}

	@Override
	protected CacheLoad buildCacheLoader() {
		Configuration config = Configuration.getConfiguration();
		CacheLoad cacheLoad = new CacheLoader(config.getDiskCache());
		return cacheLoad;
	}
}
