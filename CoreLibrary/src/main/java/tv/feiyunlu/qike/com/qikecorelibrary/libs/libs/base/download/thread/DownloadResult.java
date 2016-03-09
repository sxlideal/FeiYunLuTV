package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.thread;

public class DownloadResult{
	/**
	 * 为找到SD卡
	 */
	
	
	//存储卡未挂载
	public static final int ERROR_CODE_LOCAL_SDCARD_UNMOUNTED = -4001;
	//存储卡空间不足
	public static final int ERROR_CODE_LOCAL_SDCARD_NOTSPACE = -4002;
	//保存路径为空
	public static final int ERROR_CODE_LOCAL_SAVEPATHNULL = -4003;
	//创建文件夹失败
	public static final int ERROR_CODE_LOCAL_MKDIRS = -4004;
	//文件未找到
	public static final int ERROR_CODE_LOCAL_FILENOTFOUND = -4005;
	//创建任务失败
	
	public static final int ERROR_CODE_LOCAL_CREATE_TASKBEAN = -4006;
	//文件指针跳转失败
	
	public static final int ERROR_CODE_LOCAL_SEEK_INDEX = -4007;
	//IO读写异常
	public static final int ERROR_CODE_LOCAL_IO_EXCEPTION = -4008;

	
	//验证码错误
	public static final int ERROR_CODE_LOCAL_VCODE = -4009;

	
	//404错误
	public static final int ERROR_CODE_NETWORK_STATUS_400 = -3001;
	
	
	//500服务器错误
	public static final int ERROR_CODE_NETWORK_STATUS_500 = -3002;
	//300错误
	public static final int ERROR_CODE_NETWORK_STATUS_300 = -3003;
	
	
	//其它网络返回码错误
	public static final int ERROR_CODE_NETWORK_STATUS_OTHER = -3004;
	//网络内容为空错误
	public static final int ERROR_CODE_NETWORK_ENTITY_NULL = -3005;
	
	//未读取到长度错误
	public static final int ERROR_CODE_NETWORK_NO_LENGTH = -3006;
	
	//网络连接错误
	public static final int ERROR_CODE_NETWORK_CONN_ERROR = -3007;
	//网络IO读写错误
	public static final int ERROR_CODE_NETWORK_IO_ERROR = -3008;
	
	//文件下载不完整错误
	public static final int ERROR_CODE_NETWORK_NOT_FULL = -3009;

	
	//连接超时错误
	public static final int ERROR_CODE_NETWORK_TIMEOUT = -3010;

	
	
	//未找到主机错误
	public static final int ERROR_CODE_NETWORK_NOT_HOST = -3011;
	
	
	
	//无网络错误
	public static final int ERROR_CODE_NETWORK_DISABLE = -3012;
	
	//其它网络错误
	public static final int ERROR_CODE_NETWORK_OTHER = -3013;
	
	
	//连接停止错误
	public static final int ERROR_CODE_NETWORK_CONN_STOP = -3014;
	
	
	//任务初始化失败
	public static final int ERROR_CODE_TASK_FAILED = -4010;

	
	//成功
	public static final int SUCCESS = 0;

	private int code = SUCCESS;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
