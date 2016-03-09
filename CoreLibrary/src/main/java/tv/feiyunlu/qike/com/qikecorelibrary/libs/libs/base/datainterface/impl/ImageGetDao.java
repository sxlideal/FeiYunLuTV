package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.IMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.Image.size.ViewSizeCacheLoader;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.Task;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.BitmapHttpTask;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.impl.HttpActionProxy;

/**
 * 
 * <p>
 * 图片请求数据dao
 * </p>
 * <br/>
 * <p>
 * 请求图片。内置图片缓存
 * </p>
 * 
 * @since 1.0.0
 * @author bigpie
 */
public class ImageGetDao extends AbstractGetDao<Bitmap> {

	private ImageView imageView;
	private int defaultId = -1;
	private CacheEntry entry;
	private Bitmap defaultBitmap;
	private boolean circle;
	private boolean dontCut;
	private int[] size;

	public ImageGetDao(ImageView imageView, String url, int defaultId,
			boolean circle) {
		super(url);

		this.imageView = imageView;
		this.defaultId = defaultId;
		this.circle = circle;
		this.imageView.setTag(this.imageView.getId(), url);

		if (defaultId != -1) {

			imageView.setImageResource(defaultId);
		} else {
			imageView.setImageBitmap(null);
		}

		if (mCacheLoader == null) {
			mCacheLoader = buildCacheLoader();
		}
	}

	public ImageGetDao(ImageView imageView, String url, Bitmap bitmap,
			boolean circle) {
		super(url);

		this.imageView = imageView;
		this.defaultBitmap = bitmap;
		this.circle = circle;
		this.imageView.setTag(this.imageView.getId(), url);

		if (defaultBitmap != null) {
			imageView.setImageBitmap(defaultBitmap);
		} else {
			imageView.setImageBitmap(null);
		}

		if (mCacheLoader == null) {
			mCacheLoader = buildCacheLoader();
		}
	}

	public boolean isDontCut() {
		return dontCut;
	}

	public void setDontCut(boolean dontCut) {
		this.dontCut = dontCut;
	}

	public int[] getSize() {
		return size;
	}

	public void setSize(int[] size) {
		this.size = size;
	}

	@Override
	public void asyncDoAPI() {

		// 直接从内存取，如果取到就set到 view
		
		boolean isNoPicture = Configuration.getConfiguration().isNoPicture();
		
		if(!isNoPicture){
			
			CacheEntry entry = mCacheLoader.get(URL);
			if (entry.bitmap != null) {
				String viewKey = (String) imageView.getTag(imageView.getId());
				if (viewKey.equals(URL)) {
					imageView.setImageBitmap(entry.bitmap);
				} else {
					imageView.setImageBitmap(null);
				}
			} else {
				entry = null;// 失效的entry置为空
				super.asyncDoAPI();
			}
		}else {
			
			if (defaultId != -1) {

				imageView.setImageResource(defaultId);
			} else if (defaultBitmap != null) {
				imageView.setImageBitmap(defaultBitmap);
			}
		}
		
		
	}

	@Override
	protected Task<HttpActionProxy, Long, Void> getTask() {
		return new BitmapHttpTask();
	}

	@Override
	protected void _onTaskEnd() {

		try {
			
			String viewKey = (String) imageView.getTag(imageView.getId());
			if (viewKey.equals(entry.etag)) {
				imageView.setImageBitmap(entry.bitmap);
			}
			super._onTaskEnd();
			
		} catch (Throwable e) {
			
			if (defaultId != -1) {

				imageView.setImageResource(defaultId);
			} else if (defaultBitmap != null) {
				imageView.setImageBitmap(defaultBitmap);
			}
		}

	}

	@Override
	public void _onTaskFailed(Task.TaskFailed failed) {
		super._onTaskFailed(failed);

		if (defaultId != -1) {

			imageView.setImageResource(defaultId);
		} else if (defaultBitmap != null) {
			imageView.setImageBitmap(defaultBitmap);
		}

	}

	@Override
	protected CacheLoad buildCacheLoader() {

		IMemoryCache<String, CacheEntry> memoryCache = Configuration
				.getConfiguration().getMemoryCache();
		IDiskCache diskCache = Configuration.getConfiguration().getDiskCache();
		CacheLoader cacheLoader = new CacheLoader(diskCache, memoryCache);
		ViewSizeCacheLoader mSizeCacheLoader = new ViewSizeCacheLoader(
				cacheLoader, imageView);

		return mSizeCacheLoader;
	}

	@Override
	public void onDoInBackgroundProcess(HttpActionProxy action,
			HashMap<String, String> headers) {
		
		entry = action.getCacheEntry();
		ViewSizeCacheLoader loader = (ViewSizeCacheLoader) action.getLoader();
		Bitmap finalBitmap = null;

		if (entry.bitmap != null) {// 此处为内存中

			finalBitmap = entry.bitmap;
		} else if (loader.diskContains(action.getUrlCacheKey())) {// 此处为磁盘中读到返回，直接转换bitmap对象

			try {
				finalBitmap = BitmapFactory.decodeByteArray(entry.data, 0,
						entry.data.length);
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} else {// 此处为联网返回，需要裁减数据
			// 联网返回原始大小的bitmap根据view大小裁减之后的bitmap
			try {
				finalBitmap = loader.getFitBitmap(BitmapFactory.decodeByteArray(
						entry.data, 0, entry.data.length), dontCut, size);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
			if (circle) {
				finalBitmap = toRoundBitmap(finalBitmap);
			}
			// 获取裁剪之后的bitmap字节，存入磁盘
			ByteArrayOutputStream bos = null;

			
			try {
				bos = new ByteArrayOutputStream();
				finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				byte[] bytes = bos.toByteArray();
				
				entry.data = bytes;
				loader.putDisk(action.getUrlCacheKey(), entry);
				entry.bitmap = finalBitmap;
				
			} catch (Throwable e) {
				e.printStackTrace();
			}finally{
				
				entry.data = null;
				if(bos!=null){
					try {
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// 存入内存
		loader.putMemory(action.getUrl(), entry);
		
		action = null;

	}

	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

}
