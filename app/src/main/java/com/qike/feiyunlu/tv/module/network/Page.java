package com.qike.feiyunlu.tv.module.network;

public class Page {
	/**总条数*/
	private int total;
	/**总页数*/
	private int pagecount;//总页数
	private int pagenum;//当前页
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	public int getPagenum() {
		return pagenum;
	}
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

}
