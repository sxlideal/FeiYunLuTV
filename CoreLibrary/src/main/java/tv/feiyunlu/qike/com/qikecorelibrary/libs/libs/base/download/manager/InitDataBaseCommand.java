package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import android.content.Context;

import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;


public class InitDataBaseCommand implements Runnable {

	private Context context;
	private DatabaseLoadListener listener;
	private String mDatabasePath;
	private DatabaseProvider provider;
	private Class<Downloadable> clazz;

	public InitDataBaseCommand(DatabaseProvider provider, Context context,
			DatabaseLoadListener listener, String databasePath,
			Class<Downloadable> clazz) {
		this.context = context;
		this.listener = listener;
		this.mDatabasePath = databasePath;
		this.provider = provider;
		this.clazz = clazz;
	}

	@Override
	public void run() {
		try {

			provider.init(mDatabasePath);
			List<Downloadable> items = provider.queryAll(clazz);
			listener.onLoaded(items);
		} catch (Throwable e) {
			e.printStackTrace();
			listener.onError();
		}

	}

}
