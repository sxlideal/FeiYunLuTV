package com.qike.feiyunlu.tv.library.util.page;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

public interface ActivityPage {
	public void onCreate(Bundle data);

	public void onDestroy();

	public void onPause();

	public void onResume();

	public void onStop();

	public Resources getResources();

	public Context getContext();
	
	
	public void setContentView(View view);
	public void setContentView(int viewId);
	public View findViewById(int id);
	public void setTheme(int theme);
	public LayoutInflater getLayoutInflater();
	
	
	public Intent getIntent();
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent);
	public void finish();
	
	public boolean isRecord();
	
	public boolean onKeyDown(int keyCode, KeyEvent event);
	
	
	public void onBackPressed();
	
	
	
}
