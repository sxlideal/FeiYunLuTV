package com.qike.feiyunlu.tv.presentation.view.chatview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.socket.MessSocket;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;
import com.qike.feiyunlu.tv.presentation.view.widgets.ControllerAnimation;
import com.qike.feiyunlu.tv.presentation.view.widgets.CusImageView;
import com.qike.feiyunlu.tv.presentation.view.widgets.cusdialog.CusDialogManager;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ImageLoader;

/**
 * Created by cherish on 2016/3/22.
 */
public class ChatView extends LinearLayout{



    private LinearLayout layoutAnchor;
    private LinearLayout layoutNormal;

    private LayoutInflater mInfalter;


    public ChatView(Context context) {
        super(context);

    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }
    private  User mUser;
    private MessSocket messSocket;

    private void initView() {
        mInfalter = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mChatLayout = (LinearLayout)mInfalter.inflate(R.layout.item_two_chat, null);

        mUser = AccountManager.getInstance(getContext()).getUser();
        messSocket = MessSocket.getSocket(mUser.getUser_id(), mUser.getUser_id());

        messSocket.registListener(new MessSocket.OnNewMessageListener() {
            @Override
            public void OnNewMessage(final MessDto message) {

                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(message.getUser_id().equals(mUser.getUser_id()) && !(message.getType() == MessDto.BAN)){//是主播发文字消息，并且不是禁言消息
                            addAnchorView(message);
                        }else {
                            addNormalView(message);
                        }

                    }
                });
            }
        });
    }


    private void addNormalView( final MessDto dto){
        layoutNormal = (LinearLayout) mInfalter.inflate(R.layout.item_chat, null);

        CusImageView personIcon = (CusImageView)layoutNormal.findViewById(R.id.person_icon);
        TextView userName = (TextView)layoutNormal.findViewById(R.id.username);
        TextView content = (TextView)layoutNormal.findViewById(R.id.chat_content);
        LinearLayout chatLayout = (LinearLayout) layoutNormal.findViewById(R.id.chat_layoutid);

        if( dto.getType() == MessDto.NORMAL){

            ImageLoader.getBitmap( personIcon,dto.getUser_avatar());
            userName.setText(dto.getUser_nick());
            content.setText(dto.getContent());
            chatLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        Toast.makeText(mContext,"禁言",0).show();
                    CusDialogManager dm = new CusDialogManager(getContext());
                    dm.showBanDialog(dto);
                }
            });

        }else if( dto.getType() == MessDto.GIFT){
            ImageLoader.getBitmap(personIcon, mUser.getAvatar());
            userName.setText("@" + mUser.getNick());
            content.setText("送给主播一个礼物");
            chatLayout.setOnClickListener(null);

        }else if( dto.getType() == MessDto.BAN){//系统禁言消息
            personIcon.setBackgroundResource(R.drawable.chat_ban_back_border);
            personIcon.setImageResource(R.drawable.chat_ban_icon);
            userName.setText("@" + getContext().getResources().getString(R.string.system_message));
            content.setText(dto.getTarget_uid() + getContext().getResources().getString(R.string.ban_chat));
            chatLayout.setOnClickListener(null);
        }

        if (getChildCount()==2){
            removeViewAt(0);
        }

        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.setMargins(0,20,0,0);
        layoutNormal.setLayoutParams(params);
        addView(layoutNormal);
        ControllerAnimation.showControllerDownAnimation(layoutNormal);
    }

    private void addAnchorView( MessDto dto ){
        layoutAnchor = (LinearLayout) mInfalter.inflate(R.layout.item_anchor_chat,null);

        if( dto.getType() == MessDto.NORMAL){
            TextView content = (TextView)layoutAnchor.findViewById(R.id.chat_content);
            content.setText(dto.getContent());
        }

        if (getChildCount()==2){
            removeViewAt(0);
        }
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.setMargins(0,20,0,0);
        layoutAnchor.setLayoutParams(params);
        addView( layoutAnchor );
        ControllerAnimation.showControllerDownAnimation(layoutAnchor);
    }



}
