package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.IMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.DirectoryUtils;


/**
 * 
 *<p>配置控制类</p><br/>
 *<p>获取：getInstance()</p>
 *<p>初始化：build(context)，只需要调用一次</p>
 * @since 1.0.0
 * @author xujiaoyong
 */
public class Configuration {

	private static final long CACHE_TTL = 3 * 60 * 60 * 1000;

	private static Configuration INSTANCE = new Configuration();

	private static Method SAVE_BOOLEAN = null;
	private static Method SAVE_LONG = null;
	private static Method SAVE_INT = null;
	private static Method SAVE_FLOAT = null;
	private static Method SAVE_STRING = null;

	private boolean debug = true;

	private IDiskCache mDiskCache;
	private IMemoryCache<String, CacheEntry> mMemoryCache;
	private Context mContext;
	private long mCacheTTL = CACHE_TTL;
	private boolean mNoPicture;
	private int mSign;
	private String mGlobalBasePath;
	private String mPublicBasePath;
	private boolean mInited;

	static {
		try {
			SAVE_BOOLEAN = Editor.class.getMethod("putBoolean", String.class,
					boolean.class);
			SAVE_INT = Editor.class
					.getMethod("putInt", String.class, int.class);
			SAVE_LONG = Editor.class.getMethod("putLong", String.class,
					long.class);
			SAVE_FLOAT = Editor.class.getMethod("putFloat", String.class,
					float.class);
			SAVE_STRING = Editor.class.getMethod("putString", String.class,
					String.class);

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private Configuration() {
	}

	private int getSign() {
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), PackageManager.GET_SIGNATURES);
			return info.signatures[0].toCharsString().hashCode();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return -1;
	}

	private Config parseConfig() {
		if (!mContext.getClass().isAnnotationPresent(Config.class)) {
			throw new RuntimeException("application unconfiged");
		}
		return mContext.getClass().getAnnotation(Config.class);
	}

	@SuppressLint("SdCardPath")
	private String parsePath(String path) {
		if (path.startsWith("$sdcard") && DirectoryUtils.haveSDCardPermission(mContext) && DirectoryUtils.existSDCard()) {
			path = path.replace("$sdcard", Environment
					.getExternalStorageDirectory().getAbsolutePath());
		}
		if (path.startsWith("$private")) {
			path = path.replace("$private",
					"/data/data/" + mContext.getPackageName());
		}
		if (path.startsWith("$global") && !TextUtils.isEmpty(mGlobalBasePath)) {
			path = path.replace("$global", mGlobalBasePath + "/");
		}

		if (path.contains("$pkgname")) {
			path = path.replace("$pkgname", mContext.getPackageName());
		}
		return path;
	}

	/**
	 * 
	 *<p>初始化配置</p><br/>
	 *<p>整个应用入口处初始化，只需要一次</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param context 上下文对象
	 * @return
	 */
	public synchronized Configuration buildConfig(Context context) {
		if (!mInited) {
			mContext = context;
			Config config = parseConfig();
			int appSign = getSign();
			mSign = config.sign();
			if (appSign == mSign) {
				debug = false;
			} else {
				debug = true;
			}

			if (!DirectoryUtils.haveSDCardPermission(mContext) || !DirectoryUtils.existSDCard()) {
				mGlobalBasePath = parsePath(config.globalBasePath());
				String basePath = "/data/data/" + context.getPackageName() + "/mzwres/sdk/";
				
				if (mGlobalBasePath.startsWith("$sdcard")){
					mGlobalBasePath = mGlobalBasePath.replace("$sdcard", basePath);
				}
				
				mPublicBasePath = parsePath(config.publicBasePath());
				if (mPublicBasePath.startsWith("$sdcard")){
					mPublicBasePath = mPublicBasePath.replace("$sdcard", basePath);
				}
				
				
			}else{
				mGlobalBasePath = parsePath(config.globalBasePath());
				mPublicBasePath = parsePath(config.publicBasePath());
			}
			
			

		}

		return this;

	}

