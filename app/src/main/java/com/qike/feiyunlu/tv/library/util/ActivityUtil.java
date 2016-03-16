package com.qike.feiyunlu.tv.library.util;

import android.content.Context;
import android.content.Intent;

import com.qike.feiyunlu.tv.presentation.view.AnnounceActivity;
import com.qike.feiyunlu.tv.presentation.view.MenuActivity;
import com.qike.feiyunlu.tv.presentation.view.OnlineLiveSettingActivity;

/**
 * 
 *<p>TODO(activity的工具类)</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 1.0.0
 * @author cherish
 */
public class ActivityUtil {
	public static final String VIDEONAMEKEY = "videoname";
	public static final String VIDEOURLKEY = "videourl";
	public static final String VIDEOUTYPEKEY = "videotype";



	public static void startOnLineLiveSettingActivity( Context context){

		context.startActivity( new Intent( context, OnlineLiveSettingActivity.class));


	}


	public static void startMenuActivity( Context context){

		context.startActivity(new Intent(context, MenuActivity.class));
	}

	public static void startAnnounceActivity( Context context){
		context.startActivity(new Intent(context, AnnounceActivity.class));
	}

}
