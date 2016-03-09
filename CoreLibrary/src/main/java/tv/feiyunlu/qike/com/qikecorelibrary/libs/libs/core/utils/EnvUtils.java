package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class EnvUtils {

    public static NetType netType;

    public static enum NetType {
	Mobile, Mobile_Proxy, Wifi, NoNetwork, Unknown
    }

    private static String CPU = "";

    public static String getCpuType() {
	InputStream is = null;
	try {
	    if (!TextUtils.isEmpty(CPU)) {
		return CPU;
	    }
	    Process p = Runtime.getRuntime().exec("getprop ro.board.platform");
	    is = p.getInputStream();
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	    CPU = bufferedReader.readLine();
	    if (!TextUtils.isEmpty(CPU)) {
		return CPU;
	    }
	    CPU = getMtkType();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (Exception e2) {
		    e2.printStackTrace();
		}
	    }
	}
	return CPU;
    }

    public static int getDensity(Context context) {
	return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getWidth(Context context) {
	return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context context) {
	return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static String getCountry() {
	return Locale.getDefault().getCountry();
    }

    private static String getMtkType() {
	try {
	    Process p = Runtime.getRuntime().exec("getprop ro.mediatek.platform");
	    InputStream is = p.getInputStream();
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
	    String result = bufferedReader.readLine();
	    if (!TextUtils.isEmpty(result)) {
		return result;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return "";
    }

    public static int getVersionCode(Context context) {
	try {
	    return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	}
	return 0;
    }

    public static String getImei(Context context) {
	try {
	    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    String imei = telephonyManager.getDeviceId();
	    return TextUtils.isEmpty(imei) ? "" : imei;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return "";
    }

    private static boolean networkEnable(Context context) {

	try {
	    ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo workInfo = conn.getActiveNetworkInfo();
	    if (workInfo != null) {
		return workInfo.isConnected();
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	return false;
    }

    public static boolean isWifiEnable(Context context) {
	return isWifiEnable(context, true);
    }

    public static boolean isWifiEnable(Context context, boolean immediately) {
	if (immediately) {
	    return isNetworkActive(context, ConnectivityManager.TYPE_WIFI);
	} else {
	    return netType == NetType.Wifi;
	}
    }

    public static boolean isNetworkEnable(Context context) {
	return networkEnable(context);
    }

    private static boolean isNetworkActive(Context context, int type) {
	try {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo nInfo = cm.getActiveNetworkInfo();
	    return nInfo.getType() == type;
	} catch (Exception e) {
	    // e.printStackTrace();
	}
	return false;
    }

    public static boolean isGPRSEnable(Context context) {
	return isGPRSEnable(context, true);
    }

    public static boolean isGPRSEnable(Context context, boolean immediately) {
	if (immediately) {
	    return isNetworkActive(context, ConnectivityManager.TYPE_MOBILE);
	} else {
	    return netType == NetType.Mobile || netType == NetType.Mobile_Proxy;
	}
    }

    public static boolean isSDCardMouted() {
	try {
	    String state = Environment.getExternalStorageState();
	    return state.equals(Environment.MEDIA_MOUNTED);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
    }

}
