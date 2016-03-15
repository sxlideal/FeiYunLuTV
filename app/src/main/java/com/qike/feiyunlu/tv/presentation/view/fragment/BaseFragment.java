package com.qike.feiyunlu.tv.presentation.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;




public abstract class BaseFragment extends Fragment implements IFragmentViewOperater {

	protected View mView;
	protected LayoutInflater mInflater;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = getView(inflater);
		mInflater = inflater;
		mView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return mView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		initData();
		setListener();
		loadData();
		super.onActivityCreated(savedInstanceState);
	}
	
	


	private View getView(LayoutInflater inflater) {
		int mLayoutId = getLayoutId();
		return inflater.inflate(mLayoutId, null);
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	/**
	 * 
	 *<p>获得布局view</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public View getContentView(){
		return mView;
	}
	
	public View findViewById(int id){
		return mView == null?null:mView.findViewById(id);
	}
	
	

	public void Toast(String msg){
		Toast.makeText(getContext(), msg, 0).show();
	}
	
	public Context getContext(){
		return getActivity();
	}
	
	/**
	 * 
	 *<p>在绑定Activiy时候，是否存在这样的Activity</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @return true:存在
	 */
	public boolean isActivityContains(){
		return getActivity() != null && isAdded();
	}
	
}
