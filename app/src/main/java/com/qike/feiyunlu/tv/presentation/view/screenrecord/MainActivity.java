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
import com.qike.feiyunlu.tv.library.util.ActivityUtil;

import java.io.File;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;

    private static MainActivity mContext;
    public static final String finish_Intent = "gamelive.gamelive.screenrecorder.finishIntent";

    private LiveScreenDto mLiveDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        mLiveDto = (LiveScreenDto)intent.getSerializableExtra("liveDto");

        if( mLiveDto != null){
            Log.e("test",mLiveDto.toString());
        }

        mContext = this;
        setContentView(R.layout.activity_menu);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();

        startActivityForResult(captureIntent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {

            return;
        }

        File file = new File(Environment.getExternalStorageDirectory(),
                "/download/myrecord-" + mLiveDto.getWidth() + "x" + mLiveDto.getHeight() + "-" + System.currentTimeMillis() + ".mp4");

        SharedPreferences settings = getSharedPreferences("GamelivePrefs", 0);

        if( mLiveDto != null){
            mRecorder = new ScreenRecorder(mLiveDto.getRtmp_url(), mLiveDto.getWidth(), mLiveDto.getHeight(), mLiveDto.getBitrate(), 1, mediaProjection, file.getAbsolutePath());
            mRecorder.start();

            Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
//            moveTaskToBack(true);
            ActivityUtil.startMenuActivity(MainActivity.this);

        }else{
            Toast.makeText(this, "cannot record!", Toast.LENGTH_SHORT).show();
        }

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (finish_Intent.equals(action)) {
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

        if (mRecorder != null) {
            ScreenRecorder.mQuit.set(true);

            mRecorder = null;
        }
    }

}
