package com.qike.feiyunlu.tv.presentation.presenter;


public interface BaseCallbackPresenter {
	
	public boolean callbackResult(Object obj);
	public void onErrer(int code, String msg);
	

}
