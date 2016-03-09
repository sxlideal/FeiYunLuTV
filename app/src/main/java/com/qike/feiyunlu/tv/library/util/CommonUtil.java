package com.qike.feiyunlu.tv.library.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CommonUtil {
	/** SD卡三种状 */
    public static enum MountStatuds
    {
        SD_CARD_AVAILABLE, SD_CARD_NOT_AVAILABLE, SD_CARD_SPACE_NOT_ENOUGH
    }
    /** 预设SD卡空间 (单位M) */
    public static final long   CACHE_SIZE   = 100;
    public static final int    MB           = 1024 * 1024;
    /** 默认为可用状 */
    public static MountStatuds SDCardStatus = MountStatuds.SD_CARD_AVAILABLE;
	/**
	 * 判断程序是否已安装
	 * @param packagename
	 * @return
	 */
	public static boolean isAppInstalled( Context context, String packagename )
	{
		if(context == null || TextUtils.isEmpty( packagename )){
			return false;
		}
		try
		{
			context.getPackageManager().getPackageInfo( packagename, 0 );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 判断程序是否已安装
	 * @param packagename
	 * @return
	 */
	public static boolean isAppInstalled( Context context, String packagename,int versionCode)
	{
		if(context == null || TextUtils.isEmpty( packagename )){
			return false;
		}
		try
		{
			PackageInfo info=context.getPackageManager().getPackageInfo( packagename, 0 );
			if(versionCode!=info.versionCode){
				return false;
			}
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	

	
	
	
	/**
	 *<p>MediaPlayer控制提示音</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 5.0.0
	 * @author suenxianhao
	 * @param activity
	 * @param resId
	 */
	public static void playAudio(Context activity, int resId) {
		try {
			MediaPlayer player = MediaPlayer.create(activity, resId);
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					relaxResources(mp, false);
				}
			});
			player.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	/**
	 *<p>mediaplayer控制</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 5.0.0
	 * @author suenxianhao
	 * @param player
	 * @param releaseMediaPlayer
	 */
	private static void relaxResources(MediaPlayer player, boolean releaseMediaPlayer) {
		try {
			if (releaseMediaPlayer && player != null) {
				player.reset();
				player.release();
				player = null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	 public static String getBasePath(Context context)
	    {
	        StringBuilder sb = new StringBuilder ();
	        SDCardStatus = getSDCardStatus ();

	        switch (SDCardStatus)
	        {
	            case SD_CARD_AVAILABLE:
	            case SD_CARD_SPACE_NOT_ENOUGH:
	                sb.append (Environment.getExternalStorageDirectory ().getPath ());
	                break;
	            case SD_CARD_NOT_AVAILABLE:
	                sb.append (context.getCacheDir ().getPath ());
	                break;
	        }
	        return sb.toString ();
	    }
	
	  public static MountStatuds getSDCardStatus()
	    {
	        MountStatuds status;
	        String sdState = Environment.getExternalStorageState ();
	        if (sdState.equals (Environment.MEDIA_MOUNTED))
	        {
	            File sdcardDir = Environment.getExternalStorageDirectory ();
	            StatFs sf = new StatFs (sdcardDir.getPath ());
	            long availCount = sf.getAvailableBlocks ();
	            long blockSize = sf.getBlockSize ();
	            long availSize = availCount * blockSize / MB;
	            /** 100M内存空间大小 */
	            if (availSize < CACHE_SIZE)
	            {
	                /** TODO 是否提示用户空间不够 */
	                status = MountStatuds.SD_CARD_SPACE_NOT_ENOUGH;
	            } else
	            {
	                status = MountStatuds.SD_CARD_AVAILABLE;
	            }
	        } else
	        {
	            status = MountStatuds.SD_CARD_NOT_AVAILABLE;
	        }
	        return status;
	    }
	  /**
     * 判断是否为空
     */
    public static boolean isEmpty(String content)
    {
        return content != null && "".equals (content);
    }

	   /**
     * 复制文本到系统剪切板
     */
    public static void copy2ClipboardManager(Context context, String text) {
     ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
     clipboard.setText(text);
    }
    /**
     * 
     *<p>启动游戏</p><br/>
     *<p>TODO(详细描述)</p>
     * @since 5.0.0
     * @author xky
     * @param context
     * @param packageName
     * @throws Exception
     */
	public static void startApp(Context context,String packageName) throws Exception {

		PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);

		}
	}
    
	/**
	 * 
	 *<p>TODO 判断是否安装谷歌框架</p><br/>
	 * @since 5.0.0
	 * @author ll
	 * @param context
	 * @return
	 */
	public static boolean isGsfInstalled(Context context) {
		boolean vendingInstalled = CommonUtil.isAppInstalled(context, "com.android.vending");
		boolean gsfInstalled = CommonUtil.isAppInstalled(context, "com.google.android.gsf");
		return vendingInstalled & gsfInstalled;
	}
	
	/**
	 * 
	 *<p>判断是否联网</p><br/>
	 * @since 5.0.0
	 * @author xky
	 * @param context
	 * @return
	 */
	public static boolean isNetworkEnable(Context context) {

		try {
			ConnectivityManager conn = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo workInfo = conn.getActiveNetworkInfo();
			if (workInfo != null) {
				return workInfo.isConnected();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static int doubeToPerenet(double data){
		  DecimalFormat df = new DecimalFormat("0.00");
		  String format = df.format(data);
		  int percent =   (int) (Double.valueOf(format)*100);
		  return percent;
	}
//	/**
//	 *
//	 *<p>根据包名取得本地应用的图标</p><br/>
//	 * @since 5.0.0
//	 * @author xky
//	 * @param context
//	 * @param packageName
//	 * @return
//	 */
//	public static Drawable getLocalAppIcon(Context context, String packageName) {
//
//		PackageManager pm;
//		ApplicationInfo appInfo;
//		try {
//			pm = context.getPackageManager();
//			appInfo = pm.getApplicationInfo(packageName, 0);
//			return appInfo.loadIcon(pm);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return context.getResources().getDrawable(R.drawable.ic_launcher);
//	}
	/**
	 * 
	 *<p>根据包名取得本地应用的标题</p><br/>
	 * @since 5.0.0
	 * @author xky
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String  getLocalAppTitle(Context context, String packageName) {
		
		PackageManager pm;
		ApplicationInfo appInfo;
		try {
			pm = context.getPackageManager();
			appInfo = pm.getApplicationInfo(packageName, 0);
			return appInfo.loadLabel(pm).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 
	 *<p>将半角字符转换为全角字符</p><br/>
	 *<p>防止TextView中排版混乱</p><br/>
	 * @since 5.0.0
	 * @author xky
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) { 
        char c[] = input.toCharArray(); 
        for (int i = 0; i < c.length; i++) { 
            if (c[i] == ' ') { 
                c[i] = '\u3000'; 
            } else if (c[i] < '\177') { 
                c[i] = (char) (c[i] + 65248); 
            } 
        } 
        return new String(c); 
    } 
	
	
	/**
	 * 判断是否可能有ROOT权限
	 * @return
	 */
	public static boolean haveRoot() {
		try {
			File file = new File("/system/bin/su");
			File file2 = new File("/system/xbin/su");
			return file.exists() || file2.exists();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;

	}
	/** 
	 * 需要ROOT权限
	 * @param cmds
	 * @return 0 means success, others means fail
	 */
	public static int executeRoot(List<String> cmds){
		PrintStream ps = null;
		try {
			System.out.println("ps start");
			final Process p = Runtime.getRuntime().exec("su");
			ps = new PrintStream(p.getOutputStream());
			
			new Thread(){
				@Override
				public void run() {
					try {
						final	BufferedReader reader  = new  BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = null;
						while((line = reader.readLine()) != null){
							Log.i("qike_debug_success", line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			new Thread(){
				@Override
				public void run() {
					try {
						final BufferedReader errorReader = new  BufferedReader(new InputStreamReader(p.getErrorStream()));
						String line = null;
						while((line = errorReader.readLine()) != null){
							Log.i("qike_debug_error", line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
			
			if(cmds != null){
				for (String execCmd : cmds) {
					ps.println(execCmd);
					ps.flush();
				}
			}
			ps.println("exit");
			ps.flush();
			System.out.println("ps flush");
			
			p.getOutputStream().close();
			p.waitFor();
			int code = p.exitValue();
			System.out.println("code:"+code);
			return code;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					long start = System.currentTimeMillis();
					ps.close();
					long end = System.currentTimeMillis();
					System.out.println("ps close:"+(end-start));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
	
	 /** 
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    /**
     * 
     *<p>获取随机订单</p>
     * @since 1.0.0
     * @author xky
     */
	public static String  getRandowOrderid(){
		Random random = new Random();
		int beforeInt = random.nextInt(90)+10;
		String time = DateUtil.converTime7(new Date().getTime());
		return beforeInt+time;
	}
    
}



