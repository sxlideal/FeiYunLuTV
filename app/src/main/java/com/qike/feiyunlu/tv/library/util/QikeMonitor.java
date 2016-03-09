package com.qike.feiyunlu.tv.library.util;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *<p>游戏管理监听器</p><br/>
 * @since 5.0.0
 * @author sunxh
 */
public class QikeMonitor {
	public static final int INSTALL_KEY=0;
	public static final int UNINSTALL_KEY=1;
	private static QikeMonitor mMonitor;
	private static Map<String, QikeMonitorCallback> mMonitorMap = new HashMap<String, QikeMonitorCallback>();
	public static Map<String, QikeMonitorCallback> getmMonitorMap() {
		return mMonitorMap;
	}
	
	/**
	 *<p>启动监听者</p><br/>
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	public void IssuedMonitor(int result,String packagename) {
		Iterator iter = mMonitorMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			QikeMonitorCallback val =(QikeMonitorCallback) entry.getValue();
			val.AppOperation(result,packagename);
		}
	}

	/**
	 *<p>监听者初始化</p><br/>
	 * @since 5.0.0
	 * @author sunxh
	 * @return
	 */
	public static QikeMonitor getInstance() {
		if (mMonitor == null) {
			mMonitor = new QikeMonitor();
		}
		return mMonitor;
	}

	/**
	 *<p>监听者注册</p><br/>
	 * @since 5.0.0
	 * @author sunxh
	 */
	public void register(QikeMonitorCallback listener, String key) {
		mMonitorMap.put(key, listener);
	}

	/**
	 *<p>判断是否已注册</p><br/>
	 * @since 5.0.0
	 * @author sunxh
	 * @param key
	 * @return
	 */
	public boolean isRegister(String key) {

		if (mMonitorMap.get(key) != null) {
			return true;
		}
		return false;

	}

	/**
	 *<p>取消注册</p><br/>
	 * @since 5.0.0
	 * @author sunxh
	 * @param key
	 */
	public void cancelRegister(String key) {
		mMonitorMap.remove(key);
	}

	/**
	 *<p>游戏操作监听接口</p><br/>
	 * @since 5.0.0
	 * @author sunxh
	 */
	public interface QikeMonitorCallback {
		public void AppOperation(int result, String packagename);

	}
}
