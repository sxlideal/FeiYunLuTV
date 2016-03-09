package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.ISortProcessor;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.AbstractGetDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ParamsContants;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;


public abstract class SupportV4GetDao<T extends ResponseResult<V>, V> extends AbstractGetDao<ResponseResult<V>> {

	private List<V> results;
	private int pageSize = 32;
	private int pageNum = 0;
	private AtomicBoolean canNextPage;
	private ResponseResult<V> result;
	private ISortProcessor<List<V>, V> processor;
	private int totalCount;

	public SupportV4GetDao(String url) {
		super(url);
		canNextPage = new AtomicBoolean(true);

				if (mCacheLoader == null) {
					mCacheLoader = buildCacheLoader();
				}
	}

	@Override
	public void asyncDoAPI() {

		if (!canNextPage.get()) {
			return;
		}
		canNextPage.set(false);
		setPage();

		super.asyncDoAPI();
	}

	public void refresh() {

		pageNum = 0;
		asyncDoAPI();
	}

	public void setSortProcessor(ISortProcessor<List<V>, V> processor) {
		this.processor = processor;
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
				Gson gson = new Gson();
				result = gson.fromJson(content, getType());
				results = result.getRows();

				totalCount = result.getTotal();

				if (processor != null) {//自定义排序
					results = processor.onProcess(results);
				}

			} catch (Exception e) {
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

	private void setPage() {

		putParams(ParamsContants.PAGENUM, String.valueOf(pageNum));
		putParams(ParamsContants.PAGESIZE, String.valueOf(pageSize));
	}

	public void setPageSize(int num) {
		this.pageSize = num;
	}

	@Override
	protected void _onTaskCancelled() {
		super._onTaskCancelled();
		canNextPage.set(true);
	}

	@Override
	protected void _onTaskFailed(Task.TaskFailed failed) {
		super._onTaskFailed(failed);
		canNextPage.set(true);
	}

	@Override
	protected void _onTaskEnd() {

		canNextPage.set(true);

		if (loadListener != null) {
			if (results != null && results.size() == 0) {
				loadListener.onComplete(ResultType.EMPTY);//返回空
				return;
			}
		}
		pageNum++;//真正成功了才会翻页属性+1
		super._onTaskEnd();
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

	public int getTotalCount() {

		return totalCount;
	}

	public void addComment(V v) {
		ArrayList<V> comments = new ArrayList<V>(results.size() + 1);

		comments.add(v);

		comments.addAll(results);
		results = comments;
		totalCount++;
	}

	public boolean remove(V v) {
		boolean removed = results.remove(v);
		if (removed) {
			totalCount--;

		}
		return removed;

	}

	@Override
	protected CacheLoad buildCacheLoader() {
		return null;
	}
	
	
	
}
