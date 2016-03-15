package com.qike.feiyunlu.tv.module.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.AbstractGetDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ParamsContants;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;

/**
 *<p>dao封装类</p><br/>
 *<p>用于处理市场客户端特有数据格式</p>
 * @since 5.0.0
 * @author suenxianhao
 * @param <T>
 */
public class BazaarGetDao<T> extends AbstractGetDao {

	private Class<T> clazz = null;
	private String mStrResult;
	private Map<String, Object> mParams = new HashMap<String, Object>();
	private CommonalityParams mCommonality = new CommonalityParams();
	private String mUrl;
	private DLResultData<T> mData;
	
	private DLResultData<String> mErrorData;
	
	private int mDataType;
	public static final int ARRAY_DATA = 0;
	public static final int ARRAY_DATA_CHUNK = 1;
	public static final int ARRAY_DATA_LOOP = 2;
	private boolean isNext = false;
	private T mLoopData;

	private Gson gson = new Gson();

	public BazaarGetDao(String url, Class<T> clazz, int type) {
		super(url);
		mDataType = type;
		mData = new DLResultData<T>();
		mUrl = url;
		this.clazz = clazz;
		//		mContext = context;
		if (mCacheLoader == null) {
			mCacheLoader = buildCacheLoader();
		}

	}

	/**
	 * 这个构造函数只返回String
	 * @param url
	 */
	public BazaarGetDao(String url) {
		super(url);
		mUrl = url;

	}

	@Override
	protected void _onTaskCancelled() {
		super._onTaskCancelled();
	}

	@Override
	protected void _onTaskFailed(Task.TaskFailed failed) {
		super._onTaskFailed(failed);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action, HashMap<String, String> headers) {

		if (isServerDataError(headers)) {
			return;
		}
		CacheEntry entry = action.getCacheEntry();

		try {

			content = decodeResponse(entry.data);
			//			content = decodeResponse(action.getBytes());
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

					isNext = true;

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
			} else {
				
				mErrorData = JSONObject.parseObject(content, DLResultData.class);
			}
		} else {
			mResult.setCode(ParamsContants.ERROR_PARSE);
		}

	}

	/**
	 *<p>刷新及第一次请求列表</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	public void startTask() {
		mParams.put("page", 1);
		asyncDoAPI();
	}

	/**
	 * 请求单一数据
	 */
	@Override
	public void asyncDoAPI() {
		mParams = mCommonality.initGeneralParams(mUrl, mParams);
		putAllParams(mParams);
		super.asyncDoAPI();
	}

	/**
	 *<p>请求下一页列表数据</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	public void nextTask() {
		if (isNext) {
			int pagenum = (Integer) mParams.get("page");
			mParams.put("page", pagenum + 1);
			asyncDoAPI();
			isNext = false;
		}
	}

	public void putParams(String key, Object value) {
		mParams.put(key, value);
	}

	@Override
	public void putParams(String key, String value) {
		// TODO Auto-generated method stub
		mParams.put(key, value);
		super.putParams(key, value);
	}

	@Override
	public void putAllParams(Map paramsMap) {
		mParams.putAll(paramsMap);
		super.putAllParams(mParams);
	}

	public List<T> getList() {

		return mData.getDataList();

	}

	public T getLoopData() {
		return mLoopData;
	}

	public String getStringResult() {
		return mStrResult;
	}
	


	public T getData() {
		return mData.getData();
	}

	public DLResultData<T> getmData(){
		return mData;
	}
	
	public DLResultData<String> getErrorData(){
		return mErrorData;
	}

	public int getStatus() {
		return mData.getCode();
	}

	//
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
