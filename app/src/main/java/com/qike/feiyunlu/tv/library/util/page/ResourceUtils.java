package com.qike.feiyunlu.tv.library.util.page;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ResourceUtils {
//	public static int getDrawableID(Context context, String name) {
//		return getResource(context, "drawable", name);
//	}
//
//	public static int getStringID(Context context, String name) {
//		return getResource(context, "string", name);
//	}
//
//	public static int getDimenID(Context context, String name) {
//		return getResource(context, "dimen", name);
//	}
//
//	public static int getColorID(Context context, String name) {
//		return getResource(context, "color", name);
//	}
//
//	public static int getID(Context context, String name) {
//		return getResource(context, "id", name);
//	}
//
//	public static int getArrayID(Context context, String name) {
//		return getResource(context, "array", name);
//	}
//
//	public static int getStyleID(Context context, String name) {
//		return getResource(context, "style", name);
//	}
//
//	public static int getLayoutID(Context context, String name) {
//		return getResource(context, "layout", name);
//	}

//	public static int getResource(Context context, String prefix, String name) {
//		try {
//			String className = context.getPackageName() + ".R$" + prefix;
//
//			Class clazz = Class.forName(className);
//			return clazz.getField(name).getInt(null);
//
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}
	
	public static String getTempPath(Context context) {

		return context.getFilesDir().getAbsolutePath()
				+"/qikeres/update/temp.patch";
	}

	public static String getDestPath(Context context) {
		return context.getFilesDir().getAbsolutePath()
				+ "/qikeres/update/qike.patch";
	}

	public static String getLoadPath(Context context) {
		return context.getFilesDir().getAbsolutePath()
				+ "/qikeres/update/dexes";
	}
	
	public static ResourceEntry getResourceEntry(Context context,
			DisplayMetrics dm,Configuration config) {
		try {

			AssetManager am = AssetManager.class.getConstructor().newInstance();
			Method method = am.getClass().getDeclaredMethod("addAssetPath",
					String.class);
			method.setAccessible(true);
			method.invoke(am, getDestPath(context));
			
			Resources resources = new Resources(am, dm, config);
			return new ResourceEntry(am, resources);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	public static class ResourceEntry {
		private AssetManager assetManager;
		private Resources resources;

		public ResourceEntry(AssetManager assetManager, Resources resources) {
			super();
			this.assetManager = assetManager;
			this.resources = resources;
		}

		public AssetManager getAssetManager() {
			return assetManager;
		}

		public void setAssetManager(AssetManager assetManager) {
			this.assetManager = assetManager;
		}

		public Resources getResources() {
			return resources;
		}

		public void setResources(Resources resources) {
			this.resources = resources;
		}

	}
}
