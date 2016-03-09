/*
 * 文件名：Store.java
 * 描述：存储器接口
 * 版本：v1.0.0
 * 日期：2014-3-14
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store;

import java.io.File;
import java.util.List;


/**
 *<p>存储器接口</p><br/>
 *<p>存储器接口</p>
 * @see {@link com.muzhiwan.libs.core.store.impl.JsonStore}
 * @see {@link com.muzhiwan.libs.core.store.impl.LoggerStore}
 * @see {@link com.muzhiwan.libs.core.store.impl.ThrowableStore}
 * 
 * @since 1.0.0
 * @author xiaodong
 */
public interface Store<T> {

	/**
	 * 存储数据
	 *<p>存储数据</p><br/>
	 *<p>存储数据 重复调用相同的key 会覆盖</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param obj 数据对象
	 * @param key 文件名称
	 */
	public void save(T obj, String key);

	/**
	 * 
	 *<p>加载数据</p><br/>
	 *<p>加载数据</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param key 文件名称
	 * @return
	 */
	public T load(String key);
	/**
	 * 
	 *<p>删除数据</p><br/>
	 *<p>删除数据</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param key 文件名称
	 * @return
	 */
	public void delete(String key);
	
	/**
	 * 更新数据
	 *<p>更新数据</p><br/>
	 *<p>更新数据 重复调用相同的key 会追加写入 不会覆盖</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param obj 数据对象
	 * @param key 文件名称
	 */
	public void update(T obj, String key);
	
	/**
	 *<p>加载所有数据</p><br/>
	 *<p>加载所有数据</p>
	 * @since 1.0.0
	 * @author cxd
	 * @return
	 */
	public List<T> loadAll();
	
	
	public File getStoreFile(String key);
	
	/**
	 *<p>删除全部</p><br/>
	 *<p>删除全部</p>
	 * @since 1.0.0
	 * @author cxd
	 */
	public void deleteAll();
	
}
