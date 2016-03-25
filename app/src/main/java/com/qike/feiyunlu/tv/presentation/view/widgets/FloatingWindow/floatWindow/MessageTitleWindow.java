package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.socket.MessSocket;
import com.qike.feiyunlu.tv.presentation.view.MenuActivity;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.inter.MsgTitleParentFloatWindow;

/**
 * Created by cherish on 2016/3/24.
 */
public class MessageTitleWindow  extends MsgTitleParentFloatWindow{


    private RelativeLayout mChatLayout;

    private User mUser;

    private MessSocket messSocket;

    public MessageTitleWindow(Context context) {
        super(context);
    }

    @Override
    protected View getContentView() {

        View view = mInflater.inflate(R.layout.float_window_msg_title,null);

        mChatLayout = (RelativeLayout) view.findViewById(R.id.chat_text_layout);


        mUser = AccountManager.getInstance(mContext).getUser();
        messSocket = MessSocket.getSocket(mUser.getUser_id(), mUser.getUser_id());

        messSocket.registListener(new MessSocket.OnNewMessageListener() {
            @Override
            public void OnNewMessage(final MessDto message) {

                MenuActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addView(message);
                    }
                });
            }
        });

        return view;

    }


    public void addView(MessDto dto){

        int type = dto.getType();

        View convertView = null;

        if (type == MessDto.NORMAL && dto.getUser_id().equals(mUser.getUser_id())) {

            convertView = mInflater.inflate(R.layout.float_msg_anchor, null);
            TextView content = (TextView) convertView.findViewById(R.id.anchor_chat);
            content.setText(dto.getContent());

        } else if (type == MessDto.NORMAL || type == MessDto.GIFT) {

            convertView = mInflater.inflate(R.layout.float_msg_item, null);
            TextView content = (TextView) convertView.findViewById(R.id.user_chat);
            TextView username = (TextView) convertView.findViewById(R.id.username);

            if (type == MessDto.GIFT){
                username.setText(dto.getUser_nick());
                content.setText("送给主播礼物");
            }else {
                username.setText(dto.getUser_nick());
                content.setText(dto.getContent());
            }


        } else if (type == MessDto.BAN) {

            convertView = mInflater.inflate(R.layout.float_message_item_ban, null);
            TextView content = (TextView) convertView.findViewById(R.id.content);
            content.setText("禁言");
        }

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        mChatLayout.removeViewAt(0);
        mChatLayout.addView(convertView,0);

    }



    @Override
    public void onClick(MotionEvent event, View view) {

        FloatManager.getINSTANCE(mContext).openMsgWindow();
        FloatManager.getINSTANCE(mContext).closeMsgTitleWindow();

        super.onClick(event, view);
    }
}
