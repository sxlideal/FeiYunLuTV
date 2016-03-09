package com.qike.feiyunlu.tv.presentation.presenter.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.qike.feiyunlu.tv.presentation.view.screenrecord.MainActivity;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.FinishService;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;


/**
 * Created by cherish on 2016/2/22.
 */
public class FloatService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        FloatManager.getINSTANCE(this).openIconWindow();
        FloatManager.getINSTANCE( this ).openMsgWindow();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(FloatService.class.getName(), "chh onStartCommand flags " + flags + " startId " + startId);

        try
        {
            if ( startId == 1 || flags == 1 )
            {
                SharedPreferences settings = getSharedPreferences("GamelivePrefs", 0);
                int startMode = settings.getInt("startmode",1);

                // start recorder
//                Intent sintent = new Intent(context, GameliveRecordService.class);
//                context.startService(sintent);

                if ( startMode == 1) {
                    Intent airplane = new Intent(getApplicationContext(), MainActivity.class);
                    airplane.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(airplane);
                }
//                else {
//                    Intent sintent = new Intent(context, ScreenRecorderService.class);
//                    context.startService(sintent);
//                }
            }
            else
            {
                stopService(intent);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return START_NOT_STICKY;


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatManager.getINSTANCE(this).destroy();
        stopGameliveService();
    }


    public void stopGameliveService()
    {
        Intent intent = new Intent(FloatService.this,FinishService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
