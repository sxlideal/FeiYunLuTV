package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.MFloatWindow;


/**
 * Created by cherish on 2016/2/23.
 */
public class MenuFloatWindow extends MFloatWindow {

    private View view;

    public MenuFloatWindow( Context context){
        super(context);
//        getContentView();

    }

    @Override
    protected View getContentView() {

        view = mInflater.inflate(R.layout.float_window_menu,null);

        ImageView closeFloatImage = (ImageView)view.findViewById(R.id.hide_self_btn);
        ImageView homeImage = (ImageView) view.findViewById(R.id.float_home);


        closeFloatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FloatManager.getINSTANCE(mContext).openIconAtLoctionWindow();
                FloatManager.getINSTANCE(mContext).closeMenuWindow();

            }
        });

        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startMenuActivity(mContext, "标签");
            }
        });


        return view;
    }


}
