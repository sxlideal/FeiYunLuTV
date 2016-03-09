package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.Image.size;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.CacheLoad;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheEntry;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.CacheLoader;


/**
 * 
 * <p>
 * CacheLoader的包装类
 * </p>
 * <br/>
 * <p>
 * 处理图片业务
 * </p>
 * 
 * @since 1.0.0
 * @author bigpie
 */
public class ViewSizeCacheLoader implements CacheLoad {

	private CacheLoader cacheLoader;
	private View view;

	public ViewSizeCacheLoader(CacheLoader cacheLoader, View view) {

		this.cacheLoader = cacheLoader;
		this.view = view;
	}

	public CacheEntry get(String key) {

		String fitKey = getFitKey(key);
		CacheEntry entry = cacheLoader.get(fitKey);

		if (entry != null && entry.bitmap != null) {
			return entry;
		}

		if (entry != null && entry.data != null && entry.data.length != 0) {

			entry.bitmap = BitmapFactory.decodeByteArray(entry.data, 0,
					entry.data.length);
			entry.data = null;
		}

		return entry;
	}

	public boolean memoryContains(String key) {
		String fitKey = getFitKey(key);
		return cacheLoader.getMemoryCache().keys().contains(fitKey);
	}

	public boolean diskContains(String key) {

		boolean bool = cacheLoader.getDiskCache().containsKey(key);
		return bool;
	}

	public void putMemory(String key, CacheEntry entry) {

		String fitKey = getFitKey(key);
		cacheLoader.getMemoryCache().put(fitKey, entry);
	}

	public void putDisk(String key, CacheEntry entry) {

		String fitKey = getFitKey(key);
		cacheLoader.getDiskCache().put(fitKey, entry);
	}

	public Bitmap getFitBitmap(Bitmap bitmap, boolean dontCut, int[] size) {

		
		if (dontCut) {
			return bitmap;
		}

		int width = 0;
		int height = 0;

		if (size == null || size.length == 0) {

			width = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			height = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			view.measure(width, height);
			height = view.getMeasuredHeight();
			width = view.getMeasuredWidth();

			if (height <= 1 || width <= 1) {

				LayoutParams layoutParams = view.getLayoutParams();
				width = layoutParams.width;
				height = layoutParams.height;
			}

			if (height <= 1 || width <= 1) {
				return bitmap;
			}
		} else {

			width = size[0];
			height = size[1];
		}
		
		
		if(bitmap.getWidth()<=width || bitmap.getHeight()<=height){
			return bitmap;
		}
		

		Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, width, height,
				true);

		if (finalBitmap != bitmap) {
			bitmap.recycle();
		}
		return finalBitmap;

	}

	public String getFitKey(String key) {

		LayoutParams layoutParams = view.getLayoutParams();
		float width = layoutParams.width;
		float height = layoutParams.height;

		String fitKey = key + width + "*" + height;

		return fitKey;

	}

	@Override
	public void put(String key, CacheEntry entry) {

		// String fitKey = getFitKey(key);
		//
		// if(entry.data!=null && entry.data.length!=0){
		// putDisk(fitKey, entry);
		// }
		//
		// if(entry.bitmap!=null){
		// entry.data = null;
		// putMemroy(fitKey, entry);
		// }

	}

	@Override
	public void setNoCache() {
		cacheLoader.setNoCache();
	}

}
