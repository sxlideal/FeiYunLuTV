package com.qike.feiyunlu.tv.presentation.presenter.account;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.qike.feiyunlu.tv.library.util.Device;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 *<p>账号管理逻辑层</p><br/>
 * @since 1.0.0
 * @author xky
 */
public class AccountPresenter {
	private Context mContext;
	private CountDownTimer mTimer;
	private TimerCallBack mTimerCallBack;
	private TimerCallBack mAutoLoginTimerCallBack;
//	private SmsFactory mFactory;
//	private IVertifyCodeMsgListener mVertifyCodeListener;
	private CountDownTimer mLoginTimer;
	public AccountPresenter(Context context) {
		mContext = context;
	//	mFactory = new SmsFactory(context);
		initTimer();
		initAutoLoginTimer();
		setCallBack();
	}
	
	
	private void setCallBack() {
//		mFactory.setOnSmsCallBack(new ISmsBodyCallBack() {
//			
//			@Override
//			public void onSmsMessage(String phoneNum, String msg) {
//				if(msg.contains("7K7K.COM")){
//					String substring = msg.substring("验证码：".length(), "验证码：".length()+4);
//					setOnCode_(substring);
//				}
//			}
//		});
	}


	protected String getVertifyCode(String msg) {
		if(!TextUtils.isEmpty(msg)){
			
		}
		return msg;
	}


	private void initTimer() {
		mTimer = new CountDownTimer(60*1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				if(mTimerCallBack != null){
					mTimerCallBack.onProgress(millisUntilFinished/1000);
				}
			}
			
			@Override
			public void onFinish() {
				if(mTimerCallBack != null){
					mTimerCallBack.onFinish();
				}
			}
		};
	}
	private void initAutoLoginTimer() {
		mLoginTimer = new CountDownTimer(4*1000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				if(mAutoLoginTimerCallBack != null){
					mAutoLoginTimerCallBack.onFinish();
				}
			}
		};
	}
	/**
	 * 
	 *<p>检查密码的长度</p><br/>
	 *<p>要大于等于6个</p>
	 * @since 1.0.0
	 * @author xky
	 * @return 大于等于6 为true,否则false
	 */
	public boolean checkPwdLength(String pwd){
		if(TextUtils.isEmpty(pwd)){
			return false;
		}
		if(pwd.length()>=6){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 
	 *<p>匹配手机号</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param mobiles
	 * @return
	 */
	public  boolean isMobileNum(String mobiles)  
    {  
     Pattern p = Pattern  .compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");  
     Matcher m = p.matcher(mobiles);    
     return m.matches();  
     }
	/**
	 * 
	 *<p>如果手机存在sim卡，则读取手机号自动注册</p><br/>
	 *<p>如果读取不到手机号则进行其他的注册</p>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public String getPhoneNum(){
		if(Device.isCanUseSim(mContext)){
			return Device.getPhoneNum(mContext);
		}else{
			return null;
		}
	}
	/**
	 * 
	 *<p>处理电话号码</p><br/>
	 *  +86+电话
	 * @since 1.0.0
	 * @author xky
	 * @param phonenum
	 * @return
	 */
	public String operatePhone(String phonenum){
		String phone = null;
		if(TextUtils.isEmpty(phonenum)){
			phone = null;
		}else{
			phone = phonenum.substring(phonenum.length()-11, phonenum.length());
		}
		return phone;
	}
	/**
	 * 
	 *<p>获取一个随机密码</p><br/>
	 *<p>8~16 英文和数字的组合</p>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public String getRandomPwd(){
		StringBuilder builder = new StringBuilder();
		String str = "0123456789abcdefghigklmnopqrstuvwxyz";
		int length = getRandomNum(8, 17);
		for(int i = 1;i<=length;i++){
			builder.append(str.charAt(getRandomNum(0, str.length())));
		}
		return builder.toString();
	}
	/**
	 * 
	 *<p>获取一个随机的用户名</p><br/>
	 *<p>随机用户名由简单的英文单词、数字、最高不能超过16，暂定为【5-16】</p>
	 * @since 1.0.0
	 * @author xky
	 * @return
	 */
	public String getRandomUserName(){
		StringBuilder builder = new StringBuilder();
		String str = "0123456789abcdefghigklmnopqrstuvwxyz";
		int length = getRandomNum(5, 17);
		for(int i = 1;i<=length;i++){
			builder.append(str.charAt(getRandomNum(0, str.length())));
		}
		return builder.toString();
	}
	/**
	 * 
	 *<p>定时器的回掉</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public interface TimerCallBack{
		public void onStart();
		public void onFinish();
		public void onProgress(long count);
	}
	public void setOnTimeCallBack(TimerCallBack callBack){
		mTimerCallBack = callBack;
	}
	public void setOnAutoLoginTimeCallBack(TimerCallBack callBack){
		mAutoLoginTimerCallBack = callBack;
	}
	/**
	 * 
	 *<p>启动定时器</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void startTimer(){
		mTimer.start();
		if(mTimerCallBack != null){
			mTimerCallBack.onStart();
		}
	}
	
	/**
	 * 
	 *<p>启动自动登录延时定时器</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 */
	public void startAutoLoginTimer(){
		mLoginTimer.start();
		if(mAutoLoginTimerCallBack!= null){
			mAutoLoginTimerCallBack.onStart();
		}
	}
	/**
	 * 
	 *<p>取消延时定时器</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author xky
	 */
	public void cancelAutoLoginTimer(){
		mLoginTimer.cancel();
	}
	/**
	 * 
	 *<p>取消定时器</p><br/>
	 * @since 1.0.0
	 * @author xky
	 */
	public void cancalTimer(){
		mTimer.cancel();
	}
	/**
	 * 
	 *<p>获取[min,max)之间的随机数</p><br/>
	 * @since 1.0.0
	 * @author xky
	 * @param min
	 * @param max
	 * @return
	 */
	public int getRandomNum(int min,int max){
		Random random = new Random();
		return random.nextInt(max-min)+min;
	}
	
	/*public void setOnVertifyCodeListener(IVertifyCodeMsgListener vcodeListener){
		mVertifyCodeListener = vcodeListener;
	}
	private void setOnCode_(String msg){
		if(mVertifyCodeListener != null){
			mVertifyCodeListener.onCodeMessage(msg);
		}
	}*/
	}
