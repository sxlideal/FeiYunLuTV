package com.qike.feiyunlu.tv.module.network;

/**
 * 
 *<p>TODO(类的概括性描述)</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 5.0.0
 * @author suenxianhao
 */
public class Paths {

	public static final String   BASEPATH = "http://api.feiyun.tv";

//	public static final String ACT="act";
	public static final String KEY="key";
	
	public static final String ACT_RECOMMEND = "index";//推荐act
	public static final String ACT_RECOMMEND_BANNER = "banner";//推荐中的baner act
	public static final String RECOMMEND_ACT_VALUE = "recommend"; // 视频详情页相关视频推荐
	public static final String LOGIN_ACT_VALUE = "login"; //登陆act
	public static final String GET_CODE_ACT_VALUE = "getcode";//获取验证码act
	public static final String REGIST_ACT_VALUE = "reg_phone";//手机号注册act
	/**
	 * 初始化
	 */
	public static final String INIT="initialization";
	/**
	 * 热门搜索
	 */
	public static final String HOT_SEARCH="keyword";
	/**
	 * 搜索结果
	 */
	public static final String SEARCH="search";
	
	
	public static final String PARAM_ACCOUNT = "account";
	public static final String PARAM_MOBILE = "mobile";
	public static final String PARAM_NICK = "nick";
	public static final String PARAM_PWD = "pwd";
	public static final String PARAM_GENDER = "gender";
	public static final String PARAM_AVATAR_URL = "avatar_url";
	public static final String PARAM_NEW_PWD = "new_pwd";
	public static final String PARAM_USER_ID = "user_id";
	public static final String PARAM_INTRO = "intro";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PHONE = "phone";
	public static final String PARAM_USER_VERIFY = "user_verify";
	
	public static final String THIRD_LOGIN = "/api/login/third_source";
	
	public static final String RECOMMEND = "/api/recommend/get_list";
	
	public static final String CHECK_PHONENUM = "/api/register/check";
	
	public static final String REGISTE_MOBILE = "/api/register/mobile";
	
	public static final String LOGIN_MOBILE = "/api/login/web";
	
	public static final String SUBMIT_PICTURE = "/api/upload/image";
	
	public static final String GET_USER_FANS = "/api/user/get_fans_list";
	
	public static final String GET_USER_STAR = "/api/user/get_stars_list";
	
	public static final String USER_PWD_RESET = "/api/user/user_pwd_reset";
	
	public static final String USER_EDIT_INTRO = "/api/user/edit_intro";
	

	public static final String ROOM_URL = "/live/api.php";
	public static final String ROOM_ACT_GETINFO = "get_info";
	public static final String ROOM_ACT_CLOSE = "close";
	public static final String ROOM_ACT_OPEN = "open";
	public static final String ACTION_PARAM = "action";
	
}
