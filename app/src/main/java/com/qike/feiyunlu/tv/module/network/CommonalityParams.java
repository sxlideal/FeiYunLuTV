package com.qike.feiyunlu.tv.module.network;

import android.text.TextUtils;

import com.qike.feiyunlu.tv.library.util.Device;
import com.qike.feiyunlu.tv.library.util.MemoryCache;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.SecurityUtils;


public class CommonalityParams {

	public static final String USER_UNIQUE = "dec4b3722f";
	public static final String SECRET_KEY = "59ca285101ede1db9140d72b2c8914af"; 
	public static final String QIKE_VERSION_KEY = "qikeversion";
	public static final String SOURCE_KEY = "source";
	public static final String CPU_KEY = "cpu";
	public static final String BRAND_KEY = "brand";
	public static final String MODEL_KEY = "model";
	public static final String SYSVERSION_KEY = "sysversion";
	public static final String LOCALE_KEY = "locale";
	public static final String DENSITY_KEY = "density";
	public static final String USERID_KEY = "uid";
	public static final String ACCESSTOKEN_KEY = "accesstoken";
	public static final String DEVICE_ID="deviceid";
	private String mUrl;

	public Map<String, Object> initGeneralParams(String url, Map<String, Object> mParams) {
		try {
			mUrl = url;
//			mParams.put(Paths.KEY, initGeneralKey(url, mParams));
//			mParams.put(BRAND_KEY, URLEncoder.encode(Build.MANUFACTURER, "UTF-8"));
//			mParams.put(MODEL_KEY, URLEncoder.encode(Build.MODEL, "UTF-8"));
//			mParams.put(QIKE_VERSION_KEY, Device.getSoftVersion(QikeApplication.getApplication()));
//			mParams.put(SYSVERSION_KEY, String.valueOf(Build.VERSION.SDK_INT));
//			mParams.put(CPU_KEY, Device.getCpuType());
//			mParams.put(DENSITY_KEY, Device.getDenisity(QikeApplication.getApplication()));
//			mParams.put(SOURCE_KEY, MemoryCache.SOURCE);
//			mParams.put(DEVICE_ID, UniqueUtils.getUniqueID(QikeApplication.getApplication()));
			try {

				Integer localeValue = (Integer) MemoryCache.get(MemoryCache.PROPERTIES_LOCALE_KEY);
				if (localeValue == null) {
					localeValue = 0;
				}
				Locale locale = Device.LANGUAGE_CATEGORY[localeValue];

				String language = locale.getLanguage();
				String country = locale.getCountry().toLowerCase();
				if (TextUtils.isEmpty(country) && language.contains("zh")) {//更换简体语言时  country会为空 
					country = "cn";
				}
//				mParams.put(LOCALE_KEY, country);

//				User user = AccountManager.getUser();
//				mParams.put(USERID_KEY, user.getUserid());
//				mParams.put(ACCESSTOKEN_KEY, user.getAccesstoken());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return mParams;
	}

	public String convertParams(Map<String, Object> mParams) {
		try {
			StringBuilder sb = new StringBuilder();

			if (mParams != null && mParams.size() > 0) {
				for (Map.Entry<String, Object> entry : mParams.entrySet()) {
					sb.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("&");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String initGeneralKey(String url, Map<String, Object> mParams) {
		String paramString = convertParams(mParams);
		String sign = null;
		try {
			URI uri = new URI(mUrl);
			String path = uri.getHost() + uri.getPath();
			sign = SecurityUtils.getMd5(SecurityUtils.getMd5(path, "UTF-8") + paramString, "UTF-8");

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sign;
	}

}