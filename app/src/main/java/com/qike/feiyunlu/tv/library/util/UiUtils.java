package com.qike.feiyunlu.tv.library.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.WindowManager;

/**
 * UI相关工具类<br/>
 * 此类包含所有与ui相关的公共方法，包括activity退出等操作
 * 
 * @since 5.0.0
 * @version 5.0.0
 * @author sunxianhao
 * 
 */
public class UiUtils {
	public static final Locale DEFAULT = Locale.getDefault();
	public static final Locale CHINESE = Locale.CHINESE;
	public static final Locale ENGLISH = Locale.ENGLISH;
	public static final Locale KOREAN = Locale.KOREAN;
	public static final Locale TAIWAN = Locale.TAIWAN;
	private static List<Activity> mALiveActivity = null;
	private static long lastClickTime;
	private static int one;
	private static final SparseArray<Object> resources = new SparseArray<Object>();

	public static final int RES_DOWNLOAD = -1;
	public static final int RES_INSTALL = -2;
	public static final int RES_UPDATE = -3;
	public static final int RES_ANALYTICS = -4;
	public static final int RES_DOWNLOAD_OPTIONS = -5;
	public static final int RES_NOTIFICATION = -6;

	/** 文件系统 */
	public static final int RES_DirectoryManager = -7;
	/** pchelper **/
	public static final int RES_PCHELPER = -8;

	public static boolean inited = false;
	public static boolean debug = false;

	public static boolean PACKAGECUTED = false;
	public static boolean CUTED = false;
	public static boolean INTERNATIONAL = false;

	public static String MARKET_HOST = "market://details";
	public static String MARKET_PACKAGENAME = "com.android.vending";
	public static String MARKET_ACTIVITY = "com.android.vending.AssetBrowserActivity";

	public static String AIRPUSH_CREATIVEID = "273957";
	public static String AIRPUSH_CAMPAIGNID = "102768";

	public static boolean CDN_DISABLE = false;

	@SuppressWarnings("unchecked")
	public static <T> T getResource(Integer id) {
		try {
			return (T) resources.get(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void registerResource(Integer id, Object obj) {
		resources.put(id, obj);
	}

//	@SuppressWarnings("rawtypes")
//	public static synchronized void initResources(final Context activity) {
//		if (inited) {
//			return;
//		}
//		// cdn init
//		String value = MobclickAgent.getConfigParams(activity, "qike_cdn_disable");
//		if (!TextUtils.isEmpty(value)) {
//			if (value.equals("1")) {
//				CDN_DISABLE = true;
//			} else {
//				CDN_DISABLE = false;
//			}
//		}
//		// notification init
//		NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//		registerResource(RES_NOTIFICATION, nm);
//
//		// local lanage init
//		String country = activity.getResources().getConfiguration().locale.getCountry();
//		if (!TextUtils.isEmpty(country)) {
//			country = country.toUpperCase();
//			if ("TW".equals(country) || "HK".equals(country)) {
//				Paths.PATH_PREFIX_GENERAL = Paths.PATH_PREFIX_GENERAL_TW;
//				Paths.PATH_PREFIX_WWW = Paths.PATH_PREFIX_WWW_TW;
//			}
//		}
//
//	}

	public static PackageInfo queryPackageInfo(PackageManager pm, String packageName) {
		if (pm == null || CommonUtil.isEmpty(packageName)) {
			return null;
		}
		PackageInfo packageInfo = null;
		try {
			packageInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			return null;
		}
		return packageInfo;
	}

	public static String queryAppName(PackageManager pm, String packageName) {
		PackageInfo packageInfo = queryPackageInfo(pm, packageName);
		if (packageInfo == null) {
			return null;
		}
		return packageInfo.applicationInfo.loadLabel(pm).toString();
	}

	/**
	 * 将activity加入集合<br/>
	 * 每一个activity启动时都需要加入集合进行统一管理
	 * 
	 * @since 5.0.0
	 * @param activity
	 * @author sunxianhao
	 */
	public static void createActivity(Activity activity) {
		if (mALiveActivity == null) {
			mALiveActivity = new ArrayList<Activity>(5);
		}
		if (!mALiveActivity.contains(activity)) {
			mALiveActivity.add(activity);
		}
	}

	/**
	 *<p>资源初始化</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	private void initGlobalResources(Activity activity) {
		NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		registerResource(RES_NOTIFICATION, nm);
	}

	/**
	 * 销毁特定的activity
	 * 
	 * @param activity
	 * @sine 5.0.0
	 * @author sunxianhao
	 */
	public static boolean destroyActivity(Activity activity) {
		if (mALiveActivity == null) {
			return false;
		}
		return mALiveActivity.remove(activity);
	}

	/**
	 * 获取当前activity的数量
	 * 
	 * @return int型
	 * @since 5.0.0
	 * @author sunxianhao
	 */
	public static int getActivityCount() {
		if (mALiveActivity == null) {
			return 0;
		}
		return mALiveActivity.size();
	}

	/**
	 * 退出所有activity
	 * 
	 * @since 5.0.0
	 * @author sunxianhao
	 */
	public static void finishAllALiveAcitity() {
		if (mALiveActivity == null || mALiveActivity.size() < 1) {
			return;
		}
		for (Activity activity : mALiveActivity) {
			//			delenot(activity);
			
			activity.finish();
		}

		mALiveActivity = null;
	}
	public static List<Activity> getActivityList() {
		return mALiveActivity;

	}

	/**
	 * 
	 *<p>TODO(关闭所有activity，只保留testActivity)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 */
	public static void restartAllLiveActivity(Class<? extends Activity> aClass) {

		if (mALiveActivity == null || mALiveActivity.size() < 1) {
			return;
		}

		for (int i = 0; i < getActivityCount(); i++) {

			Activity activity = mALiveActivity.get(i);

			if (aClass == null || activity.getClass() != aClass) {

				mALiveActivity.get(i).finish();

			}

		}
	}

	/**
	 * 
	 *<p>TODO(通过Class删除特定的Activity)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param aClass
	 */
	public static void destorySpecificActiviaty(Class<? extends Activity> aClass) {

		for (int i = 0; i < getActivityCount(); i++) {

			Activity activity = mALiveActivity.get(i);
			if (activity.getClass() == aClass) {

				mALiveActivity.get(i).finish();

			}

		}

	}


	/**
	 * 清除通知栏
	 * @since 5.0.0
	 * @author sunxianhao
	 * @param activity
	 */
	public static void delenot(Activity acticity) {
		NotificationManager notificationManager = (NotificationManager) acticity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}

	/**
	 * 变更应用语言
	 * @param locale 常量LANGUAGE_CATEGORY的值
	 */
	public static void changeLanguage(Context context, Locale locale) {
		Configuration config = context.getResources().getConfiguration();
		config.locale = locale;
		context.getResources().updateConfiguration(config, null);
	}

	

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 900) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕方向
	 */
	public static int getOrientation(Context context) {

		Configuration configuration = context.getResources().getConfiguration();

		if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			return Configuration.ORIENTATION_PORTRAIT;
		} else {
			return Configuration.ORIENTATION_LANDSCAPE;
		}

	}

	/**
	 * 获取屏幕density属性的等级 包括:low, mid, high, x
	 */
	public static float getDensity(Context context) {
		WindowManager mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		;
		mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		return mDisplayMetrics.density;
	}

	/**
	 * 清除缓存
	 * 
	 */
	public static void delCacheData(Context context) {
//		DirectoryManagerV1Impl.getInstance().cleanPublic("cachee");
	}

}
