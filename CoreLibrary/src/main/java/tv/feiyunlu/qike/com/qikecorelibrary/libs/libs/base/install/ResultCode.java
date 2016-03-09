package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

public class ResultCode {
	public static final int INSTALL_SUCCESS = 1000;
	
	public static final int NOT_FOUND = 1001;
	public static final int APKCRC32_INCORRECT = 1002;
	public static final int DATACRC32_INCORRECT = 1003;
	public static final int CPUTYPE_INCORRECT = 1004;
	
	public static final int ROOT_INSTALL_FAIL_DEFAULT = 1005;
	public static final int ROOT_INSTALL_FAIL_SDCARD = 1012;
	public static final int INSTALL_EXCEPTION = 1006;
	public static final int SDCARD_NOT_FOUND = 1007;
	public static final int SCREENDENSITY_INCORRECT = 1008;
	public static final int SDCARD_NOT_ENOUGH = 1009;
	public static final int INSTALL_RUNNING = 1010;
	
	public static final int SDKVERSION_INCORRECT = 1011;
	public static final int INSTALL_MOUNT_FAIL = 1013;
	
	
	public static final int INSTALL_SIGNATURE_FAIL = 1014;
	public static final int INSTALL_SIGNATURE_FAIL_WHEN_CLICK_UPDATE = 1015;
	
	
	
	public static final int INSTALL_CANCEL = 2000;
	public static final int INSTALL_VERIFYCOMPLETE = 4000;
	public static final int INSTALL_PREPARE = 5000;
	
	
	
	public static final int INSTALL_SYSTEM = 1016;
	public static final int INSTALL_PARSE_ERROR = 1017;

	public static final int NONE = 0;
	
}
