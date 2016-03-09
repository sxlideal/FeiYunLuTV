package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface;

import java.util.List;

/**
 * 
 *<p>列表页请求结果的异步排序处理器</p><br/>
 *<p>排序的</p>
 * @since 1.0.0
 * @author bigpie
 * @param <T>
 */
public interface ISortProcessor<T extends List<V>, V> {

	public T onProcess(T datas);
}
