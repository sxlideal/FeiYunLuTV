package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;

public class UniqueUtils {

	private static String id;
	private static String mac;

	
	public static void saveUniqueID(Context context,String uniqueid)throws Throwable {
			FileOutputStream os = null;
			try {
				id = uniqueid;
				File file = new File(getUniqueIDSavePath(context));
				//				if (!file.exists()) {
				os = getOutputStream(file);
				os.write(id.getBytes("UTF-8"));
				//				}
			} catch (Throwable e) {
				throw e;
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	private static boolean isCorrectImei(String imei){
		
		if (!TextUtils.isEmpty(imei)) {
			if (imei.startsWith("000000000000000")) {
				return false;
			}
			return true;
		}
		
		return false;
	}
	public static String getUniqueID(Context context)  {
		try {
			FileInputStream ins = null;
			FileOutputStream os = null;
			try {
				if (!TextUtils.isEmpty(id)) {
					return id;
				}
				File file = new File(getUniqueIDSavePath(context));
				if (!file.exists()) {
					
					String imei = EnvUtils.getImei(context);
					if (isCorrectImei(imei)) {
						id = imei;
					}else{
						id = UUID.randomUUID().toString();
					}
					os = getOutputStream(file);
					os.write(id.getBytes("UTF-8"));
				} else {
					ins = new FileInputStream(file);
					byte[] buff = new byte[(int) file.length()];
					ins.read(buff);
					id = new String(buff, "UTF-8");
				}
				return id;
			} catch (Throwable e) {
				throw e;
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (ins != null) {
					try {
						ins.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return "none";
	}

	public static String getMacAddress(Context context) {
		if (!TextUtils.isEmpty(mac)) {
			return mac;
		}
		FileInputStream ins = null;
		FileOutputStream os = null;
		try {
//			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !haveSDCardPermission(context)) {
//				WifiManager wm = (WifiManager) context
//						.getSystemService(Context.WIFI_SERVICE);
//				WifiInfo wifi = wm.getConnectionInfo();
//				if (wifi != null) {
//					mac = wifi.getMacAddress();
//				}
//				return mac;
//			}

			File file = new File(getMacSavePath(context));
			if (!file.exists()) {
				WifiManager wm = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifi = wm.getConnectionInfo();
				if (wifi != null) {
					mac = wifi.getMacAddress();
					if (!TextUtils.isEmpty(mac)) {
						os = getOutputStream(file);
						os.write(mac.getBytes("UTF-8"));
					}
				}
			} else {
				ins = new FileInputStream(file);
				byte[] buff = new byte[(int) file.length()];
				ins.read(buff);
				mac = new String(buff, "UTF-8");
			}
			return mac;

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	private static String getUniqueIDSavePath(Context context) {
		String sdcardPath = getRootDirPath(context);
		String path = sdcardPath + "/data/.systemid";
		return path;
	}

	private static String getMacSavePath(Context context) {
		
		String sdcardPath = getRootDirPath(context);
		String path = sdcardPath + "/data/.systemmac";
		return path;
	}
	
	
	public static String getRootDirPath(Context context) {
		if (haveSDCardPermission(context)
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/";
		} else {
			return  context.getFilesDir().getAbsolutePath()+"/mzwsdk/";
		}
	}
	
	public static boolean haveSDCardPermission(Context context){
		try {
			String[] array = 	context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
			for (String string : array) {
				if (string.equals(permission.WRITE_EXTERNAL_STORAGE)) {
					return true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	private static FileOutputStream getOutputStream(File file)
			throws FileNotFoundException {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return new FileOutputStream(file);
	}

}
