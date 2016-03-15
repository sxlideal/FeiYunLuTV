package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow;

import android.content.Context;

import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.IconFloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.MenuFloatWindow;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow.MsgFloatWindow;


/**
 * Created by cherish on 2016/2/23.
 */
public class FloatManager {

    private static FloatManager INSTANCE;

    private IconFloatWindow mIconWindow;
    private MenuFloatWindow mMenuWindow;
    private MsgFloatWindow mMsgWindow;

    public static FloatManager getINSTANCE( Context context){

        if( INSTANCE == null ){
            synchronized (FloatManager.class){
                if ( INSTANCE == null ){
                    INSTANCE = new FloatManager(context);
                }
            }
        }
        return INSTANCE;

    }


    public void setInVisible(){
        mIconWindow.setInVisible();
//        mMenuWindow.setInVisible();
        mMsgWindow.setInVisible();
    }

    public void setVisible(){
        mIconWindow.setVisible();
        mMsgWindow.setVisible();
    }


    private FloatManager(Context context){

        mIconWindow = new IconFloatWindow(context);
        mMenuWindow = new MenuFloatWindow(context);
        mMsgWindow = new MsgFloatWindow(context);

    }
    public void openMenuWindow(){

        mMenuWindow.show();
    }
    public void closeMenuWindow(){
        mMenuWindow.close();
    }
    public void openIconWindow(){
        mIconWindow.show();
    }

    public void closeIconWindow(){
        mIconWindow.close();
    }

    public void openMsgWindow(){
        mMsgWindow.show();
    }

    public void closeMsgWindow(){
        mMsgWindow.close();
    }

    public boolean isOpen( ){
        return mMsgWindow.isShowing();
    }


    public void closeAllWindows(){
        mMsgWindow.close();
        mIconWindow.close();
        mMenuWindow.close();
    }

    public void destroy(){
        closeAllWindows();
        INSTANCE = null;
    }

}
