package com.qike.feiyunlu.tv.library.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;


public class SystemApiUtil
{

	/**
	 * 获取应用版本号（int）
	 * @param pm
	 * @param packageName
	 * @return
	 */
	public static int queryVersionCode(PackageManager pm,String packageName)
	{
		PackageInfo packageInfo = queryPackageInfo (pm, packageName);
		if (packageInfo == null)
		{
			return -1;
		} else
		{
			return packageInfo.versionCode;
		}
	}
	
	/**
	 * 获取应用版本名（string）
	 * @param pm
	 * @param packageName
	 * @return
	 */
    public static String queryVersionName(PackageManager pm,String packageName)
    {
        PackageInfo packageInfo = queryPackageInfo (pm, packageName);
        if (packageInfo == null)
        {
            return null;
        } else
        {
            return packageInfo.versionName;
        }
    }

    public static boolean isAppInstall(PackageManager pm,String packageName)
    {
        PackageInfo packageInfo = queryPackageInfo (pm, packageName);
        if (packageInfo == null)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public static int versionCode(PackageManager pm,String packageName)
    {
        PackageInfo packageInfo = queryPackageInfo (pm, packageName);
        if (packageInfo == null)
        {
            return -1;
        } else
        {
            return packageInfo.versionCode;
        }
    }

    public static PackageInfo queryPackageInfo(PackageManager pm,String packageName)
    {
        if (pm == null || CommonUtil.isEmpty (packageName)) { return null; }
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = pm.getPackageInfo (packageName, 0);
        } catch (NameNotFoundException e)
        {
            return null;
        }
        return packageInfo;
    }

    public static String queryAppName(PackageManager pm,String packageName)
    {
        PackageInfo packageInfo = queryPackageInfo (pm, packageName);
        if (packageInfo == null) { return null; }
        return packageInfo.applicationInfo.loadLabel (pm).toString ();
    }

    /**
     * TODO 可能缺乏版本号风险
     * 
     * @param packName
     * @return
     */
    public static boolean isRunningTask(Context context,String packName)
    {

        ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> lists = am.getRunningAppProcesses ();
        for ( RunningAppProcessInfo infos : lists )
        {
            if (infos.processName.equals (packName) == true) { return true; }
        }
        return false;
    }

    /**
     * TODO 命令行方式关闭其它应用程序,非官方
     * 
     * @param packName
     */
    public static void killApp(String packName)
    {

        Process p = null;
        PrintStream ps = null;
        try
        {
            p = Runtime.getRuntime ().exec ("su");
            ps = new PrintStream (p.getOutputStream ());
            ps.println ("service call activity 79 s16 " + packName);

        } catch (Exception e)
        {} finally
        {
            if (ps != null)
            {
                ps.flush ();
                ps.println ("exit");
                ps.flush ();
                if (p != null) try
                {
                    p.waitFor ();
                } catch (InterruptedException e)
                {
                    e.printStackTrace ();
                }
                ps.close ();
            }
        }
    }

    @TargetApi(8)
    public static void killAppByApi(Context context,String packName)
    {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1)
        {

        }
        ActivityManager am = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);

        am.restartPackage (packName);
        am.killBackgroundProcesses (packName);
    }

    /**
     * 获得系统正在运行的进程信息
     */
    public static boolean isRunningAppProcessInfo(ActivityManager mActivityManager,String packageName)
    {
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses ();

        for ( RunningAppProcessInfo appProcessInfo : appProcessList )
        {
            String[] packageList = appProcessInfo.pkgList;
            for ( String pkg : packageList )
            {
                if (packageName.equals (pkg)) { return true; }
            }
        }
        return false;
    }
    public static String getCpuType() {
		try {
			Process p = Runtime.getRuntime().exec("getprop ro.board.platform");
			InputStream is = p.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(is));
			String result = bufferedReader.readLine();
			if (!isEmpty(result)) {
				return result;
			}
			return getMtkType();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
    public static String getMtkType() {
		try {
			Process p = Runtime.getRuntime().exec(
					"getprop ro.mediatek.platform");
			InputStream is = p.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(is));
			String result = bufferedReader.readLine();
			if (!isEmpty(result)) {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static  boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}
//    /**
//     * 调用系统发送消息
//     */
//    public static void startSystemMessageIntent(Context context,String message,String appTitle)
//    {
//        try
//        {
//            Intent intent = new Intent (Intent.ACTION_SEND);
//            intent.setType ("text/plain");
//            intent.putExtra (Intent.EXTRA_SUBJECT, context.getString (R.string.qike_share));
//            intent.putExtra (Intent.EXTRA_TEXT, message);
//            context.startActivity (Intent.createChooser (intent, appTitle));
//        } catch (Exception e)
//        {
//            e.printStackTrace ();
//        }
//    }

}
