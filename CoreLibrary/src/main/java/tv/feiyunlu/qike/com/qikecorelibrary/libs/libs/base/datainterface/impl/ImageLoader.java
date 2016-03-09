package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoader {

	public static void getBitmap(ImageView view, String url) {

		ImageGetDao dao = new ImageGetDao(view, url, -1, false);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, boolean circle) {

		ImageGetDao dao = new ImageGetDao(view, url, -1, circle);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, boolean circle,
			int[] size) {

		ImageGetDao dao = new ImageGetDao(view, url, -1, circle);
		dao.setSize(size);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, boolean circle,
			boolean dontCut) {

		ImageGetDao dao = new ImageGetDao(view, url, -1, circle);
		dao.setDontCut(dontCut);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, int defultId) {

		ImageGetDao dao = new ImageGetDao(view, url, defultId, false);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, int defultId,
			boolean circle) {

		ImageGetDao dao = new ImageGetDao(view, url, defultId, circle);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, int defultId,
			boolean circle, int[] size) {

		ImageGetDao dao = new ImageGetDao(view, url, defultId, circle);
		dao.setSize(size);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, int defultId,
			boolean circle, boolean dontCut) {

		ImageGetDao dao = new ImageGetDao(view, url, defultId, circle);
		dao.setDontCut(dontCut);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, Bitmap bitmap) {

		ImageGetDao dao = new ImageGetDao(view, url, bitmap, false);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, Bitmap bitmap,
			boolean circle) {

		ImageGetDao dao = new ImageGetDao(view, url, bitmap, circle);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, Bitmap bitmap,
			boolean circle, int[] size) {

		ImageGetDao dao = new ImageGetDao(view, url, bitmap, circle);
		dao.setSize(size);
		dao.asyncDoAPI();
	}

	public static void getBitmap(ImageView view, String url, Bitmap bitmap,
			boolean circle, boolean dontCut) {

		ImageGetDao dao = new ImageGetDao(view, url, bitmap, circle);
		dao.setDontCut(dontCut);
		dao.asyncDoAPI();
	}
}
