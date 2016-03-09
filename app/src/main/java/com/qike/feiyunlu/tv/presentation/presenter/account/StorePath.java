package com.qike.feiyunlu.tv.presentation.presenter.account;

import android.content.Context;

import com.qike.feiyunlu.tv.library.util.SDCardUtils;
import com.qike.feiyunlu.tv.module.database.DatabasePath;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.DirectoryUtils;


/**
 * 
 *<p>管理存储路径信息</p><br/>
 * @since 1.0.0
 * @author xky
 */
public class StorePath {
//	private static final  String PATHDB = "/7k7kTranscribe/database/";
	private static final  String PathSuffx = "/video_current_user";
	public static String getUsersDbStorePath(Context context){
		String BasePath = "";
//		if(DirectoryUtils.existSDCard(context)){
			SDCardUtils.StorageDriectory directory = SDCardUtils.getInnerStorageDirectory(context, true);
			BasePath = directory.getPath()+"/"+ DatabasePath.EXENAL_GAME_DATABASE_PATH;
//		}
//		else{
//			BasePath = context.getCacheDir().getAbsolutePath()+"/sdk";
//		}
			
//		return	context.getDatabasePath(DatabasePath.EXENAL_GAME_DATABASE_PATH).getPath();
		return BasePath;
	}
	/**
	 * 
	 *<p>获取json的缓存路径</p><br/>
	 *优先使用sd卡路径来缓存 
	 *如果sd卡不存在 在使用 cache路径来缓存
	 * @since 1.0.0
	 * @author xky
	 */
	public static String getBaseJsonPath(Context context){
		String BasePath = "";
		if(DirectoryUtils.existSDCard(context)){
			SDCardUtils.StorageDriectory directory = SDCardUtils.getInnerStorageDirectory(context, true);
			BasePath = directory.getPath()+PathSuffx;
		}else{
			String absolutePath = context.getCacheDir().getAbsolutePath();
			BasePath = absolutePath+PathSuffx;
		}
		return BasePath;
	}
}
