package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.FloatManager;
import com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.inter.MsgTitleParentFloatWindow;

/**
 * Created by cherish on 2016/3/24.
 */
public class MessageTitleWindow  extends MsgTitleParentFloatWindow{


    public MessageTitleWindow(Context context) {
        super(context);
    }

    @Override
    protected View getContentView() {

        View view = mInflater.inflate(R.layout.float_window_msg_title,null);


        return view;

    }


//    private void addNormalView( final MessDto dto){
//        layoutNormal = (LinearLayout) mInfalter.inflate(R.layout.item_chat, null);
//
//        CusImageView personIcon = (CusImageView)layoutNormal.findViewById(R.id.person_icon);
//        TextView userName = (TextView)layoutNormal.findViewById(R.id.username);
//        TextView content = (TextView)layoutNormal.findViewById(R.id.chat_content);
//        LinearLayout chatLayout = (LinearLayout) layoutNormal.findViewById(R.id.chat_layoutid);
//
//        if( dto.getType() == MessDto.NORMAL){
//
//            ImageLoader.getBitmap(personIcon, dto.getUser_avatar());
//            userName.setText(dto.getUser_nick());
//            content.setText(dto.getContent());
//            chatLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                        Toast.makeText(mContext,"禁言",0).show();
//                    CusDialogManager dm = new CusDialogManager(getContext());
//                    dm.showBanDialog(dto);
//                }
//            });
//
//        }else if( dto.getType() == MessDto.GIFT){
//            ImageLoader.getBitmap(personIcon, mUser.getAvatar());
//            userName.setText("@" + mUser.getNick());
//            content.setText("送给主播一个礼物");
//            chatLayout.setOnClickListener(null);
//
//        }else if( dto.getType() == MessDto.BAN){//系统禁言消息
//            personIcon.setBackgroundResource(R.drawable.chat_ban_back_border);
//            personIcon.setImageResource(R.drawable.chat_ban_icon);
//            userName.setText("@" + getContext().getResources().getString(R.string.system_message));
//            content.setText(dto.getTarget_uid() + getContext().getResources().getString(R.string.ban_chat));
//            chatLayout.setOnClickListener(null);
//        }
//
//        if (getChildCount()==2){
//            removeViewAt(0);
//        }
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(0,20,0,0);
//        layoutNormal.setLayoutParams(params);
//        addView(layoutNormal);
//        ControllerAnimation.showControllerDownAnimation(layoutNormal);
//    }
//
//    private void addAnchorView( MessDto dto ){
//        layoutAnchor = (LinearLayout) mInfalter.inflate(R.layout.item_anchor_chat,null);
//
//        if( dto.getType() == MessDto.NORMAL){
//            TextView content = (TextView)layoutAnchor.findViewById(R.id.chat_content);
//            content.setText(dto.getContent());
//        }
//
//        if (getChildCount()==2){
//            removeViewAt(0);
//        }
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(0,20,0,0);
//        layoutAnchor.setLayoutParams(params);
//        addView( layoutAnchor );
//        ControllerAnimation.showControllerDownAnimation(layoutAnchor);
//    }



    @Override
    public void onClick(MotionEvent event, View view) {

        FloatManager.getINSTANCE(mContext).openMsgWindow();
        FloatManager.getINSTANCE(mContext).closeMsgTitleWindow();

        super.onClick(event, view);
    }
}
