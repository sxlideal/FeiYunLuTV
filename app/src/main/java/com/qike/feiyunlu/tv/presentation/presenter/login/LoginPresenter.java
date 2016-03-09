package com.qike.feiyunlu.tv.presentation.presenter.login;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.qike.feiyunlu.tv.presentation.model.IAccountBizCallBack;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.model.login.LoginBiz;
import com.qike.feiyunlu.tv.presentation.presenter.IAccountPresenterCallBack;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountStoreManager;


/**
 * 
 *<p>登陆presenter</p><br/>
 *<p>处理登陆的逻辑 包括登陆后对数据的存储处理</p>
 * @since 1.0.0
 * @author sll
 */
public class LoginPresenter implements IAccountBizCallBack {
	private LoginBiz mLoginBiz;
	private IAccountPresenterCallBack mLoginPresenterCallBack;
	private Context mContext;

//	private UmengSocial mUmengSocial;
//
//	private UmengUserInfo mUmengUserInfo;

	private AccountManager.LoginType mLoginType;
	
	public LoginPresenter(Context context) {
		mContext = context;

//		mUmengSocial = new UmengSocial();
		mLoginBiz = new LoginBiz();
		mLoginBiz.registLoginBizCallBack(this);
	}

//	public void login(AccountManager.LoginType loginType, Context context) {
//
//		mLoginType = loginType;
//
//		switch (loginType) {
//		case WEIXIN:
//			mUmengSocial.doOauthLogin(SHARE_MEDIA.WEIXIN, mOnLoginListener, context);
//			break;
//		case QQ:
//			mUmengSocial.doOauthLogin(SHARE_MEDIA.QQ, mOnLoginListener, context);
//			break;
//		case WEIBO:
//			mUmengSocial.doOauthLogin(SHARE_MEDIA.SINA, mOnLoginListener, context);
//			break;
//
//		default:
//			break;
//		}
//	}

	/**
	 * 登陆
	 * @author sll
	 * @param username
	 * @param pwd
	 */
	public void login(String username, String pwd, final IAccountPresenterCallBack callBack) {

		mLoginBiz.login(username, pwd, new IAccountBizCallBack() {

			@Override
			public void errerResult(int code, String msg) {
				if (callBack != null) {
					callBack.onErrer(code, msg);
					Log.e("TAG", "cuowu");
				}
			}

			@Override
			public void dataResult(Object obj) {
				if (obj != null && obj instanceof User) {
					User user = (User) obj;
					if (!TextUtils.isEmpty(user.getUser_id())) {
						
						
						AccountStoreManager.getInstance(mContext).saveUser(user);
						Log.d("login_user_storage", AccountManager.getInstance(mContext).getUser().toString());
//						Log.e("TAG", "111=="+AccountManager.getInstance(mContext).getUser().toString());
					}
				}
				if (callBack != null) {
					callBack.callbackResult(obj);
				}
				Log.e("TAG", "cuowu222");
			}

			@Override
			public void callBackStats(int status) {
				Log.e("TAG", "cuowu333");
				if (callBack != null) {
					callBack.callBackStats(status);
				}
			}
		});
	}

//	private void loginThird( AccountManager.LoginType mLoginType) {
//		if (mUmengUserInfo != null) {
//
//
//				mLoginBiz.loginThird(mUmengUserInfo.getToken(), mUmengUserInfo.getOpenId(), mUmengUserInfo.getSource());
//
//
//		}
//
//	}

	public void registLoginPresenterCallBack(IAccountPresenterCallBack callBack) {
		mLoginPresenterCallBack = callBack;
	}

	@Override
	public void dataResult(Object obj) {
		if (obj != null && obj instanceof User) {
			User user = (User) obj;
			if (!TextUtils.isEmpty(user.getUser_id())) {
				AccountStoreManager.getInstance(mContext).saveUser(user);
				Log.d("login_user_storage", AccountManager.getInstance(mContext).getUser().toString());
			}

		}
		if (mLoginPresenterCallBack != null) {
			mLoginPresenterCallBack.callbackResult(obj);
		}
	}

	@Override
	public void callBackStats(int status) {
		// TODO Auto-generated method stub
		if (mLoginPresenterCallBack != null) {
			mLoginPresenterCallBack.callBackStats(status);
		}
	}

	@Override
	public void errerResult(int code, String msg) {
		if (mLoginPresenterCallBack != null) {
			mLoginPresenterCallBack.onErrer(code, msg);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (mUmengSocial != null) {
//			mUmengSocial.onActivityResult(requestCode, resultCode, data);
//		}

	}

//	/**
//	 * 第三方授权登陆的回调
//	 */
//	private OnLoginListener mOnLoginListener = new OnLoginListener() {
//
//		@Override
//		public void onLoginSucceed(UmengUserInfo userInfo) {
//			// TODO Auto-generated method stub
//			mUmengUserInfo = userInfo;
//
//			loginThird( mLoginType );
//		}
//
//		@Override
//		public void onLoginFailed() {
//			Toast.makeText(mContext, "授权登陆失败", 0).show();
//		}
//
//		@Override
//		public void onLoginCancle() {
//			Toast.makeText(mContext, "授权登陆取消", 0).show();
//		}
//	};

}
