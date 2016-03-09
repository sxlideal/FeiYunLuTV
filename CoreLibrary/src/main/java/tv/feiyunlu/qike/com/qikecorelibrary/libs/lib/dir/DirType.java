package tv.feiyunlu.qike.com.qikecorelibrary.libs.lib.dir;
/**
 *注意，重构拇指玩文件系统
 *使用此DirectoryManager ,  
 *继承 DirectoryContext ,
 *调用createAll
 * 
 */
public enum DirType {
	APP_BASE,
		CACHE,
			IMAGE,
			DATA,
			LOG,
			SPLASH,
			UPDATES,
			DB,
			
		RESURCE,
			LOCAL,
			DOWNLOAD,
			USER,
			
		 //收藏
			STORE,
			//礼包
			GIFT,
			//搜索
			SEARCH,
			//挂载
			MOUNT,
			//PC端建立socket链接，保存的数据
			PC
			
}
