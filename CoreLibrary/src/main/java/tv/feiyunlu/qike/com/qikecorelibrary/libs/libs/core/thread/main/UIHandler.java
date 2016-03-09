package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread.main;

import android.os.Handler;

public class UIHandler {
	private Handler mHandler;

	private static UIHandler INSTANCE = new UIHandler();

	private UIHandler() {

	}

	public static UIHandler getHandler() {
		return INSTANCE;
	}

	public void init() {
		mHandler = new Handler();
	}

	public void exit() {
		if (mHandler != null) {
			mHandler.getLooper().quit();
		}
	}
	
	
	

}
