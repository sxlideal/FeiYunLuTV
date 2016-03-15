package com.qike.feiyunlu.tv.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.inter.IActivityOperate;
import com.qike.feiyunlu.tv.presentation.view.widgets.ControllerAnimation;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;

public class MenuActivity extends BaseActivity implements IActivityOperate{

    private ResultsListView mChatListview;


    private RelativeLayout backLayout;
    private LinearLayout mStopLayout;
    private LinearLayout mArrowLayout;
    private LinearLayout mUpDownLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initView();
        initData();
        setListener();
        loadDatas();
    }

    @Override
    public void initView() {
        mChatListview = (ResultsListView)findViewById(R.id.chat_menu_listview);

//        FloatManager.getINSTANCE(MenuActivity.this).setInVisible();

        backLayout = (RelativeLayout)findViewById(R.id.back_layout);

        mStopLayout = (LinearLayout)findViewById(R.id.stop);

        mUpDownLayout = (LinearLayout)findViewById(R.id.up_down);



        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("test", "onclick");
                ControllerAnimation.showControllerUpAnimation(mStopLayout);
                Toast.makeText(getContext(),"click",0).show();

            }
        });

        mUpDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "updown onclick");
                Toast.makeText(getContext(),"click",0).show();
            }
        });



    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void loadDatas() {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        moveTaskToBack(true);

    }

    @Override
    protected void onStop() {
//        FloatManager.getINSTANCE(MenuActivity.this).setVisible();

        super.onStop();
    }
}
