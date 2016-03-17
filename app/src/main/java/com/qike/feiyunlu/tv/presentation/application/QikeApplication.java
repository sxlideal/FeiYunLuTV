package com.qike.feiyunlu.tv.presentation.application;


import android.app.Application;

import com.qike.feiyunlu.tv.library.util.UiUtils;
import com.qike.feiyunlu.tv.module.database.DatabasePath;
import com.qike.feiyunlu.tv.module.database.QikeDatabaseProvider;

import java.io.File;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.impl.DiskBasedCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.impl.LRULimitedMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Config;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception.Delivery;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception.ExceptionManager;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception.Level;

/**
 * 程序的Application类
 * @since 5.0.0
 * @author sunxianhao
 */
@Config(globalBasePath="$sdcard/7k7kFeiYunLu",publicBasePath="$global/$pkgname/",sign=0)
public class QikeApplication extends Application {
	private static Application mApp = null;
	private ExceptionManager mException;
	private static QikeDatabaseProvider mDatabase;

	@Override
	public void onCreate() {
		super.onCreate();
		mApp=this;
		//用户行为统计初始化
		statisticsInit();
		//第三方初始化
		librariesInit();
		//core初始化
		coreInit();
		//本地常用数据初始化（存入缓存池内）
		LocalCommonDataInit();
		//异常捕获初始化
		exceptionCatchInit();
		//开始自动检查更新
		startAutoCheckUpdate();

	}
	/**
	 *<p>异常捕获初始化</p><br/>
	 *<p>处理全局异常未捕获初始化（功能完成后解开注释）</p>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void exceptionCatchInit() {
		mException=ExceptionManager.getInstance();
//		mException.registerDelivery(new QikeDelivery());
	}
	/**
	 *<p>异常监听</p><br/>
	 *<p>统一处理异常捕获</p>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private class QikeDelivery implements Delivery {

		@Override
		public boolean onCatch(Level level, Throwable ex) {
			UiUtils.finishAllALiveAcitity();
			return false;
		}

	}
	/**
	 *<p>获取常用数据库对象</p><br/>
	 * @since 1.0.0
	 * @author xianhao
	 * @return
	 */
	public static QikeDatabaseProvider getQikeDatabaseProvider() {
	
		return mDatabase;
	}
	
	/**
	 *<p>本地常用数据初始化</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	private void LocalCommonDataInit() {
		if (mDatabase == null) {
			mDatabase = new QikeDatabaseProvider(this, DatabasePath.getDownloadSavePath()
					+ DatabasePath.EXENAL_GAME_DATABASE_PATH, QikeDatabaseProvider.DATABASE_NAME);
		}

	}
	/**
	 *<p>core初始化</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void coreInit() {
		
		
		Configuration config = Configuration.getConfiguration();
		config.buildConfig(this);
		config.setCacheTTL(5*60*1000);
		IDiskCache diskCache = new DiskBasedCache(new File(config.getPublicBasePath() + "/cache"));
		diskCache.initialize();
		config.setDiskCache(diskCache);
		config.setMemoryCache(new LRULimitedMemoryCache(5*1024*1024));
		
	}
	
	/**
	 *<p>获取数据库操作对象实例</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 * @return
	 */
//	public static DatabaseProvider getmProvider() {
//		return mProvider;
//	}

	
	/**
	 *<p>第三方初始化</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void librariesInit() {
		//初始化友盟相关
//		UmengInit.initUmengService();
//
//		ShareSDKInit.initShareSDK(getApplicationContext());
	}

	/**
	 *<p>用户行为统计初始化</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void statisticsInit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	/**
	 *<p>全局获取Application</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 * @return
	 */
	public static Application getApplication() {
		return mApp;
	}

		
	/**
	 * 
	 *<p>开启自动更新</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void startAutoCheckUpdate(){
		// TODO Auto-generated method stub
	}
	
}
