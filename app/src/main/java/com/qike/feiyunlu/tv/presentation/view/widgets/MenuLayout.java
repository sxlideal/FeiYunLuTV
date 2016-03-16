package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;

/**
 * Created by cherish on 2016/3/3.
 */
public class MenuLayout extends RelativeLayout {


    private RelativeLayout backLayout;
    private LinearLayout mStopLayout;
    private LinearLayout mArrowLayout;
    private LinearLayout mUpDownLayout;



    public MenuLayout(Context context){
        super(context);
        initView();
    }

    public MenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){




        backLayout = (RelativeLayout)findViewById(R.id.back_layout);

        mStopLayout = (LinearLayout)findViewById(R.id.stop);

//        mUpDownLayout = (LinearLayout)findViewById(R.id.up_down);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test","onclick");
                Toast.makeText(getContext(), "click", 0).show();
            }
        });

        backLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("test", "onclick");
                ControllerAnimation.showControllerUpAnimation(mStopLayout,null);
                Toast.makeText(getContext(),"click",0).show();

            }
        });

//        mUpDownLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("test", "updown onclick");
//            }
//        });

    }

}
