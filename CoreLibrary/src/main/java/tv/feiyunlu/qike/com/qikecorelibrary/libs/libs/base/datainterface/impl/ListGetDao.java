package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.ISortProcessor;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


/**
 * 
 *<p>对应有分页请求的数据接口</p><br/>
 *<p>可以把该dao传入实现了 onScrollListener的 adapter, 在判定需要加载下一页时调用asyncDoAPI方法</p>
 * @since 1.0.0
 * @author bigpie
 * @param <T>
 */
public abstract class ListGetDao<T extends List<V>, V> extends AbstractGetDao<T> {

	private int pageSize = 32;
	private int pageNum = 0;
	private AtomicBoolean canNextPage;
	private T results;
	private ISortProcessor<T, V> processor;

	public ListGetDao(String url) {
		super(url);
		canNextPage = new AtomicBoolean();
		
		if (mCacheLoader == null) {
		    mCacheLoader = buildCacheLoader();
		}

	}

	public void refresh() {

		pageNum = 0;
		asyncDoAPI();

	}

	@Override
	public void asyncDoAPI() {

		if (canNextPage.get()) {
			return;
		}
		canNextPage.set(true);

		setPage();

		super.asyncDoAPI();

	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * 
	 *<p>分页的参数组成部分</p><br/>
	 *<p>上一页加载完后 标志页码数的pageNum自动+1</p>
	 * @since 1.0.0
	 * @author bigpie
	 */
	private void setPage() {

		putParams(ParamsContants.PAGENUM, String.valueOf(pageNum));
		putParams(ParamsContants.PAGESIZE, String.valueOf(pageSize));
	}

	public void setSortProcessor(ISortProcessor<T, V> processor) {
		this.processor = processor;
	}

	@Override
	protected void _onTaskEnd() {

		if (loadListener != null) {
			if (results != null && results.size() == 0) {
				loadListener.onComplete(ResultType.EMPTY);
				return;
			}
		}
		pageNum++;//真正成功了才会翻页属性+1
		super._onTaskEnd();
	}


	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action, HashMap<String, String> headers) {

		if (isServerDataError(headers)) {
			return;
		}

		byte[] bytes = action.getCacheEntry().data;

		try {
			content = decodeResponse(bytes);
		} catch (Throwable e) {
			e.printStackTrace();
			mResult.setCode(ParamsContants.ERROR_DECODE);
		}

		if (!TextUtils.isEmpty(content)) {
			try {
				//		results = new Gson().fromJson(content, new TypeToken<List<T>>() {
				//		}.getType());

				Gson gson = new Gson();
				results = gson.fromJson(content, getType());

				if (processor != null) {//自定义排序
					results = processor.onProcess(results);
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

		ParameterizedType types = (ParameterizedType) this.getClass().getGenericSuperclass();

		return types.getActualTypeArguments()[0];

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
	
	@Override
	    protected CacheLoader buildCacheLoader() {

		IDiskCache diskCache = Configuration.getConfiguration().getDiskCache();
		CacheLoader cacheLoader = new CacheLoader(diskCache);
		return cacheLoader;
	    }

}
