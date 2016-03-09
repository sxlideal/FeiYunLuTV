package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.widgets.cuspopupwindow.PopupWinManager;

/**
 * Created by cherish on 2016/3/3.
 */
public class MenuLayout extends RelativeLayout {


    private RelativeLayout backLayout;


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

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test","onclick");
            }
        });

        backLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("test","onclick");
                PopupWinManager popupWinManager = new PopupWinManager(getContext());
                popupWinManager.showButtonUpPopupWin(MenuLayout.this, new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });
    }

}
