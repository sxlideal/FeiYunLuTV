/*
 * 文件名：Params.java
 * 描述：登录相关网络请求参数
 * 版本：v1.0.0
 * 日期：2014-4-3
 * 版权：Copyright ? 2012 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.function.configuration;

/**
 *<p>网络请求参数</p><br/>
 *<p>网络请求参数</p>
 * @since 1.0.0
 * @author cxd
 */
public class Params {

	public static final String BASE_URL = "http://sdk.muzhiwan.com/";

	public static String BASE_USER_URL = "http://sdk.muzhiwan.com/" + "user/index.php";
	
	

	/**
	 *<p>网络请求参数的key</p><br/>
	 *<p>网络请求参数的key</p>
	 * @since 1.0.0
	 * @author cxd
	 */
	public class ParamsKey {
		public static final String ACT = "act";

		/*------------------登录------------------*/
		public static final String LOGIN_USER = "name";

		public static final String LOGIN_PWD = "pwd";

		/*------------------第三方登录------------------*/
		public static final String LOGIN_THIRD_OPENID = "openid";

		public static final String LOGIN_THIRD_FROMID = "fromid";

		/*------------------第三方注册------------------*/
		public static final String REGISTER_THIRD_USERNAME = "username";

		public static final String REGISTER_THIRD_OPENID = LOGIN_THIRD_OPENID;

		public static final String REGISTER_THIRD_FROMID = LOGIN_THIRD_FROMID;

		public static final String REGISTER_THIRD_PWD = LOGIN_PWD;

		/*------------------手机注册-------------------*/
		public static final String REGISTER_PHONE_NUMBER = "number";

		public static final String REGISTER_PHONE_PWD = LOGIN_PWD;

		public static final String REGISTER_PHONE_CODE = "verifycode";

		/*------------------匿名用户绑定手机----------------*/

		public static final String BIND_PHONE_UID = "uid";

		public static final String BIND_PHONE_PWD = LOGIN_PWD;

		public static final String BIND_PHONE_NUMBER = REGISTER_PHONE_NUMBER;

		public static final String BIND_PHONE_CODE = REGISTER_PHONE_CODE;
	}

	/**
	 *<p>网络请求参数的value</p><br/>
	 *<p>网络请求参数的value</p>
	 * @since 1.0.0
	 * @author cxd
	 */
	public class ParamsValue {

		/**登录*/
		public static final String LOGIN_ACT = "login";
		
		/**登录*/
		public static final String AUTO_LOGIN_ACT = "login_auto";

		/**第三方登录*/
		public static final String LOGIN_THIRD = "login_open";

		/**设备登录*/
		public static final String LOGIN_GUEST = "login_device";

		/**第三方注册*/
		public static final String REGISTER_THIRD = "reg_open";

		/** 手机注册*/
		public static final String REGISTER_PHONE = "reg_phone";
		
		/** 手机一键注册*/
		public static final String REGISTER_PHONE_FAST = "reg_phone_fast";

		/**拇指玩注册*/
		public static final String REGISTER = "reg";

		/**获取验证码*/
		public static final String GET_CODE = "getphonecode";

		/** 匿名用户绑定手机*/
		public static final String DVICE_BIND_PHONE = "reg_device_phone";

	}

}
