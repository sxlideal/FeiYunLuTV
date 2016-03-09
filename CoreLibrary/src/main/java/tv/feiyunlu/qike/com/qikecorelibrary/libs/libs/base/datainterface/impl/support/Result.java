package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support;

import java.io.Serializable;

public class Result implements Serializable{

	private static final long serialVersionUID = 1L;
	private String type;
	private int code;
	private String content;
	private String errmsg;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getErrmsg() {
		return errmsg;
	}
	
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
	public static int NONENETWORK = -900009;
	public static int NORMAL = -900008;
	public static int HTTPTIMEOUT = -900007;
	
}
