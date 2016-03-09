package com.qike.feiyunlu.tv.presentation.presenter.account;
/**
 * 
 *<p>账号操作</p><br/>
 *<p>最基本的操作 登陆 注册 注销</p>
 * @since 1.0.0
 * @author xky
 */
public interface IAccount {
	/**
	 * 
	 *<p>登录</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param objs
	 */
	public void login(Object... objs);
//	/**
//	 *
//	 *<p>注册</p><br/>
//	 * @since 1.0.0
//	 * @author xky
//	 * @param objs
//	 */
//	public void register(Object... objs);
	/**
	 * 
	 *<p>注销</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param objs
	 */
	public void logout(Object... objs);
}
