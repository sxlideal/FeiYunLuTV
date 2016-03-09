package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases;

import java.util.List;

/**
 * <p>数据库操作类</p><br/>
 * <p>提供增删改查与数据库初始化等方法</p>
 * @since 1.0.0
 * @author xujiaoyong
 */
public interface DatabaseProvider {
	/**
	 * 
	 *<p>TODO 保存实体对象到数据库</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 保存到数据库的Java对象
	 */
	public <T, ID> void save(T data);

	/**
	 * 
	 *<p>从数据库中删除指定数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 要从数据库中删除的对象
	 */
	public <T, ID> void delete(T data);

	/**
	 * 
	 *<p>TODO更新具体的数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 需要进行更新的Java对象
	 */
	public <T, ID> void update(T data);

	/**
	 * 
	 *<p>查询对应类型在数据库中的所有数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 需要进行查询的数据对象类型
	 * @return 返回对应类型的所有数据对象，无则返回null
	 */
	public <T, ID> List<T> queryAll(Class<T> clazz);

	/**
	 * 
	 *<p>关闭数据库连接，清理资源</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 */
	public void close();

	/**
	 * 
	 *<p>根据主键查询对应数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 返回对象类型
	 * @param key 数据表主键
	 * @return 返回查询到的数据对象，无则返回null
	 */
	public <T, ID> T query(Class<T> clazz, ID key);

	/**
	 * 
	 *<p>初始化数据库</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param databasePath 数据库文件路径
	 */
	public void init(String databasePath);

	public <T, ID> List<T> queryForEq(Class<T> clazz, String arg0, Object arg1);
}
