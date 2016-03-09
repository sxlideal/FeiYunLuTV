package com.qike.feiyunlu.tv.library.util;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;

public class RecordTool {

	private static final String BASEPATH = "screenrecordbase";
	private static final String PICPATH = "pic";
	private static final String VIDEOPATH = "video";
	private static final String GARBAGEPATH = "garbage";
	private static final String SDCARD = Environment.getExternalStorageDirectory().toString();
	private static RecordTool recordTool = null;
	private Context mContext;
	
	private RecordTool(Context context){
		
		this.mContext = context;
		File baseFile = new File(SDCARD+"/"+BASEPATH);
		File picFile = new File( SDCARD+"/"+BASEPATH +"/"+ PICPATH );
		File videoFile = new File( SDCARD+"/"+BASEPATH +"/"+ VIDEOPATH );
		File garbageFile = new File( SDCARD+"/"+BASEPATH +"/"+ GARBAGEPATH );
		
		if( !baseFile.exists()){
			baseFile.mkdirs();
		}
		if( !picFile.exists()){
			picFile.mkdirs();
		}
		if( !videoFile.exists()){
			videoFile.mkdirs();
		}
		if( !garbageFile.exists()){
			garbageFile.mkdirs();
		}
		
	}
	
	public static synchronized RecordTool getInstants( Context context ){
		
		if( recordTool == null){
			recordTool = new RecordTool(context);
		}
		return recordTool;
		
	}
	public static synchronized RecordTool getInstants( ){
		
		if( recordTool == null){
			
		}
		return recordTool;
		
	}
	
	/**
	 * 
	 *<p>TODO(判断包名应用程序是否处于前台)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author cherish
	 * @param pakagename 应用程序包名
	 * @return boolean
	 */
	public static String packagename = "";
	
	public static void setPackageName( String name ){
		packagename = name;
	}
	
	public boolean isApplicationForeground() {
		if (mContext == null) {
			return false;
		} else {
			ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (!tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (topActivity.getPackageName().equals(packagename)) {
					return true;
				}
			}
		}
		return false;

	}
	
	public String getGarbagePath(){
		String garbagePath = "";
		garbagePath = SDCARD+"/"+BASEPATH+"/"+GARBAGEPATH+"/";
		return garbagePath;
	}
	
	
	public String getVideoPath(){
		
		String videoPath = "";
		videoPath = SDCARD+"/"+BASEPATH+"/"+GARBAGEPATH+"/video"+getFileName();
		return videoPath;
	}
	
	public String getPicPath(){
		String picPath = "";
		picPath = SDCARD+"/"+BASEPATH +"/"+ PICPATH+"/" + getPicName();
		return picPath;
	}
	
	private String getPicName(){
		return System.currentTimeMillis()+".jpg";
	}
	
	public String getAudioPath(){
		
		String audioPath = "";
		audioPath = SDCARD+"/"+BASEPATH+"/"+GARBAGEPATH+"/audio"+getFileName();
		return audioPath;
	}
	
	public String getVAPath(){
		return SDCARD+"/"+BASEPATH+"/"+GARBAGEPATH+"/VA"+getFileName();
	}
	
	public String getRecordPath(){
		return SDCARD+"/"+BASEPATH+"/"+VIDEOPATH+"/record"+getFileName();
	}
	
	
	
	
	private String getFileName(){
		return System.currentTimeMillis()+".mp4";
	}
	
	
	
}
