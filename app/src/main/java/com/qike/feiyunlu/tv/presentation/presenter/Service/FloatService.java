package com.qike.feiyunlu.tv.presentation.presenter.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.MenuActivity;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.FinishService;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.LiveScreenDto;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.MainActivity;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by cherish on 2016/2/22.
 */
public class FloatService extends Service {


    private static final Class[] mStartForegroundSignature = new Class[] {
            int.class, Notification.class};
    private static final Class[] mStopForegroundSignature = new Class[] {
            boolean.class};
    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];




    @Override
    public void onCreate() {
        super.onCreate();

        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mStartForeground = FloatService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = FloatService.class.getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }
        // 我们并不需要为 notification.flags 设置 FLAG_ONGOING_EVENT，因为
        // 前台服务的 notification.flags 总是默认包含了那个标志位
        Notification notification = new Notification(R.drawable.feiyun_logo, "Foreground Service Started.",
                System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MenuActivity.class), 0);
        notification.setLatestEventInfo(this, "Foreground Service",
                "Foreground Service Started.", contentIntent);
        // 注意使用  startForeground ，id 为 0 将不会显示 notification
        startForegroundCompat(1, notification);


        FloatManager.getINSTANCE(this).openIconWindow();
        FloatManager.getINSTANCE(this).openMsgTitleWindow();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i(FloatService.class.getName(), "chh onStartCommand flags " + flags + " startId " + startId);

        try {
            if (startId == 1 || flags == 1) {
                LiveScreenDto dto = (LiveScreenDto) intent.getSerializableExtra("liveDto");
                Intent airplane = new Intent(getApplicationContext(), MainActivity.class);
                airplane.putExtra("liveDto", dto);

                airplane.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(airplane);

            } else {
                stopService(intent);
            }
        } catch (Exception e) {
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

        stopGameliveService();

        FloatManager.getINSTANCE(this).closeAllWindows();
        FloatManager.getINSTANCE(this).destroy();
        stopForegroundCompat(1);

    }

    public void stopGameliveService() {
        Intent intent = new Intent(FloatService.this, FinishService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    // 以兼容性方式开始前台服务
    private void startForegroundCompat(int id, Notification n){
        if(mStartForeground != null){
            mStartForegroundArgs[0] = id;
            mStartForegroundArgs[1] = n;

            try {
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return;
        }
        mNM.notify(id, n);
    }

    // 以兼容性方式停止前台服务
    private void stopForegroundCompat(int id){
        if(mStopForeground != null){
            mStopForegroundArgs[0] = Boolean.TRUE;

            try {
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }

        //  在 setForeground 之前调用 cancel，因为我们有可能在取消前台服务之后
        //  的那一瞬间被kill掉。这个时候 notification 便永远不会从通知一栏移除
        mNM.cancel(id);
    }

}
