package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseOpenHelper;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;


/**
 * <p>数据库操作实现类</p><br/>
 * <p>提供增删改查与数据库初始化等方法</p>
 * @since 1.0.0
 * @author xujiaoyong
 */
public class DatabaseProviderImpl implements DatabaseProvider {

	//数据库操作对象
	private DatabaseOpenHelper helper;

	public DatabaseProviderImpl(String databasePath) {
		init(databasePath);
	}

	/**
	 * 
	 *<p>初始化数据库</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param databasePath 数据库文件路径
	 */
	@Override
	public void init(String databasePath) {
		helper = new DatabaseOpenHelper(databasePath);
	}

	/**
	 * 
	 *<p>TODO 保存实体对象到数据库</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 保存到数据库的Java对象
	 */
	@Override
	public <T, ID> void save(T data) {
		try {
			if (data != null) {
				Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(data.getClass());
				dao.createOrUpdate(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *<p>关闭数据库连接，清理资源</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 */
	@Override
	public void close() {
		try {
			helper.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *<p>从数据库中删除指定数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 要从数据库中删除的对象
	 */
	@Override
	public <T, ID> void delete(T data) {
		try {
			if (data != null) {
				Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(data.getClass());
				dao.delete(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *<p>TODO更新具体的数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param data 需要进行更新的Java对象
	 */
	@Override
	public <T, ID> void update(T data) {
		try {
			if (data != null) {
				Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(data.getClass());
				dao.update(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *<p>查询对应类型在数据库中的所有数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 需要进行查询的数据对象类型
	 * @return 返回对应类型的所有数据对象，无则返回null
	 */
	@Override
	public <T, ID> List<T> queryAll(Class<T> clazz) {
		try {
			Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(clazz);
			List<T> items = dao.queryForAll();

			return items;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 *<p>根据主键查询对应数据对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 返回对象类型
	 * @param key 数据表主键
	 * @return 返回查询到的数据对象，无则返回null
	 */
	@Override
	public <T, ID> T query(Class<T> clazz, ID key) {
		try {
			Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(clazz);
			return dao.queryForId(key);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T, ID> List<T> queryForEq(Class<T> clazz, String arg0, Object arg1) {
		try {
			Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(clazz);

			return dao.queryForEq(arg0, arg1);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	};

	public <T, ID> QueryBuilder<T, ID> queryBuilder(Class<T> clazz) {
		try {
			Dao<T, ID> dao = (Dao<T, ID>) helper.getDao(clazz);
			return dao.queryBuilder();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
