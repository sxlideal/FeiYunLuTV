package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.image;

import java.io.FileInputStream;
import java.io.IOException;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.HttpStack;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.Response;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.ByteRequest;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http.impl.HttpClientStack;


public class ImageDownloader {

	public static Response<byte[]> getImage(String url) {

		Response<byte[]> response = null;
		if (url.startsWith("http")) {

			HttpStack stack = HttpClientStack.getInstance();
			ByteRequest request = new ByteRequest(1, url, null, null);

			try {
				response = stack.performRequest(request);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return response;

		} else if (url.startsWith("file:")) {

			String newUrl = url.replace("file:", "");
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(newUrl);

				byte[] bytes = new byte[fis.available()];
				fis.read(bytes);
				response = new Response<byte[]>();
				response.setResult(bytes);

			} catch (Exception e) {
				e.printStackTrace();
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e1) {
						e.printStackTrace();
					}
				}
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else if (url.startsWith("assets:")) {
			//TODO

		} else if (url.startsWith("drawable")) {
			//TODO
		}

		return response;
	}

}
