package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.socket.MessSocket;
import com.qike.feiyunlu.tv.presentation.view.MenuActivity;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;
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

    private RelativeLayout chatLayout;

    private LinearLayout chatMessLayout;

    private ImageView sendMessImage;

    private EditText editText;
    private User mUser;

    private MessSocket messSocket;

    public MsgFloatWindow( Context context){
        super(context);

    }
    @Override
    protected View getContentView() {

        mUser = AccountManager.getInstance(mContext).getUser();
        messSocket = MessSocket.getSocket(mUser.getUser_id(),mUser.getUser_id());
        view = mInflater.inflate(R.layout.float_window_msg,null);

        titleBar = (RelativeLayout)view.findViewById(R.id.float_titlebar);

       listview = (ResultsListView) view.findViewById(R.id.chat_listview);
        chatLayout = (RelativeLayout)view.findViewById(R.id.chat_layout);

        chatMessLayout = (LinearLayout)view.findViewById(R.id.chat_message_layout);

        final MsgAdapter msgAdapter = new MsgAdapter( mContext );
        listview.setAdapter(msgAdapter, mContext);
        listview.setFooterView(ResultsListView.FOOTER_NOT_DATA);
        listview.removeHead();

        spreadImage = (ImageView) view.findViewById(R.id.spread);

        sendMessImage = (ImageView) view.findViewById(R.id.send_message);

        editText = (EditText) view.findViewById(R.id.chat_edit);

        spreadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatManager.getINSTANCE(mContext).openMsgTitleWindow();
                FloatManager.getINSTANCE(mContext).closeMsgWindow();
            }
        });


        sendMessImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) MenuActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow((MenuActivity.activity).getCurrentFocus().getWindowToken(), 0);
                }
                User user =  AccountManager.getInstance(mContext).getUser();

                MessDto dto = new MessDto();
                dto.setType(1);
                dto.setUser_id(user.getUser_id());
                dto.setUser_nick("@" + user.getNick());
                dto.setUser_avatar(user.getAvatar());
                dto.setContent(editText.getText().toString());
                messSocket.emitMessage(dto);

                Log.e("test","emit message");
            }
        });



        messSocket.registListener(new MessSocket.OnNewMessageListener() {
            @Override
            public void OnNewMessage(final MessDto message) {
                (MenuActivity.activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("test", "onNewMessage");
                        msgAdapter.addData(message);
                        listview.setSelection(listview.getAdapter().getCount() - 1);
                    }
                });
            }
        });


        return view;
    }


    @Override
    public void onClick(MotionEvent event, View view) {

        int x = (int)event.getX();
        int y = (int)event.getY();

        chatMessLayout = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        Rect chatRect = new Rect();
        chatMessLayout.getHitRect(chatRect);

        if ( !chatRect.contains(x,y)){
            FloatManager.getINSTANCE(mContext).openMsgTitleWindow();
            FloatManager.getINSTANCE(mContext).closeMsgWindow();

        }

    }




}
