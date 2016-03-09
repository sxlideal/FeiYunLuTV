package com.qike.feiyunlu.tv.presentation.model.dto;

import com.j256.ormlite.field.DatabaseField;

/**
 * 
 *<p>用户实体类</p><br/>
 * @since 1.0.0
 * @author sll
 */
public class User {
	/**用户id*/
	@DatabaseField(id=true)
	private String user_id;
	
	@DatabaseField
	private String user_type;
	
	@DatabaseField
	private String nick;
	
	@DatabaseField
	private String avatar;
	
	@DatabaseField
	private String set_login_pwd;
	
	@DatabaseField
	private String user_verify;
	
	/**是否为最新的账号 1：是最新的 ，0:不是最新的*/
	@DatabaseField
	private int islast;
	
	@DatabaseField
	private String gender;
	
	
	
	public User(){
		
	}



	public String getUser_id() {
		return user_id;
	}



	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}



	public String getUser_type() {
		return user_type;
	}



	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}



	public String getNick() {
		return nick;
	}



	public void setNick(String nick) {
		this.nick = nick;
	}



	public String getAvatar() {
		return avatar;
	}



	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}



	public String getSet_login_pwd() {
		return set_login_pwd;
	}



	public void setSet_login_pwd(String set_login_pwd) {
		this.set_login_pwd = set_login_pwd;
	}



	public String getUser_verify() {
		return user_verify;
	}



	public void setUser_verify(String user_verify) {
		this.user_verify = user_verify;
	}



	public int getIslast() {
		return islast;
	}



	public void setIslast(int islast) {
		this.islast = islast;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", user_type=" + user_type
				+ ", nick=" + nick + ", avatar=" + avatar + ", set_login_pwd="
				+ set_login_pwd + ", user_verify=" + user_verify + ", islast="
				+ islast + ", gender=" + gender + "]";
	}
	
	
	
	
	
}
