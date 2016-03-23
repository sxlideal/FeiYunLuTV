package com.qike.feiyunlu.tv.presentation.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;
import com.qike.feiyunlu.tv.presentation.view.widgets.CusImageView;
import com.qike.feiyunlu.tv.presentation.view.widgets.cusdialog.CusDialogManager;

import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ImageLoader;

/**
 * Created by cherish on 2016/3/18.
 */
public class MessAdapter extends BaseAdapter {

    private Context mContext;
    private List<MessDto> mList;
    private LayoutInflater mInflater;

    private static final int TYPE_ANCHOR = 0;
    private static final int TYPE_WATCHER = 1;
    private static final int TYPE_NUM = 2;

    private User mUser;

    public MessAdapter( Context context){
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = new ArrayList<MessDto>();
        mUser =  AccountManager.getInstance(context).getUser();
        for (int i=0; i<6;  i++){

            MessDto dto = new MessDto();
            dto.setType(1);
            dto.setUser_id(mUser.getUser_id());
            dto.setUser_nick("@" + mUser.getNick());
            dto.setUser_avatar(mUser.getAvatar());
            dto.setContent("聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容");
            mList.add(dto);
        }

    }

    @Override
    public int getViewTypeCount() {
        return TYPE_NUM;
    }

    @Override
    public int getItemViewType(int position) {
        MessDto dto = mList.get(position);
        if(dto.getUser_id().equals(mUser.getUser_id()) && !(dto.getType() == MessDto.BAN)){//是主播发文字消息，并且不是禁言消息
            return TYPE_ANCHOR;
        }else {
            return TYPE_WATCHER;
        }
    }

    public void addData( MessDto dto){
        mList.add(dto);
        Log.e("test", "size=" + mList.size());
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        AnchorHolder anchorHolder = null;
        if ( getItemViewType(position) == TYPE_ANCHOR){
            anchorHolder = new AnchorHolder();
            if( convertView == null ){
                convertView = mInflater.inflate(R.layout.item_anchor_chat,null);
                anchorHolder.content = (TextView)convertView.findViewById(R.id.chat_content);
                convertView.setTag(anchorHolder);
            }else {
                anchorHolder = (AnchorHolder)convertView.getTag();
            }
            MessDto dto = mList.get(position);
            if( dto.getType() == MessDto.NORMAL){
                anchorHolder.content.setText(dto.getContent());
            }
        }else {
            if( convertView == null ){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_chat,null);
                holder.personIcon = (CusImageView)convertView.findViewById(R.id.person_icon);
                holder.userName = (TextView)convertView.findViewById(R.id.username);
                holder.content = (TextView)convertView.findViewById(R.id.chat_content);
                holder.chatLayout = (LinearLayout) convertView.findViewById(R.id.chat_layoutid);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            final MessDto dto = mList.get(position);

            if( dto.getType() == MessDto.NORMAL){

                ImageLoader.getBitmap(holder.personIcon,dto.getUser_avatar());
                holder.userName.setText("@"+dto.getUser_nick());
                holder.content.setText(dto.getContent());
                holder.chatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(mContext,"禁言",0).show();
                        CusDialogManager dm = new CusDialogManager(mContext);
                        dm.showBanDialog(dto);
                    }
                });

            }else if( dto.getType() == MessDto.GIFT){
                ImageLoader.getBitmap(holder.personIcon, mUser.getAvatar());
                holder.userName.setText("@" + mUser.getNick());
                holder.content.setText("送给主播一个礼物");
                holder.chatLayout.setOnClickListener(null);

            }else if( dto.getType() == MessDto.BAN){//系统禁言消息
                holder.personIcon.setBackgroundResource(R.drawable.chat_ban_back_border);
                holder.personIcon.setImageResource(R.drawable.chat_ban_icon);
                holder.userName.setText("@" + mContext.getResources().getString(R.string.system_message));
                holder.content.setText(dto.getTarget_uid()+mContext.getResources().getString(R.string.ban_chat));
                holder.chatLayout.setOnClickListener(null);
            }
        }

        return convertView;
    }

    class ViewHolder{

        CusImageView personIcon;
        TextView userName;
        TextView content;

        LinearLayout chatLayout;
    }

    class AnchorHolder{

        TextView content;

    }

}
