package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.MsgParentFloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;


/**
 * Created by cherish on 2016/2/24.
 */
public class MsgFloatWindow  extends MsgParentFloatWindow {


    private boolean isExpand = false;//默认开始时不展开

    private View view;
    private RelativeLayout titleBar;
    private ResultsListView listview;
    private ImageView spreadImage;



    public MsgFloatWindow( Context context){
        super(context);

    }
    @Override
    protected View getContentView() {

        view = mInflater.inflate(R.layout.float_window_msg,null);

        titleBar = (RelativeLayout)view.findViewById(R.id.float_titlebar);

       listview = (ResultsListView) view.findViewById(R.id.chat_listview);

        MsgAdapter msgAdapter = new MsgAdapter( mContext );

        listview.setAdapter(msgAdapter, mContext);
        listview.setFooterView(ResultsListView.FOOTER_NOT_DATA);

        spreadImage = (ImageView) view.findViewById(R.id.spread);

        if (isExpand()){
            expand( );
        }else{
            collaps();
        }

        return view;
    }





    @Override
    public void onClick(MotionEvent event, View view) {

        int x = (int)event.getX();
        int y = (int)event.getY();

        titleBar = (RelativeLayout)view.findViewById(R.id.float_titlebar);

        Rect barRect = new Rect();
        titleBar.getHitRect(barRect);
        if(barRect.contains(x,y)){
            collpasOrExpand();
        }

    }

    public void collpasOrExpand(){

        if( isExpand()){
            collaps();
        }else {
            expand( );
        }

    }

    private void expand( ){
        listview.setVisibility(View.VISIBLE);
        isExpand = true;
    }

    private void collaps(){
        listview.setVisibility(View.GONE);
        isExpand = false;
    }

    public boolean isExpand(){
        return isExpand;
    }


    public void updateList(){

    }


}
