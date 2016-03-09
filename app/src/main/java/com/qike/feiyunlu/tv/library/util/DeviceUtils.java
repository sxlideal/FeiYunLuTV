package com.qike.feiyunlu.tv.library.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;

public class DeviceUtils {
	/**
	 * 
	 * <p>
	 * 获取屏幕的宽和高
	 * </p>
	 * <br/>
	 * 
	 * @since 5.0.0
	 * @author xky
	 * @param activity
	 * @return int[] int[0]=widht;int[1]=height
	 */
	public static int[] getScreenWidthAndHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		Display display = context.getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		int[] wh = { dm.widthPixels, dm.heightPixels };
		return wh;
	}

	public static int getScreenWidth( Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		Display display = context.getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		return dm.widthPixels;

	}

	public static int getScreenHeight( Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		Display display = context.getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		return dm.heightPixels;

	}

	/**
	 * 
	 *<p>获取位深</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 * @param context
	 * @return
	 */
	public static int getPixe(Activity context) {
		Display display = context.getWindowManager().getDefaultDisplay();
		int pixelformat = display.getPixelFormat();
		PixelFormat	localPixelFormat1 = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		int deepth = localPixelFormat1.bytesPerPixel;// 位深
		return deepth;
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
	
}
