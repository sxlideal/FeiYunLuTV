package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.MFloatWindow;


/**
 * Created by cherish on 2016/2/23.
 */
public class IconFloatWindow extends MFloatWindow {

    private View view;
    private ImageView btn1;
    private ImageView btn2;

    public IconFloatWindow( Context context){
        super(context);
//        getContentView();

    }

    @Override
    protected View getContentView() {

        view = mInflater.inflate(R.layout.float_window_icon, null);

        return view;
    }


    @Override
    public void onClick(MotionEvent event,View view) {
        Rect rect = new Rect();
        view.getHitRect(rect);

        btn1 = (ImageView) view.findViewById(R.id.btn1);

        FloatManager.getINSTANCE(mContext).openMenuWindow();
        FloatManager.getINSTANCE(mContext).closeIconWindow();


//        mContext.stopService(new Intent(mContext, FloatService.class));

        Log.e("test", "onclick");
        super.onClick(event,view);
    }
}
