/*
 * 文件名：SupportV4PostDao.java
 * 描述：TODO(一句话描述该文件做什么)
 * 版本：v1.0.0
 * 日期：2014-5-15
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.AbstractPostDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ParamsContants;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


public abstract class SupportV4PostDao<T extends ResponseResult<V>, V> extends AbstractPostDao<ResponseResult<V>> {

	private ResponseResult<V> result;
	private List<V> results;

	public SupportV4PostDao(String url) {
		super(url);
		
		if (mCacheLoader == null) {
		    mCacheLoader = buildCacheLoader();
		}
	}

	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action, HashMap<String, String> headers) {

		if (isServerDataError(headers)) {
			return;
		}

		try {
			content = decodeResponse(action.getBytes());
		} catch (Throwable e) {
			e.printStackTrace();
			mResult.setCode(ParamsContants.ERROR_DECODE);
		}

		if (!TextUtils.isEmpty(content)) {

			try {
				Type clazz = getType();

				if (clazz != null) {
					result = new Gson().fromJson(content, clazz);
				}

			} catch (Throwable e) {
				e.printStackTrace();
				mResult.setCode(ParamsContants.ERROR_PARSE);
			}
		} else {
			mResult.setCode(ParamsContants.ERROR_PARSE);
		}

	}

	private Type getType() {

		ParameterizedType mType = (ParameterizedType) this.getClass().getGenericSuperclass();

		return mType.getActualTypeArguments()[0];

	}

	
	
	@Override
	protected CacheLoad buildCacheLoader() {
		return null;
	}

	public V getItem(int index) {
		return results.get(index);
	}

	public int getItemSize() {
		if (results != null && results.size() > 0) {
			return results.size();
		} else {
			return 0;
		}
	}

	public int getCode() {
		return result.getCode();
	}
	
	public void putFile(String path){
		
		putParams("files", path);
	}
}
