package com.qike.feiyunlu.tv.library.util.ImageLoader;


import java.io.File;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class SyncImageLoader {

	private Object lock = new Object();
    // 是否允许加载图片
    private boolean mAllowLoad = true;
    // 开始加载图片的位置
    private int mStartLoadLimit = 0;
    // 停止加载图片的位置
    private int mStopLoadLimit = 0;
//    ArrayList<String> list=new ArrayList<String>();
    Bitmap mBitmap;
    Handler handler = new Handler();

    public interface OnImageLoadListener {
        public void OnImageLoad(Integer id, Bitmap bitmap);
    }

    public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
        mStartLoadLimit = startLoadLimit;
        mStopLoadLimit = stopLoadLimit;
    }

    public void lock() {
        mAllowLoad = false;
    }

    public void unlock() {
        mAllowLoad = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void loadImage(Integer t, String imageUrl,
            OnImageLoadListener listener) {
        final OnImageLoadListener mListener = listener;
        final String mImageUrl = imageUrl;
        final Integer mt = t;
        new Thread(new Runnable() {
            public void run() {
                if (!mAllowLoad) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (mStopLoadLimit == 0 && mStartLoadLimit == 0) {
                    loadImage(mImageUrl, mt, mListener);
                }
                if (mAllowLoad && mt <= mStopLoadLimit && mt >= mStartLoadLimit) {
                    System.out.println("loadImage");
                    loadImage(mImageUrl, mt, mListener);
                }
            }
        }).start();
    }

    private void loadImage(final String mImagePath, final Integer mt,
            final OnImageLoadListener mListener) {
        try {
//            if(list.contains(mImagePath)){
//                mBitmap = MyBitmap.readBitMap(mImagePath + "1.jpg",1);
//                mBitmap = MyBitmap.adjustPhotoRotation1(mBitmap, 90);
//            }else{
        	if( (new File(mImagePath)).length() > 80*1000 ){
        		mBitmap = MyBitmap.readBitMap(mImagePath, 4);
                mBitmap = MyBitmap.adjustPhotoRotation1(mBitmap, 90);
                MyBitmap.saveFile(mBitmap, mImagePath );
                Log.e("SyncImageLoader", "压缩"+mImagePath);
        	}else{
        		mBitmap = MyBitmap.readBitMap(mImagePath,1);
//        		mBitmap = MyBitmap.adjustPhotoRotation1(mBitmap, 90);
        		Log.e("SyncImageLoader", "不压缩"+mImagePath);
        	}
                
//                list.add(mImagePath);
//            }
            handler.post(new Runnable() {
                public void run() {
                    if (mAllowLoad) {
                        mListener.OnImageLoad(mt, mBitmap);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
}
