package com.qike.feiyunlu.tv.module.network;

public class Status {
	private int code;
	private String message;

	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setCode(int code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "Status [code=" + code + ", message=" + message + "]";
	}

	

}
