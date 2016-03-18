package com.qike.feiyunlu.tv.module.database;

import android.os.Environment;
import android.text.TextUtils;

import com.qike.feiyunlu.tv.library.util.PreferencesUtils;
import com.qike.feiyunlu.tv.presentation.application.QikeApplication;


public class DatabasePath {
	public static final  String EXENAL_GAME_DATABASE_PATH="7K7KFeiYunLu/database/";
	public static final String SAVE_PATH = "save_path"; // 保存地址
	public static String getDownloadSavePath() {
			String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			sdcardPath = sdcardPath.endsWith("/") ? sdcardPath : sdcardPath + "/";
			String tempDownloadPath = sdcardPath ;
			String downloadPath = PreferencesUtils.loadPrefString(QikeApplication.getApplication(), SAVE_PATH,
					tempDownloadPath);
			downloadPath = TextUtils.isEmpty(downloadPath) ? tempDownloadPath : downloadPath;
			return downloadPath ;
		}
}

