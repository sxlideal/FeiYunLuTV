package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.inter.FloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.inter.IMoveListener;


/**
 * Created by cherish on 2016/2/22.
 */
public abstract class MFloatWindow implements FloatWindow {

    public Context mContext;

    private WindowManager mWM;

    private WindowManager.LayoutParams layoutParams;

    public LayoutInflater mInflater;

    private View mContainerView;

    private boolean isShowing;

    private int[] location = new int[2];


    public MFloatWindow(Context context) {

        mContext = context;
        mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        initView();
        initParams();

    }


    public void initView() {

        mInflater = LayoutInflater.from(mContext);
        mContainerView = (View) mInflater.inflate(R.layout.layout_float_window, null);
        mContainerView.setOnTouchListener(onTouchListener);
        RelativeLayout container = (RelativeLayout) mContainerView.findViewById(R.id.container);

        container.addView(getContentView());

    }


    public void initParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;


        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

    }


    @Override
    public void show() {
        if (!isShowing) {
            isShowing = true;
            mWM.addView(mContainerView, layoutParams);

        }
    }

    public int[] getLocation(){

        int[] location = new int[2];
        mContainerView.getLocationOnScreen(location);
        return location;

    }

    public void showAtLastLocation(){
        if (!isShowing) {
            isShowing = true;
            layoutParams.x = location[0];
            layoutParams.y= location[1];
            mWM.addView(mContainerView, layoutParams);

        }

    }


    @Override
    public void close() {
        if (isShowing) {
            isShowing = false;
            mContainerView.getLocationOnScreen(location);
            mWM.removeView(mContainerView);

        }
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void initLocation(int left, int top) {

        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        if (left > mWM.getDefaultDisplay().getWidth()) {
            left = mWM.getDefaultDisplay().getWidth();
        }
        if (top > mWM.getDefaultDisplay().getHeight()) {
            top = mWM.getDefaultDisplay().getHeight();
        }
        layoutParams.x = left;
        layoutParams.y = top;

    }

    @Override
    public void setParams(Object... objects) {

    }

    @Override
    public void setOnConnectMoveListener(IMoveListener moveListener) {

    }

    public void onClick( MotionEvent event,View view){

    }

    protected abstract View getContentView();

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        int x;
        int y;
        long time;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();

                    time = System.currentTimeMillis();

                    break;
                case MotionEvent.ACTION_MOVE:

                    int dx = (int) event.getRawX() - x;
                    int dy = (int) event.getRawY() - y;

                    layoutParams.x = layoutParams.x + dx;
                    layoutParams.y = layoutParams.y + dy;

                    if (mContainerView != null) {
                        mWM.updateViewLayout(mContainerView, layoutParams);
                    }
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:

                    if(System.currentTimeMillis() - time < 300){
                        onClick(event,mContainerView);
                    }
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    int width = mWM.getDefaultDisplay().getWidth();
                    int height = mWM.getDefaultDisplay().getHeight();
                    int mDx = 0;
                    int mDy = 0;
                    if (x > width / 2) {
                        mDx = width - x;
                    } else {
                        mDx = x;
                    }
                    if (y > height / 2) {
                        mDy = height - y;
                    } else {
                        mDy = y;
                    }
                    if (mDx > mDy) {
                        //靠近y�?
                        if (y > height / 2) {
                            //靠近下边
                            layoutParams.y = height;
                        } else {
                            //靠近上边
                            layoutParams.y = 0;
                        }
                    } else {
                        //靠近x�?
                        if (x > width / 2) {
                            //右边
                            layoutParams.x = width;
                        } else {
                            //坐标
                            layoutParams.x = 0;
                        }
                    }
                    if(mContainerView != null && isShowing ){
                        mWM.updateViewLayout(mContainerView, layoutParams);

                    }
                    break;
            }


            return false;
        }
    };


}
