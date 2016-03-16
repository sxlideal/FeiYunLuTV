package com.qike.feiyunlu.tv.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.inter.IActivityOperate;
import com.qike.feiyunlu.tv.presentation.view.widgets.ControllerAnimation;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;

public class MenuActivity extends BaseActivity implements IActivityOperate{

    private ResultsListView mChatListview;


    private RelativeLayout backLayout;
    private LinearLayout mStopLayout;
    private RelativeLayout mArrowLayout;
    private LinearLayout mUpDownLayout;

    private ImageView mArrowImage;

    private ImageButton mStopBtn;
    private ImageButton mBackBtn;
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
        mUpDownLayout = (LinearLayout)findViewById(R.id.up_down);

        mStopLayout = (LinearLayout)findViewById(R.id.stop);
        mStopLayout.setVisibility(View.GONE);
        mArrowLayout = (RelativeLayout)findViewById(R.id.arrow_down);
        mArrowLayout.setVisibility(View.GONE);

        mArrowImage = (ImageView)findViewById(R.id.button_up);

        mStopBtn = (ImageButton)findViewById(R.id.stopbtn);
        mBackBtn = (ImageButton)findViewById(R.id.imgback_btn);

    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("test", "onclick");

                if(mArrowLayout.getVisibility() == View.GONE ){
                    mArrowLayout.setVisibility(View.VISIBLE);
                    ControllerAnimation.showControllerUpAnimation(mArrowLayout, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
//                            mArrowLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }else  if(mArrowLayout.getVisibility() == View.VISIBLE ){
                    ControllerAnimation.hideControllerUpAnimation(mArrowLayout, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mArrowLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }


            }
        });


        mArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStopLayout.setVisibility(View.VISIBLE);

                ControllerAnimation.showControllerUpAnimation(mStopLayout, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mArrowLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });



        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



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
