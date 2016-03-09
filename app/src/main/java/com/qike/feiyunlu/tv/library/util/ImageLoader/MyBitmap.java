package com.qike.feiyunlu.tv.library.util.ImageLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class MyBitmap {

	public static Bitmap readBitMap(String fileName, int n) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inSampleSize = n; // width，hight设为原来的十分一
		opt.inPurgeable = true;
		opt.inInputShareable = true; // 获取资源图片
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return BitmapFactory.decodeStream(fis, null, opt);
	}

	 public static Bitmap compressImageFromFile(String srcPath) {  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        newOpts.inJustDecodeBounds = true;//只读边,不读内容  
	        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
	  
	        newOpts.inJustDecodeBounds = false;  
	        int w = newOpts.outWidth;  
	        int h = newOpts.outHeight;  
	        float hh = 800f;//  
	        float ww = 480f;//  
	        int be = 1;  
	        if (w > h && w > ww) {  
	            be = (int) (newOpts.outWidth / ww);  
	        } else if (w < h && h > hh) {  
	            be = (int) (newOpts.outHeight / hh);  
	        }  
	        if (be <= 0)  
	            be = 1;  
	        newOpts.inSampleSize = be;//设置采样率  
	          
	        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
	        newOpts.inPurgeable = true;// 同时设置才会有效  
	        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
	          
	        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
//	      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩  
	                                    //其实是无效的,大家尽管尝试  
	        return bitmap;  
	    }  
	
	
	public static Bitmap adjustPhotoRotation1(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
		}
		return null;
	}
	
	
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		 opt.inSampleSize = 10; //width，hight设为原来的十分一
		opt.inPurgeable = true;
		opt.inInputShareable = true; // 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return adjustPhotoRotation(BitmapFactory.decodeStream(is, null, opt),90);
	}

	public static void saveFile(Bitmap bm, String fileName) throws IOException {
		File myCaptureFile = new File(fileName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
	{

	        Matrix m = new Matrix();
	        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
	        float targetX, targetY;
	        if (orientationDegree == 90) {
	        targetX = bm.getHeight();
	        targetY = 0;
	        } else {
	        targetX = bm.getHeight();
	        targetY = bm.getWidth();
	  }

	    final float[] values = new float[9];
	    m.getValues(values);

	    float x1 = values[Matrix.MTRANS_X];
	    float y1 = values[Matrix.MTRANS_Y];

	    m.postTranslate(targetX - x1, targetY - y1);

	    Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Config.ARGB_8888);
	Paint paint = new Paint();
	    Canvas canvas = new Canvas(bm1);
	    canvas.drawBitmap(bm, m, paint);

	    return bm1;
	  }
	
}
