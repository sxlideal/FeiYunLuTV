package com.qike.feiyunlu.tv.presentation.view.screenrecord;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.qike.feiyunlu.tv.presentation.presenter.Service.FloatService;

public class FinishService extends Activity {
    private final static String TAG = "FinishService";
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
        Log.i("FinishService", " chh stop it now");
		stopServices();
		

		finish();
        //TODO wait for rtmp sender finish
        Log.i("FinishService", " System.exit");
        System.exit(0);
	}

    private void printServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i(TAG, "runing " + service.service.getClassName());
        }
    }

    private void stopServices()
    {
        printServiceRunning();

        Intent intent;
        try {
            Log.i("FinishService", " chh stop FloatingService now");
            intent = new Intent(FinishService.this, FloatService.class);
            stopService(intent);
        }catch (Exception e) {

        }

        try {
            MainActivity.myfinish();
        }catch (Exception e) {

        }
    }
}
