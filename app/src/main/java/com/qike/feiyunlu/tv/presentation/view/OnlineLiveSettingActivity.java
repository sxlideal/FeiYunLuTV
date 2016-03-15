package com.qike.feiyunlu.tv.presentation.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.room.RoomPresenter;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.LiveScreenDto;


// Annotation[SYSTEM, dalvik.annotation.MemberClasses, {value=[com.kascend.chushou.ui.OnlineLiveSettingActivity$b, com.kascend.chushou.ui.OnlineLiveSettingActivity$d, com.kascend.chushou.ui.OnlineLiveSettingActivity$c, com.kascend.chushou.ui.OnlineLiveSettingActivity$a]}]
public class OnlineLiveSettingActivity extends BaseActivity implements IViewOperater {

    private RadioGroup sharpRadioGroup;
    private RadioGroup orientRadioGroup;

    private DrawerLayout mDrawerLayout;
    private Button mStartButton;
    private RelativeLayout mTagLayout;
    private TextView mTagText;
    private LiveScreenDto mLiveDto;

    private RoomPresenter mRoomPresenter;

    private User mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_setting_layout);

        mLiveDto = new LiveScreenDto();

        initView();
        initData();
        setListener();
        loadData();

    }


    @Override
    public void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        initDrawerLayout();
    };

    public void initData(){

    };

    public void setListener(){

    };

    public void loadData(){

    };

    private void initDrawerLayout() {

        final TextView text1 = (TextView) findViewById(R.id.text1);
        final TextView text2 = (TextView) findViewById(R.id.text2);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
                mTagText.setText(text1.getText().toString());
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
                mTagText.setText(text2.getText().toString());
            }
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

}
