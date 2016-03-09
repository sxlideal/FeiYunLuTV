package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


/**
 * Created by zhulingjun on 14-3-19.
 */
public class HttpUtil {
	/**
	 * 转换Map<String, Object> to HttpEntity
	 * 
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static HttpEntity covertMap2HttpEntity(Map<String, Object> params,
			boolean multipart) throws UnsupportedEncodingException {
		List<NameValuePair> pairs = convertNameValuePair(params);
		HttpEntity entity = null;
		if (multipart) {
			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			entity = multipartEntity;
			for (int index = 0; index < pairs.size(); index++) {
				multipartEntity.addPart(pairs.get(index).getName(),
						new StringBody(pairs.get(index).getValue(), "text",
								Charset.forName("UTF-8")));
			}
		} else {
			entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
		}

		return entity;
	}

	private static List<NameValuePair> convertNameValuePair(
			Map<String, Object> params) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null) {
			for (Map.Entry<String, Object> e : params.entrySet()) {
				Object value = e.getValue();
				if (value instanceof PostContent || value instanceof File) {
					continue;
				}
				pairs.add(new BasicNameValuePair(e.getKey(), String
						.valueOf(value)));
			}
		}
		return pairs;
	}

	public static String convertParams(Map<String, Object> params)
			throws Exception {
		StringBuilder sb = new StringBuilder();

		if (params != null && params.size() > 0) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String value = String.valueOf(entry.getValue());
				value = URLEncoder.encode(value == null ? "null" : value,
						"UTF-8");
				sb.append(entry.getKey()).append("=").append(value).append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static HttpEntity convertUploadEntity(Map<String, Object> params,
			Request.UploadFileListener fileListener) throws Exception {

		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		List<NameValuePair> namepairs = convertNameValuePair(params);
		for (int index = 0; namepairs != null && index < namepairs.size(); index++) {
			reqEntity.addPart(namepairs.get(index).getName(),
					new StringBody(namepairs.get(index).getValue(),
							"text/plain", Charset.forName("UTF-8")));
		}
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value instanceof PostContent || value instanceof File) {

				PostContent postContent = null;
				File file = null;
				String contentType = null;
				String fileName = null;
				if (value instanceof PostContent) {
					postContent = (PostContent) value;
					contentType = postContent.getContentType();
					fileName = postContent.getFilename();
					file = new File(postContent.getFilePath());
				} else {
					file = (File) value;
				}

				if (TextUtils.isEmpty(contentType)) {
					
					String filename = file.getName();
					if( filename.endsWith(".png")){
						contentType = "image/png";
					}else{
						contentType = "application/octet-stream";
					}
					
				}
				if (TextUtils.isEmpty(fileName)) {
					fileName = file.getName();
				}

				FileBodyKownSizeBody fileBody = null;

				if (file.exists() && file.isFile()) {
					if (contentType == null && fileName == null) {
						fileBody = new FileBodyKownSizeBody(file);
					} else if (contentType != null && fileName == null) {
						fileBody = new FileBodyKownSizeBody(file, contentType);
					} else {
						fileBody = new FileBodyKownSizeBody(file, contentType,
								fileName);
					}
					fileBody.setUploadFileListener(fileListener);
					
					reqEntity.addPart(key, fileBody);
					
				} else {
					continue;
				}
			}

		}
		return reqEntity;

	}

	public static boolean isUpload(Map<String, Object> params) {

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof PostContent || value instanceof File) {
				return true;
			}

		}

		return false;
	}

	public static synchronized DefaultHttpClient getClient(int retryCount,int timeout) {

	

		// Shamelessly cribbed from AndroidHttpClient
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// HttpProtocolParams.setUseExpectContinue(params, true);
		// Turn off stale checking. Our connections break all the time
		// anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 30 seconds. Tweak to
		// taste.
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, true);

		ConnManagerParams.setTimeout(params, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(50));
		ConnManagerParams.setMaxTotalConnections(params, 200);

		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(
				params, supportedSchemes);
		DefaultHttpClient client = new DefaultHttpClient(ccm, params);
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
				retryCount, true));
		client.addRequestInterceptor(new GzipHttpRequestInterceptor());
		client.addResponseInterceptor(new GzipHttpResponseInterceptor());

		return client;
	}

	private final static class GzipHttpRequestInterceptor implements
			HttpRequestInterceptor {

		public void process(final HttpRequest request, final HttpContext context)
				throws HttpException, IOException {
			request.setHeader("Accept-Encoding", "gzip");
		}
	}

	private final static class GzipHttpResponseInterceptor implements
			HttpResponseInterceptor {

		public void process(final HttpResponse response,
				final HttpContext context) throws HttpException, IOException {
			HttpEntity entity = response.getEntity();
			Header header = entity.getContentEncoding();
			if (header != null) {
				HeaderElement[] codecs = header.getElements();
				for (int i = 0; i < codecs.length; i++) {
					if (codecs[i].getName().equalsIgnoreCase("gzip")) {
						response.setEntity(new GzipDecompressingEntity(response
								.getEntity()));
						return;
					}
				}
			}
		}
	}

	private final static class GzipDecompressingEntity extends
			HttpEntityWrapper {

		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		public InputStream getContent() throws IOException,
				IllegalStateException {
			InputStream wrappedin = wrappedEntity.getContent();
			return new GZIPInputStream(wrappedin);
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}

	/**
	 * 转化Inputstream 2 byte[]
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] covertInputstream2Bytes(InputStream is)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int read = 0;
			byte[] buffer = new byte[1024];
			while ((read = is.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baos.toByteArray();
	}
}
