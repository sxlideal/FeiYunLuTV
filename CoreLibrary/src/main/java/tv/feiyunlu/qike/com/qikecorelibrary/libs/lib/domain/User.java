package tv.feiyunlu.qike.com.qikecorelibrary.libs.lib.domain;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	private long userid;
	private String username;
	private String pwd;
	private Integer sex;
	private String location;
	private String mail;
	private Integer bid;
	private Integer pid;
	private String icon;
	private String device;
	
	private Integer commentCount;	//评论总数
	private Integer giftsCount;		//礼包总数
	private Integer downloadCount;	//我下载的游戏总数
	private Integer storeCount;		//我的收藏总数
	
	
	private String fromId;
	private String openId;
	
	private String fromtype; //代表注册或登录
	
	
	
	private int random;
	
	/**
	 * 1 means 已绑定
	 * 0 means 未绑定
	 */
	private int bound;
	
	
	
	
	
	
	
	
	


	public int getBound() {
		return bound;
	}


	public void setBound(int bound) {
		this.bound = bound;
	}


	public int getRandom() {
		return random;
	}


	public void setRandom(int random) {
		this.random = random;
	}


	public String getFromtype() {
		return fromtype;
	}


	public void setFromtype(String fromtype) {
		this.fromtype = fromtype;
	}


	public String getFromId() {
		return fromId;
	}


	public void setFromId(String fromId) {
		this.fromId = fromId;
	}


	public String getOpenId() {
		return openId;
	}


	public void setOpenId(String openId) {
		this.openId = openId;
	}


	public String getDevice() {
		return device;
	}


	public void setDevice(String device) {
		this.device = device;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


	public Integer getSex() {
		return sex;
	}


	public void setSex(Integer sex) {
		this.sex = sex;
	}


	public Integer getBid() {
		return bid;
	}


	public void setBid(Integer bid) {
		this.bid = bid;
	}


	public Integer getPid() {
		return pid;
	}


	public void setPid(Integer pid) {
		this.pid = pid;
	}


	public String getMail() {
		return mail;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}




	public long getUserid() {
		return userid;
	}
	
	
	/**
	 * 
	 */
	public User() {
	}


	public Integer getCommentCount() {
		return commentCount;
	}


	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}


	public Integer getGiftsCount() {
		return giftsCount;
	}


	public void setGiftsCount(Integer giftsCount) {
		this.giftsCount = giftsCount;
	}


	public Integer getDownloadCount() {
		return downloadCount;
	}


	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}


	public Integer getStoreCount() {
		return storeCount;
	}


	public void setStoreCount(Integer storeCount) {
		this.storeCount = storeCount;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * @param username
	 * @param pwd
	 */
	public User(String username, String pwd) {
		super();
		this.username = username;
		this.pwd = pwd;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
