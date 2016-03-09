package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.accounts;

import java.io.Serializable;

public class UserData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7571682141050527465L;
	private String userName;
	private String nickName;
	private String pwd;
	private String openid;
	private String fromId;
	private String type;
	private String iconpath;
	
	
	
	
	
	
	
	public String getIconpath() {
		return iconpath;
	}
	public void setIconpath(String iconpath) {
		this.iconpath = iconpath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
//	public String getType() {
//		return type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	
	
}	
