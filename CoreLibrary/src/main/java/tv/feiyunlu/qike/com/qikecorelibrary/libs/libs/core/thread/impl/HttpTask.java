/*
 * 文件名：HttpTask.java
 * 描述：不带有进度的HTTP任务
 * 版本：v1.0.0
 * 日期：2014-3-15
 * 版权：Copyright ? 2014 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import java.io.IOException;
import java.security.MessageDigest;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpError;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpStack;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Request;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.ByteRequest;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.HttpClientStack;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.IResultProcessor;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;


/**
 *<p>Http任务</p><br/>
 *<p>此任务不支持任务进度</p>
 * @since 1.0.0
 * @author sunxianhao
 */
public class HttpTask extends Task<HttpActionProxy, Long, Void> {
    private static final int    CORE_POOL_MAX  = 20;
    private static final String CORE_POOL_NAME = HttpTask.class.getName();

	public HttpTask() {
		super(CORE_POOL_MAX, CORE_POOL_NAME);
	}

    @Override
    protected Void doInBackground(HttpActionProxy... params) throws ConnectTimeoutException, Exception {
	
	Response<byte[]> respomse = null;
	Void result = null;

	    HttpActionProxy proxy = params[0];
	    
	    CacheLoad cacheLoader = proxy.getLoader();
	    IResultProcessor processor = proxy.getProcessor();
		CacheEntry cacheEntry = cacheLoader.get(proxy.getUrlCacheKey());
		

		if (cacheEntry.ttl != 0 && !cacheEntry.isExpired()) {

			proxy.setCacheEntry(cacheEntry);//如果请求的是bitmap， 内存中取到的话，返回entry.bitmap;如果磁盘取到的话，返回entry.data
			processor.onDoInBackgroundProcess(proxy,null);

		} else {

			/*******************************       缓存无效，请求网络部分      *********************************/
			HttpStack stack = HttpClientStack.getInstance();
			ByteRequest request = new ByteRequest(proxy.getRequestType(),
					proxy.getUrl(), proxy.getParams(), null);

			request.setUploadFileListener(mUploadFileListener);

			try {
				respomse = stack.performRequest(request);
			} catch (IOException e) {
				e.printStackTrace();
				if (e.getClass() == ConnectTimeoutException.class
						|| e.getClass() == ConnectionPoolTimeoutException.class) {
					throw new ConnectTimeoutException(e.getMessage());
				}
			} catch (HttpError e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}

			cacheEntry.ttl = proxy.getCacheTTL() + System.currentTimeMillis();//设置缓存失效时间
			byte[] bytes = respomse.getResult();//缓存字节
			cacheEntry.data = (bytes);

			proxy.setCacheEntry(cacheEntry);//缓存包装对象 给数据bean对象，供传递解析用
			cacheLoader.put(proxy.getUrlCacheKey(), cacheEntry);//保存缓存实体，如果为bitmap则为空实现，不保存完整缓存，只在解析后保存裁剪大小之后的缓存
			processor.onDoInBackgroundProcess(proxy,respomse.getHeaders());//对字节的解析，如果为bitmap则在此处保存缓存
		}

		return result;
	}

	/**
	 *<p>是否有可用数据链接</p>
	 * @since 1.0.0
	 * @author sunxianhao
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 *<p>MD5生成</p><br/>
	 *<p>用于缓存数据命名</p>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param plainText
	 * @return
	 */
	public String toMD5(String plainText) {
		StringBuffer buf = null;
		try {
			//生成实现指定摘要算法的 MessageDigest 对象。
			MessageDigest md = MessageDigest.getInstance("MD5");
			//使用指定的字节数组更新摘要。
			md.update(plainText.getBytes());
			//通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();
			//生成具体的md5密码到buf数组
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位的加密
			System.out.println("32位: " + buf.toString());
			// 16位的加密，其实就是32位加密后的截取
			System.out.println("16位: " + buf.toString().substring(8, 24));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	private Request.UploadFileListener mUploadFileListener = new Request.UploadFileListener() {

		@Override
		public void onProgress(long progress, long length) {
			HttpTask.this.onProgress(CORE_POOL_NAME, progress, length);
		}
	};
}
