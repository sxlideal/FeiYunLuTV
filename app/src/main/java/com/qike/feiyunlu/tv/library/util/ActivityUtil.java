package com.qike.feiyunlu.tv.library.util;

import android.content.Context;
import android.content.Intent;

import com.qike.feiyunlu.tv.presentation.view.AnnounceActivity;
import com.qike.feiyunlu.tv.presentation.view.LoginActivity;
import com.qike.feiyunlu.tv.presentation.view.MenuActivity;
import com.qike.feiyunlu.tv.presentation.view.MessageActivity;
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

	public static final String GAME_TAG = "game_tag";
	public static void startMenuActivity( Context context,String gameTag){

		Intent intent = new Intent(context, MenuActivity.class);
		intent.putExtra(GAME_TAG,gameTag);
		context.startActivity(intent);
	}

	public static void startAnnounceActivity( Context context){
		context.startActivity(new Intent(context, AnnounceActivity.class));
	}

	public static  void startMessageActivity(Context context){

		context.startActivity(new Intent(context, MessageActivity.class));


	}


	public static void startLoginActivity(Context context){
		context.startActivity(new Intent(context, LoginActivity.class));
	}

}
