package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.support;

import java.util.List;

public class ResponseResult<T> {
	
	
	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 0;
	
	private int total;
	private int pagecount;
	private int pagenum;
	private int code;
	private List<T> rows;
	private int[] daygamecount;

	private int opt;
	
	
	
	
	
	public int[] getDaygamecount() {
		return daygamecount;
	}
	public void setDaygamecount(int[] daygamecount) {
		this.daygamecount = daygamecount;
	}
	public int getOpt() {
		return opt;
	}
	public void setOpt(int opt) {
		this.opt = opt;
	}
	
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
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	
}
