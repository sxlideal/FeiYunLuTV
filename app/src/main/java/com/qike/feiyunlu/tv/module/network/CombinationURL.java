package com.qike.feiyunlu.tv.module.network;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 
 *<p>对所有的url进行组合</p><br/>
 * @since 1.0.0
 * @author xky
 */
public class CombinationURL {
	private CommonalityParams mInitParams;
	private Map<String, Object> mParams;
	private String mUrl;
	
	public CombinationURL(String  url) {
		mUrl = url;
		mInitParams = new CommonalityParams();
		mParams = new HashMap<String, Object>();
	}
	/**
	 * 
	 *<p>设置参数</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param key
	 * @param value
	 */
	public void putParams(String key,Object value){		
		mParams.put(key, value);
	}
	/**生成订单号*/
	public static String createMerchantOrderId() {
		Random r = new Random();
		Double d = r.nextDouble();
		String s = d + "";
		s=s.substring(3,3+8);
		Log.e("OrderId:", s);
		Log.e("-----------------", "订单"+s);
		return s;
	}
	
	
	/**
	 * 
	 *<p>获取组合后的参数</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 */
	public String getCombinationURL(Context context){
		Map<String, Object> params = mInitParams.initGeneralParams(mUrl, mParams);
		return mUrl+map2Params(params);
	}
	public String map2Params(Map<String,Object> map){
		StringBuffer sbuf = new StringBuffer();
		if(map == null){
			return null;
		}
		sbuf.append("?");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = String.valueOf(entry.getValue());
			sbuf.append(key+"="+value+"&");
		}
		sbuf.deleteCharAt(sbuf.length()-1);
		return sbuf.toString();
	}
	
}
