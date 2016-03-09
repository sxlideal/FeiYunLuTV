package com.qike.feiyunlu.tv.presentation.presenter.account;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qike.feiyunlu.tv.presentation.model.User;
import com.qike.feiyunlu.tv.presentation.presenter.IAccountPresenterCallBack;

import java.util.List;



/**
 * 
 *<p>账号管理器</p><br/>
 * @since 1.0.0
 * @author xky
 */
public class AccountManager {
	private static  AccountManager INSTANCE =null;
	private QiKeAccount mAccount;
	private User mUser;
	private Context mContext;
	
	private LoginType mLoginType = LoginType.NORMAL;
	
	
	private AccountManager(Context context){
		mAccount = new QiKeAccount(context);
		mContext = context;
	};
	public static synchronized AccountManager getInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new AccountManager(context);
		}
		return INSTANCE;
	}
	
	
//	/**
//	 *
//	 *<p>普通注册</p><br/>
//	 * @since 1.0.0
//	 * @author sll
//	 */
//	public void regist(Context context,String mobile,String pwd,String nick,String gender, String avatar_url, IAccountPresenterCallBack registCallBack){
//		mAccount.registe(mobile, pwd, nick, gender, avatar_url, registCallBack);
//	}

	/**
	 * 
	 *<p>手动登录</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void login(String username ,String pwd,IAccountPresenterCallBack mLoginBack){
		
		mAccount.login(username,pwd,mLoginBack);
		setmLoginType(LoginType.NORMAL);
	}


//	public void submitPic(File file, IAccountPresenterCallBack callBack){
//		mAccount.submitPic(file, callBack);
//	}
	

	/**
	 * 
	 *<p>注销</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void logout(){
		mUser = getUser();
		if(mUser != null){
			mUser.setIslast(0);
			AccountStoreManager.getInstance(mContext).updateUser(mUser);
			mUser = null;
		}
	}

	/**
	 * 
	 *<p>获得当前的User</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @return 如果没有则为null
	 */
	public User getUser(){
		if(mUser == null || TextUtils.isEmpty(mUser.getUser_id())){
			mUser = AccountStoreManager.getInstance(mContext).queryNewUser();
		}
		return mUser;
	}
	/**
	 * 
	 *<p>设置用户</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 */
	public void setUser(User user){
		mUser = user;
	}
	/**
	 * 
	 *<p>删除具体的用户信息</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param user
	 */
	public void deleteUser(User user){
		AccountStoreManager.getInstance(mContext).deleteUser(user);
	}
	/**
	 * 
	 *<p>修改用户信息存储</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void updateUser(User user){
		AccountStoreManager.getInstance(mContext).updateUser(user);
		mUser = AccountStoreManager.getInstance(mContext).queryNewUser();
	}
	/**
	 * 
	 *<p>获取所有的用户登录记录</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public List<User> getAllUser(){
		return AccountStoreManager.getInstance(mContext).queryUsers();
	}
	
	
	public static enum LoginType{
		WEIXIN, WEIBO, QQ, NORMAL
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if( mAccount != null){
			mAccount.onActivityResult(requestCode, resultCode, data);	
		}
	}
	public LoginType getmLoginType() {
		return mLoginType;
	}
	public void setmLoginType(LoginType mLoginType) {
		this.mLoginType = mLoginType;
	}
}
