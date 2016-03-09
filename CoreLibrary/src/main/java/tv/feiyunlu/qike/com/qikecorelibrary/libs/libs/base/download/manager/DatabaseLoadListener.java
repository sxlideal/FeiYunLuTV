package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import java.util.List;

public interface DatabaseLoadListener {
	public void onLoaded(List<Downloadable> datas);
	public void onError();
}
