/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qike.feiyunlu.tv.presentation.view.screenrecord;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;

import java.io.File;



public class MainActivity extends Activity {
    private static final String TAG="MainActivity";
	private static final String LUNCH_COUNT = "lunch_count";
	private int lunchCount;
	private SharedPreferences sharedPreferences;
	
	private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    MediaProjection mediaProjection;

    private static MainActivity mContext;
    public static final String finish_Intent = "gamelive.gamelive.screenrecorder.finishIntent";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "chh  onCreate");

        mContext = this;
      //setContentView(R.layout.activity_main);
      mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
               
      Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
      
          startActivityForResult(captureIntent, REQUEST_CODE);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "chh  onActivityResult");
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }
        // video size
        final int width = getWindowManager().getDefaultDisplay().getWidth(); //1280;
        final int height = getWindowManager().getDefaultDisplay().getHeight();// 720;
        File file = new File(Environment.getExternalStorageDirectory(),
                "/download/myrecord-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");

        SharedPreferences settings = getSharedPreferences("GamelivePrefs", 0);
//        int selectId =  settings.getInt("selectId",0);
//        int rwidth = width;
//        int rheight = height;
//
//        switch(selectId) {
//            case 1:
//                rwidth = width/2;
//                rheight = height/2;
//                break;
//            case 2:
//                rwidth = width/4;
//                rheight = height/4;
//                break;
//            case 0:
//            default:
//                break;
//        }

        int rwidth = settings.getInt("width",1280);
        int rheight = settings.getInt("height",720);
        int bitrate = settings.getInt("bitrate",2000000);
        int orientation_type = settings.getInt("orientation_type", R.id.horizontal_btn);

        if (orientation_type != R.id.horizontal_btn) {
            int w = rwidth;
            rwidth = rheight;
            rheight = w;
        }

        String rtmp_uri = settings.getString("rtmp_uri", "rtmp://123.56.107.108/myapp/test");
        mRecorder = new ScreenRecorder(rtmp_uri, rwidth, rheight, bitrate, 1, mediaProjection, file.getAbsolutePath());
        mRecorder.start();
               
        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
//        BaseGamelive.recorderFlag = false;
        moveTaskToBack(true);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            Log.i(TAG, "chh  broadcastReceiver");
            String action = intent.getAction();
            if ( finish_Intent.equals(action) )
            {
                finish();
            }
        }
    };

    public static void myfinish() {
        mContext.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "chh onDestroy");
        if(mRecorder != null){
            ScreenRecorder.mQuit.set(true);
            //mRecorder.quit();
            mRecorder = null;
        }
    }    
   
}
