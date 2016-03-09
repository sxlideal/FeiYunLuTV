package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.SecurityUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpUtil;


public class URLParamsUtils {
	//	public static String convertParams(Map<String, Object> params) {
	//		try {
	//			StringBuilder sb = new StringBuilder();
	//			
	//			if(params != null && params.size() >0){
	//				for (Map.Entry<String, Object> entry : params.entrySet()) {
	//					sb.append(entry.getKey()).append("=")
	//							.append(entry.getValue().toString()).append("&");
	//				}
	//				sb.deleteCharAt(sb.length() - 1);
	//			}
	//			return sb.toString();
	//		} catch (Throwable e) {
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
	public static String decodeResponse(String value) {
		byte[] data = Base64.decode(value);
		byte[] resultData = SecurityUtils.decrypt(data);
		return new String(resultData);
	}

	public static HashMap<String, Object> encodeParams(String key, HashMap<String, Object> params) {
		try {
			String result = HttpUtil.convertParams(params);
			byte[] data = SecurityUtils.encrypt(result.getBytes("UTF-8"));
			result = Base64.encode(data);

			params.put(key, URLEncoder.encode(result.replace("\n", ""), "UTF-8"));

			return params;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String initGeneralKey(String url, Map<String, Object> params) {

		String sign = "";
		try {
			String paramString = HttpUtil.convertParams(params);
			URI uri = new URI(url);
			String path = uri.getHost() + uri.getPath();
			sign = SecurityUtils.getMd5(SecurityUtils.getMd5(path, "UTF-8") + paramString, "UTF-8");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sign;
	}
}
