package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.drive;


public interface Drive {
	public String getDownloadUrl(String url);

	public String verifyVcode(String code);
	public boolean isShowVcode();

}
