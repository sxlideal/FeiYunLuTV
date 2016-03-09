package com.qike.feiyunlu.tv.presentation.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.qike.feiyunlu.tv.library.util.UiUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 所有Activity基类<br/>
 * 封装了Activity经常使用的方法
 * @since 5.0.0
 * @author suenxianhao
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

	
	/**
	 * 程序是否处于前台，默认值false
	 */
	private boolean isActive = true;

	/**
	 * 监听程序从后台切换回来的接口
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	public interface RestoreBackGroundListener {
		void onBack();
	}

	/**
	 * 监听程序从后台切换回来的接口集合
	 */
	public ArrayList<RestoreBackGroundListener> mRestoreBgListeners = new ArrayList<RestoreBackGroundListener>();

	/**
	 * 添加监听程序从后台切换回来的接口
	 */
	public void addRestoreBackGroundListener(RestoreBackGroundListener listener) {
		if (!mRestoreBgListeners.contains(listener)) {
			mRestoreBgListeners.add(listener);
		}
	}

	/**
	 * 删除监听程序从后台切换回来的接口
	 */
	public void removeRestoreBackGroundListener(RestoreBackGroundListener listener) {
		if (mRestoreBgListeners.contains(listener)) {
			mRestoreBgListeners.remove(listener);
		}
		
		
	}

	/**
	 * 每个继承此类的activity都会将实例添加到activity集合中进行统一管理
	  * @since 5.0.0
	  * @author suenxianhao	
	  */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		UiUtils.createActivity(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	public Context getContext(){
		return this;
	}


	/**
	 * 在銷毀的同時，刪除其所在activity集合中的實例
	 * @since 5.0.0
	 * @author suenxianhao
	 */
	@Override
	protected void onDestroy() {

		UiUtils.destroyActivity(this);
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//TODO	用户行为统计（从后台返回）
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*		int upIconId =  getResources().getSystem().getIdentifier("up", "id", "android");
				ImageView upIcon = (ImageView) findViewById(upIconId);
				if(upIcon!=null){
					upIcon.setImageResource(R.drawable.abc_ic_ab_back_holo_dark);
				}
				
				*/
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onClick(View v) {
	}

	private long mExitTime;



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}
}
