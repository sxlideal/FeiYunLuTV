package com.qike.feiyunlu.tv.presentation.presenter.account;
/**
 * 
 *<p>注销结果的回掉</p><br/>
 * @since 1.0.0
 * @author xky
 */
public interface ILoginoutListener {
	/**
	 * 
	 *<p>注销成功</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void onLoginoutSuccess();
	/**
	 * 
	 *<p>注销失败</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void onLoginoutFailed();
}
