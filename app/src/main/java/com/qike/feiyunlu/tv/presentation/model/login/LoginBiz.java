package com.qike.feiyunlu.tv.presentation.model.login;


import com.qike.feiyunlu.tv.module.network.BazaarGetDao;
import com.qike.feiyunlu.tv.module.network.Paths;
import com.qike.feiyunlu.tv.presentation.model.IAccountBizCallBack;
import com.qike.feiyunlu.tv.presentation.model.User;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.BaseLoadListener;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.IDao;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support.Result;

/**
 * 
 *<p>登录biz</p><br/>
 * @since 1.0.0
 * @author sll
 */

public class LoginBiz {
	private IAccountBizCallBack mLoginBizCallBack;
	private BazaarGetDao<User> mDao;
	private BazaarGetDao<User> mThirdDao;

	public LoginBiz() {

	}

	/**
	 * 登陆
	 * @author sll
	 * @param username
	 * @param pwd
	 */
	public void login(String username, String pwd, final IAccountBizCallBack loginBizCallBack) {
		
		mDao = new BazaarGetDao<User>(Paths.BASEPATH + Paths.LOGIN_MOBILE, User.class, BazaarGetDao.ARRAY_DATA_CHUNK);
		mDao.setNoCache();
		mDao.registerListener(new BaseLoadListener() {

			@Override
			public void onComplete(IDao.ResultType resultType) {
				super.onComplete(resultType);
				if (loginBizCallBack != null) {
					if (mDao.getStatus() == 200) {
						loginBizCallBack.dataResult(mDao.getData());
						loginBizCallBack.callBackStats(mDao.getStatus());
					} else {
						loginBizCallBack.errerResult(mDao.getErrorData().getCode(), mDao.getErrorData().getData());
					}
				}
			}

			@Override
			public void onError(Result result) {
				super.onError(result);
				if (loginBizCallBack != null) {
					loginBizCallBack.errerResult(result.getCode(), result.getErrmsg());
				}
			}

		});

		mDao.putParams(Paths.PARAM_ACCOUNT, username);
		mDao.putParams(Paths.PARAM_PWD, pwd);
		mDao.asyncDoAPI();
	}

	public void loginThird(String token, String openId, String source) {

		mThirdDao = new BazaarGetDao<User>(Paths.BASEPATH + Paths.THIRD_LOGIN, User.class, BazaarGetDao.ARRAY_DATA_CHUNK);

		mThirdDao.registerListener(new BaseLoadListener() {
			@Override
			public void onComplete(IDao.ResultType resultType) {
				super.onComplete(resultType);
				if (mLoginBizCallBack != null) {
					if (mThirdDao.getStatus() == 200) {
						mLoginBizCallBack.dataResult(mThirdDao.getData());
						mLoginBizCallBack.callBackStats(mThirdDao.getStatus());
					} else {
						mLoginBizCallBack.errerResult(mThirdDao.getErrorData().getCode(), mThirdDao.getErrorData().getData());
					}
				}
			}

			@Override
			public void onError(Result result) {
				super.onError(result);
				if (mLoginBizCallBack != null) {
					mLoginBizCallBack.errerResult(result.getCode(), result.getErrmsg());
				}
			}
		});
		if( source.equals("qq")){
			mThirdDao.putParams("token", "A1CCB4C343467B8E6ED3F4AE16435114");
			mThirdDao.putParams("open_id", "A0002DEE127A86F8AE18D485050C8F25");
		}else{
			mThirdDao.putParams("token", token);
			mThirdDao.putParams("open_id", openId);
		}
		
		mThirdDao.putParams("source", source);
		mThirdDao.asyncDoAPI();

	}

	/**
	 * 注册一个登陆的回调
	 * @author sll
	 * @param callBack
	 */
	public void registLoginBizCallBack(IAccountBizCallBack callBack) {
		mLoginBizCallBack = callBack;
	}

}
