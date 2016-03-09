package com.qike.feiyunlu.tv.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * class name：SIMCardInfo<BR>
 * class description：读取Sim卡信息<BR>
 * PS： 必须在加入各种权限 <BR>
 * Date:2012-3-12<BR>
 * 
 * @version 1.00
 */
public class SIMCardUtils {

	/**
	 * Role:获取当前设置的电话号码
	 * <BR>Date:2012-3-12
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String nativePhoneNumber = null;
		nativePhoneNumber = telephonyManager.getLine1Number();

		if (!TextUtils.isEmpty(nativePhoneNumber)) {

			Pattern p = Pattern.compile("^((\\+{0,1}86){0,1})");
			Matcher m = p.matcher(nativePhoneNumber);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);
			return sb.toString();
		} else {

			return "";
		}

	}

	/**
	 *<p>将手机号的中间四位加密</p><br/>
	 *<p>将手机号的中间四位加密</p>
	 * @since 1.0.0
	 * @author cxd
	 * @param phoneNumber 手机号码
	 * @return
	 */
	public static String replacePhone(String phoneNumber) {

		Pattern p = Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
		Matcher m = p.matcher(phoneNumber);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "$1****$3");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 <BR>
	 * 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/> <BR>
	 * Date:2012-3-12 <BR>
	 * 
	 */
	public static String getProvidersName(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}

	/**
	 * 
	 *<p>TODO(判断是否是大陆手机卡)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param context
	 * @return
	 */
	public static boolean isTheChineseMainland(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		if (!TextUtils.isEmpty(IMSI)) {
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			return (getNativePhoneNumber(context).length() == 11) || IMSI.startsWith("46000") || IMSI.startsWith("46002")
					|| IMSI.startsWith("46001") || IMSI.startsWith("46003");
		} else {
			return false;
		}

	}

	/**
	 * 
	 *<p>TODO(判断是否有短信权限)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param context
	 * @return
	 */
	public static boolean hasSMSPermission(Context context) {
		try {
			String[] array = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
			for (String string : array) {
				if (string.equals(permission.RECEIVE_SMS)) {

					return true && !TextUtils.isEmpty(getProvidersName(context));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 
	 *<p>TODO(判断sim卡是否可用)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param context
	 * @return
	 */

	public static boolean isCanUseSim(Context context) {
		try {
			TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 *<p>TODO(手机号码校验)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param number
	 * @return
	 */
	public static boolean isPhone(String number) {

		Pattern p = Pattern.compile("^13[0-9]{1}[0-9]{8}$|15[0-9]{1}[0-9]{8}$|18[0-9]{1}[0-9]{8}$");
		Matcher m = p.matcher(number);

		return m.matches();
	}

	/**
	 * 
	 *<p>TODO(邮箱校验)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		Pattern p = Pattern.compile("^(\\w|\\.|-|\\+)+@(\\w|-)+(\\.(\\w|-)+)+$");
		Matcher m = p.matcher(email);

		return m.matches();
	}

}