	/**
	 * 
	 *<p>设置磁盘缓存</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param diskCache 磁盘缓存对象
	 * @return
	 */
	public Configuration setDiskCache(IDiskCache diskCache) {
		mDiskCache = diskCache;
		return this;
	}

	/**
	 * 
	 *<p>设置内存缓存</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param memoryCache 内存缓存对象
	 * @return
	 */
	public Configuration setMemoryCache(
			IMemoryCache<String, CacheEntry> memoryCache) {
		mMemoryCache = memoryCache;
		return this;
	}

	/**
	 * 
	 *<p>获取磁盘缓存</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public IDiskCache getDiskCache() {
		return mDiskCache;
	}

	/**
	 * 
	 *<p>获取内存缓存</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public IMemoryCache<String, CacheEntry> getMemoryCache() {
		return mMemoryCache;
	}

	/**
	 * 
	 *<p>获取配置对象</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public static Configuration getConfiguration() {
		return INSTANCE;
	}

	/**
	 * 
	 *<p>保存Preference数据</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param prefName 配置文件名称
	 * @param key 配置项key
	 * @param value 配置项value
	 * @return
	 * @throws Exception
	 */
	public Configuration savePreference(String prefName, String key,
			Object value) throws Exception {
		SharedPreferences preferences = null;
		if (TextUtils.isEmpty(prefName)) {
			preferences= mContext.getSharedPreferences("olsdk_"+mContext.getPackageName(), Context.MODE_PRIVATE);
		}else{
			 preferences = mContext.getSharedPreferences(prefName,
					Context.MODE_PRIVATE);
		}
		Editor editor = preferences.edit();
		Method method = null;
		if (value.getClass() == boolean.class
				|| value.getClass() == Boolean.class) {
			method = SAVE_BOOLEAN;
		} else if (value.getClass() == Float.class
				|| value.getClass() == float.class) {
			method = SAVE_FLOAT;
		} else if (value.getClass() == Long.class
				|| value.getClass() == long.class) {
			method = SAVE_LONG;
		} else if (value.getClass() == Integer.class
				|| value.getClass() == int.class) {
			method = SAVE_INT;
		} else {
			method = SAVE_STRING;
			value = String.valueOf(value);
		}
		method.invoke(editor, key, value);
		editor.commit();

		return this;
	}

	/**
	 * 
	 *<p>获取配置属性</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param prefName 配置文件名称
	 * @param key 配置项key
	 * @param defaultValue 默认值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getPreference(String prefName, String key, T defaultValue) {
		SharedPreferences preferences = null;
		if (TextUtils.isEmpty(prefName)) {
			preferences= mContext.getSharedPreferences("olsdk_"+mContext.getPackageName(), Context.MODE_PRIVATE);
		}else{
			 preferences = mContext.getSharedPreferences(prefName,
					Context.MODE_PRIVATE);
		}
		Map<String, ?> prefs = preferences.getAll();
		T data = (T) prefs.get(key);
		if (data == null) {
			return defaultValue;
		}
		return data;

	}

	/**
	 * 
	 *<p>判断是否为调试环境<br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public boolean isDebug() {
		return debug;
	}

	
	/**
	 * 无图模式
	 * @return
	 */
	public boolean isNoPicture() {
		return mNoPicture;
	}

	public void setNoPicture(boolean noPicture) {
		this.mNoPicture = noPicture;
	}

	/**
	 * 
	 *<p>获取全局路径</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public String getGlobalBasePath() {
		return mGlobalBasePath;
	}

	/**
	 * 
	 *<p>获取应用公开路径</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public String getPublicBasePath() {
		return mPublicBasePath;
	}

	/**
	 * 
	 *<p>获取默认缓存时间</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public long getCacheTTL() {
		return mCacheTTL;
	}

	/**
	 * 
	 *<p>设置默认缓存时间</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param cacheTTL
	 * @return
	 */
	public Configuration setCacheTTL(long cacheTTL) {
		mCacheTTL = cacheTTL;
		return this;
	}

}
