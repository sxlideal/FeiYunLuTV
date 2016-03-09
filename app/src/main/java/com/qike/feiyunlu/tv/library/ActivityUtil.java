package com.qike.feiyunlu.tv.library;

import android.content.Context;
import android.content.Intent;

import com.qike.feiyunlu.tv.presentation.view.MainActivity;


/**
 * 
 *<p>TODO(activity的工具类)</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 1.0.0
 * @author cherish
 */
public class ActivityUtil {

    public static void startMainActivity( Context context){
        context.startActivity(new Intent( context, MainActivity.class));
    }
	
}
