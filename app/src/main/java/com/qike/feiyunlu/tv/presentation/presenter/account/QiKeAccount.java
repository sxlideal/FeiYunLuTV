package com.qike.feiyunlu.tv.presentation.presenter.account;

import android.content.Context;
import android.content.Intent;

import com.qike.feiyunlu.tv.presentation.presenter.IAccountPresenterCallBack;
import com.qike.feiyunlu.tv.presentation.presenter.login.LoginPresenter;


/**
 * 
 *<p>7k7k账号操作</p><br/>
 * @since 1.0.0
 * @author xky
 */
public class QiKeAccount implements IAccount{
	private LoginPresenter mLoginPresenter;
//	private RegistPresenter mRegisterPresenter;
	public QiKeAccount(Context context) {
		mLoginPresenter = new LoginPresenter(context);
//		mRegisterPresenter = new RegistPresenter(context);
	}
	
	
//	public void submitPic( File file, IAccountPresenterCallBack callBack){
//
//		mRegisterPresenter.submitPic(file, callBack);
//
//	}
	
	
	/**
	 * 
	 *<p>账号登陆</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author sll
	 */
	@Override
	public void login(Object... objs) {
		String username = (String) objs[0];
		String pwd = (String) objs[1];
		IAccountPresenterCallBack mLoginBack = (IAccountPresenterCallBack) objs[2];
		mLoginPresenter.login(username,pwd,mLoginBack);
	}
	
	
//	public void login( LoginType loginType,IAccountPresenterCallBack mLoginBack ,Context context){
//		mLoginPresenter.login(loginType,context);
//		mLoginPresenter.registLoginPresenterCallBack(mLoginBack );
//	}
	
	
//	public void checkPhone( String phonenum ,IAccountPresenterCallBack callBack){
//
//		mRegisterPresenter.checkPhoneNum(phonenum,callBack);
//
//	}
	
	
	/**
	 * 
	 *<p>取消登陆</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void cancelLogin(){
	//	mLoginPresenter.cancelLogin();
	}
	

//	/**
//	 * 注册
//	 * @author sll
//	 * @param objs
//	 */
//	@Override
//	public void register(Object... objs) {
//
//		String userid  = (String) objs[0];
//		String pwd  = (String) objs[1];
//		String verifyCode = (String) objs[2];
//
//		IAccountPresenterCallBack registCallBack = (IAccountPresenterCallBack) objs[3];
////		mRegisterPresenter.registRegistPresenterCallBack(registCallBack);
////		mRegisterPresenter.regist(userid,verifyCode, pwd);
//	}
	
//	public void registe(String mobile,String pwd,String nick,String gender, String avatar_url, IAccountPresenterCallBack registCallBack){
//		mRegisterPresenter.regist(mobile,pwd,nick,gender,avatar_url, registCallBack);
//	}
//

	@Override
	public void logout(Object... objs) {
//		ILoginoutListener mLoginoutListener = (ILoginoutListener) objs[0];//注销的回掉
	}
	

	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if( mLoginPresenter != null){
			mLoginPresenter.onActivityResult(requestCode, resultCode, data);	
		}
		
	}

}
