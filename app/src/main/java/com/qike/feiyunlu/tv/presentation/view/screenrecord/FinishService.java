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
//        try {
//        Log.i("FinishService", " chh stop ScreenRecorderService now");
//        intent = new Intent(FinishService.this, ScreenRecorderService.class);
//        stopService(intent);
//        }catch (Exception e) {
//
//        }
//        while(true){
//            boolean running = false;
//            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//                if ("gamelive.floating.service.FloatingService".equals(service.service.getClassName())) {
//                    running = true;
//                }
//            }
//            if ( ! running) {
//                Log.i("FinishService"," chh stoped FloatingService now");
//                break;
//            } else {
//                Log.i("FinishService"," chh  FloatingService running now");
//            }
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


//        Log.i("FinishService"," chh stop GameliveMainService now");
//        intent = new Intent(FinishService.this, GameliveMainService.class);
//        stopService(intent);
        try {
            MainActivity.myfinish();
        }catch (Exception e) {

        }
    }
}
