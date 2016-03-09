package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import android.content.Context;

public interface DownloadChecker {
	public void check(Context context, Object item) throws CheckException;
}
