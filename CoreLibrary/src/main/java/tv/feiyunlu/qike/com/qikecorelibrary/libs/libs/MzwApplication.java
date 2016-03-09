package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs;

import android.app.Application;

import java.io.File;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.IDiskCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.disk.impl.DiskBasedCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.cache.memory.impl.LRULimitedMemoryCache;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Config;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;


@Config(globalBasePath = "$sdcard/muzhiwan", publicBasePath = "$global", sign = 0)
public class MzwApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		Configuration config = Configuration.getConfiguration();
		config.buildConfig(this);
		config.setCacheTTL(5 * 60 * 1000);
		IDiskCache diskCache = new DiskBasedCache(new File(config.getPublicBasePath() + "/cachee"));
		diskCache.initialize();
		config.setDiskCache(diskCache);
		config.setMemoryCache(new LRULimitedMemoryCache(5 * 1024 * 1024));

	}
}
