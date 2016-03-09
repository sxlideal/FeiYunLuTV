package com.qike.feiyunlu.tv.module.network;

import java.util.List;

public class ResultData<T> {
	private Status status;
	private Page page;
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	private List<T> dataList;
	private T data;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
}
