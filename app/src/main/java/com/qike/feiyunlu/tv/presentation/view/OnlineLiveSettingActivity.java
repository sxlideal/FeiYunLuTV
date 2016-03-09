package com.qike.feiyunlu.tv.presentation.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.liveutil.CsMideaProjectionObject;
import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.presenter.Service.FloatService;


// Annotation[SYSTEM, dalvik.annotation.MemberClasses, {value=[com.kascend.chushou.ui.OnlineLiveSettingActivity$b, com.kascend.chushou.ui.OnlineLiveSettingActivity$d, com.kascend.chushou.ui.OnlineLiveSettingActivity$c, com.kascend.chushou.ui.OnlineLiveSettingActivity$a]}]
public class OnlineLiveSettingActivity extends Activity {
    int mBitrate = 2000000;
    int width = 1280;
    int height = 720;
    int orient_type = 0;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int screen_width;
    int screen_height;

    SharedPreferences settings ;
    SharedPreferences.Editor editor;

    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_setting_layout);
        settings = getSharedPreferences("GamelivePrefs", 0);
        editor = settings.edit();

        screen_width = getWindowManager().getDefaultDisplay().getWidth(); //1280;
        screen_height = getWindowManager().getDefaultDisplay().getHeight();// 720;

        RadioGroup orientRadioGroup  = (RadioGroup) findViewById(R.id.orientation_radio_btn);
        int orientation_type = settings.getInt("orientation_type", R.id.horizontal_btn);
        orientRadioGroup.check(orientation_type);
        orientRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                editor.putInt("orientation_type", checkedId);
                editor.commit();
            }
        });


        radioGroup = (RadioGroup) findViewById(R.id.live_density_radio_btn);
        int desity_type = settings.getInt("desity_type", R.id.high_density);
        radioGroup.check(desity_type);
        /*
        if (mBitrate == 1000000) {
            height = 480;
        } else if (mBitrate == 2000000) {
            height = 720;
        } else if (mBitrate == 500000) {
            height = 360;
        }
        int width = (int) (f * ((float) height));
        if (width % 4 != 0) {
            width -= width % 4;
        }
        */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.normal_density:
                        width = screen_width/2;
                        height = screen_height/2;
                        mBitrate = 500000;
                        break;

                    case R.id.high_density:
                        width = screen_width/2;
                        height = screen_height/2;
                        mBitrate = 1000000;
                        break;

                    case R.id.super_density:
                        width = screen_width;
                        height = screen_height;
                        mBitrate = 2000000;
                        break;
                }
                /****************************************/
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Point point = new Point();
                windowManager.getDefaultDisplay().getRealSize(point);
                float f = ((float) point.y) / ((float) point.x);

                if (mBitrate == 1000000) {
                    height = 480;
                } else if (mBitrate == 2000000) {
                    height = 720;
                } else if (mBitrate == 500000) {
                    height = 360;
                }
                int width = (int) (f * ((float) height));
                if (width % 4 != 0) {
                    width -= width % 4;
                }
                /*****************************************************/
                editor.putInt("desity_type", checkedId);
                editor.putInt("width", width);
                editor.putInt("height", height);
                editor.putInt("bitrate", mBitrate);
                editor.commit();
            }
        });


        String rtmp_uri = settings.getString("rtmp_uri", "rtmp://123.56.107.108/myapp/test");
        //String rtmp_uri = settings.getString("rtmp_uri", "/sdcard/record.flv");

        rtmp_uri = "rtmp://123.56.107.108/myapp/test";
        rtmp_uri = "rtmp://wslive-upload.kascend.com/chushou_live/3780c88c055b44a48e9232514e591980";
        rtmp_uri = "rtmp://publish.huizu100.com/g_live/songguangyu?key=d2b755c06e892172";
        EditText mUriEdit = (EditText) findViewById(R.id.live_uri_edit);
        mUriEdit.setText(rtmp_uri);


        Button mStartButton = (Button) findViewById(R.id.online_live_control_btn);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord(1);
            }

        });

        Button mStartButton2 = (Button) findViewById(R.id.online_live_control_btn2);
        mStartButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord(2);
            }

        });


    }

    private void startRecord(int i) {
        EditText mUriEdit = (EditText) findViewById(R.id.live_uri_edit);
        String rtmp_uri = mUriEdit.getText().toString();


        SharedPreferences settings = getSharedPreferences("GamelivePrefs", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("rtmp_uri", rtmp_uri);
        editor.putInt("startmode", i);

        editor.commit();


//                Intent intent = new Intent(OnlineLiveSettingActivity.this, com.gamelive.screenrecorder.MainActivity.class);
//                intent.putExtra("data", "mainActivity");
//                startActivity(intent);
        if (i == 2) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_CODE);
        }

            Intent intent = new Intent(OnlineLiveSettingActivity.this, FloatService.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startService(intent);

    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
//        if (i2 == -1 && i == 17) {
            CsMideaProjectionObject.m8091a().m8092a(this, intent);
//            if (this.m_f5554G) {
//                m8389h();
//            }
//        }
    }

}
