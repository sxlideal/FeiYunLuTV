package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;


/**
 * 
 *<p>对OrmLite进行简单封装以及扩展</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 1.0.0
 * @author xujiaoyong
 */
public class DatabaseOpenHelper {
	private SQLiteDatabase db;
	private AndroidConnectionSource conn;

	
	/**
	 * 
	 * @param dbPath 数据库本地路径
	 */
	public <T> DatabaseOpenHelper(String dbPath) {
		db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
		conn = new AndroidConnectionSource(db);
		
	}

	/**
	 * 
	 *<p>释放数据库资源</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @throws Exception 关闭数据库失败抛出异常
	 */
	public void release() throws Exception {
		db.close();
		conn.close();
	}

	
	/**
	 * 
	 *<p>更新数据表结构</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 需要更新表结构的数据对象类型
	 */
	private <T> void updateDatabase(Class<T> clazz) {
		Cursor cursor = null;
		try {
			DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil
					.fromClass(conn, clazz);
			TableUtils.createTableIfNotExists(conn, clazz);
			String tableName = tableConfig.getTableName();
			List<DatabaseFieldConfig> configs = tableConfig.getFieldConfigs();
			cursor = db.rawQuery("select * from " + tableName + " limit 1",
					null);
			if ( configs != null && configs.size() > 0) {
				String[] columnNames = cursor.getColumnNames();
				if (columnNames != null && columnNames.length > 0) {
					for (DatabaseFieldConfig config : configs) {
						if (!isFieldExists(config, columnNames)) {
							try {
								String alterSql = createAlterSqlStatement(config, tableName,clazz);
								db.execSQL(alterSql); 
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
					

				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * 
	 *<p>判断字段是否已经存在数据表中</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param config 数据字段配置
	 * @param columnNames 列名
	 * @return 数据列已经存在返回true，否则返回false
	 */
	private boolean isFieldExists(DatabaseFieldConfig config,
			String[] columnNames) {
		for (String column : columnNames) {
			if (config.getFieldName().equals(column)) {
				return true;
			}
		}
		return false;
	}

	
	
	/**
	 * 
	 *<p>创建修改数据表结构语句</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param config 数据字段配置
	 * @param tableName 数据表名
	 * @param clazz 数据对象类型
	 * @return 返回修改数据表结构SQL语句
	 */
	private String createAlterSqlStatement(
			 DatabaseFieldConfig config, String tableName,Class clazz) {
		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		sb.append(tableName).append(" add column ").append(config.getFieldName())
		.append(" ").append(getSqlType(config,clazz));
		return sb.toString();
	}

	
	/**
	 * 
	 *<p>获取字段在数据库中的具体类型</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param config 数据字段配置
	 * @param clazz 数据对象类型
	 * @return 返回对应数据字段具体类型
	 */
	private String getSqlType(DatabaseFieldConfig config,Class clazz) {
		DataPersister persister = config.getDataPersister();
		if(persister == null){
			return getPrimarySqlType(config, clazz);
		}
		SqlType sqlType = persister.getSqlType();
		switch (sqlType) {

		case STRING:
			return "VARCHAR";

		case LONG_STRING:
			return "TEXT";

		case BOOLEAN:
			return "BOOLEAN";

		case DATE:
			return "TIMESTAMP";

		case CHAR:
			return "CHAR";

		case BYTE:
			return "TINYINT";

		case BYTE_ARRAY:
			return "BLOB";

		case SHORT:
			return "SMALLINT";

		case INTEGER:
			return "INTEGER";

		case LONG:
			return "BIGINT";

		case FLOAT:
			return "FLOAT";

		case DOUBLE:
			return "DOUBLE PRECISION";

		case SERIALIZABLE:
			return "BLOB";

		case BIG_DECIMAL:
			return "NUMERIC";

		case UNKNOWN:
		default:
			return "TEXT";
		}
	}
	
	
	/**
	 * 
	 *<p>获取Java基础类型在数据库中的具体类型</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param config 数据字段配置
	 * @param clazz 数据对象类型
	 * @return 返回数据库具体类型
	 */
	private String getPrimarySqlType(DatabaseFieldConfig config,Class clazz){
		try {
			String fieldName = config.getFieldName();
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Class fieldClass = field.getType();
			if(fieldClass == int.class || fieldClass == Integer.class){
				return "INTEGER";
			}
			if(fieldClass == long.class || fieldClass == long.class){
				return "LONG";
			}
			if(fieldClass == short.class || fieldClass == Short.class){
				return "SMALLINT";
			}
			if(fieldClass == boolean.class || fieldClass == Boolean.class){
				return "BOOLEAN";
			}
			if(fieldClass == String.class){
				return "VARCHAR";
			}
			if(fieldClass == float.class){
				return "FLOAT";
			}
			if(fieldClass == double.class || fieldClass == Double.class){
				return "DOUBLE PRECISION";
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "TEXT";
	}

	/**
	 * 
	 *<p>获取对应数据对象类型的数据库操作对象</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param clazz 数据对象类型
	 * @return 数据库操作对象
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public <D extends Dao<T, ID>, T,ID> D getDao(Class<T> clazz)
			throws SQLException {
		Dao<T, ID> dao = DaoManager.lookupDao(conn, clazz);
		if (dao == null) {
			updateDatabase(clazz);
			TableUtils.createTableIfNotExists(conn, clazz);
			DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil
					.fromClass(conn, clazz);
			if (tableConfig == null) {
				dao = (Dao<T, ID>) DaoManager.createDao(conn, clazz);
			} else {
				dao = (Dao<T, ID>) DaoManager.createDao(conn, tableConfig);
			}
		}

		D castDao = (D) dao;
		return castDao;
	}

}
