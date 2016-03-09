package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.http;

public class PostContent {
	private String mContentType = null;
	private String mFilename = null;
	private String mFilePath = null;

	public PostContent(String mFilePath) {
		this.mFilePath = mFilePath;
	}

	public PostContent(String mFilename, String mFilePath) {
		this.mFilename = mFilename;
		this.mFilePath = mFilePath;
	}

	public PostContent(String contentType, String filename, String filePath) {
		mContentType = contentType;
		mFilename = filename;
		mFilePath = filePath;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String mContentType) {
		this.mContentType = mContentType;
	}

	public String getFilename() {
		return mFilename;
	}

	public void setFilename(String mFilename) {
		this.mFilename = mFilename;
	}

	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}

	public String toString() {
		return this.getClass() + "Content-Type:" + mContentType + "File-Name:"
				+ mFilename + "File-Path:" + mFilePath;
	}
}
