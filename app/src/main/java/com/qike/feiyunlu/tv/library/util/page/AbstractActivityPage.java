package com.qike.feiyunlu.tv.library.util.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.qike.feiyunlu.tv.library.util.viewparse.AnnotationViewParser;
import com.qike.feiyunlu.tv.library.util.viewparse.ViewInjectable;


abstract public class AbstractActivityPage implements ActivityPage, ViewInjectable {
	protected Activity activity;

	public Intent getIntent() {
		return activity.getIntent();
	}

	public AbstractActivityPage(Activity activity) {
		this.activity = activity;
	}
	
	

	@Override
	public void onBackPressed() {
		
	}

	@Override
	public void onCreate(Bundle data) {

	}

	public LayoutInflater getLayoutInflater() {
		return activity.getLayoutInflater();
	}

	public void setContentView(View view) {
		activity.setContentView(view);
		new AnnotationViewParser().parse(this);
	}

	public void setContentView(int viewId) {
		activity.setContentView(viewId);
		new AnnotationViewParser().parse(this);
	}

	public View findViewById(int id) {
		return activity.findViewById(id);
	}

	public void setTheme(int theme) {
		activity.setTheme(theme);
	}


	@Override
	public void onDestroy() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return false;
	}

	@Override
	public Resources getResources() {
		return activity.getResources();
	}

	@Override
	public Context getContext() {
		return activity;
	}

	public void finish() {
		activity.finish();
		if (isRecord()) {
//			PageUtils.removePage(this);
		}
	}

	public void setResult(int resultcode) {
		activity.setResult(resultcode);
	}

	@SuppressWarnings("unchecked")
	public <T> T getViewById(int id) {
		return (T) this.findViewById(id);
	}

	@SuppressWarnings("unchecked")
	public <T> T getViewById(View mView, int id) {
		return (T) mView.findViewById(id);
	}

	@Override
	public View findView(int id) {
		return activity.findViewById(id);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

	}

	@Override
	public boolean isRecord() {
		return false;
	}

}
