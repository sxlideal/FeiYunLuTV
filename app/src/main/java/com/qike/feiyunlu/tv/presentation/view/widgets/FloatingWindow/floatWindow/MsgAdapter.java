package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cherish on 2016/3/1.
 */
public class MsgAdapter extends BaseAdapter {

    private List<MessDto> mList;

    private LayoutInflater mInflater;

    private Context mContext;

    private static final int TYPE_ANCHOR = 0;

    private static final int TYPE_USER = 1;

    private static final int TYPE_BAN = 2;

    private static final int TYPE_NUM = 3;

    private User mUser;

    public MsgAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mUser = AccountManager.getInstance(mContext).getUser();

        mList = new ArrayList<MessDto>();
        for (int i = 0; i < 10; i++) {

            MessDto dto = new MessDto();
            dto.setContent("聊天内容" + i);
            dto.setType(0);

            dto.setUser_nick("ddd");
            dto.setUser_id("iiiii");

            mList.add(dto);
        }

    }

    public void addData( MessDto dto){
        mList.add(dto);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AnchorHolder anchorHolder = null;
        UserHolder userHolder = null;
        BanHolder banHolder = null;
        MessDto dto = mList.get(position);

        int type = getItemViewType(position);

        if (convertView == null) {

            if (type == TYPE_ANCHOR) {
                anchorHolder = new AnchorHolder();
                convertView = mInflater.inflate(R.layout.float_msg_anchor, null);
                anchorHolder.content = (TextView) convertView.findViewById(R.id.anchor_chat);

                anchorHolder.content.setText(dto.getContent());

                convertView.setTag(anchorHolder);

            } else if (type == TYPE_USER) {

                userHolder = new UserHolder();
                convertView = mInflater.inflate(R.layout.float_msg_item, null);
                userHolder.content = (TextView) convertView.findViewById(R.id.user_chat);
                userHolder.username = (TextView) convertView.findViewById(R.id.username);

                if (dto.getUser_id().equals(mUser.getUser_id())){
                    userHolder.username.setText(dto.getUser_nick());
                    userHolder.content.setText("送给主播礼物");
                }else {
                    userHolder.username.setText(dto.getUser_nick());
                    userHolder.content.setText(dto.getContent());
                }

                convertView.setTag(userHolder);
            } else if (type == TYPE_BAN) {

                banHolder = new BanHolder();

                convertView = mInflater.inflate(R.layout.float_message_item_ban, null);
                banHolder.content = (TextView) convertView.findViewById(R.id.content);

                banHolder.content.setText("禁言");
                convertView.setTag(banHolder);
            }

        } else {
            if (type == TYPE_ANCHOR) {
                anchorHolder = (AnchorHolder) convertView.getTag();
                anchorHolder.content.setText(dto.getContent());

            } else if (type == TYPE_USER) {

                userHolder = (UserHolder) convertView.getTag();
                userHolder.content.setText(dto.getContent());
                userHolder.username.setText(dto.getUser_nick());
            } else if (type == TYPE_BAN) {
                banHolder = (BanHolder) convertView.getTag();
                banHolder.content.setText("禁言");
            }

        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_NUM;
    }

    @Override
    public int getItemViewType(int position) {

        MessDto dto = mList.get(position);

        if (dto.getUser_id().equals(mUser.getUser_id())) {
            return TYPE_ANCHOR;
        } else if (dto.getType() == MessDto.NORMAL) {
            return TYPE_USER;
        } else if (dto.getType() == MessDto.GIFT) {
            return TYPE_USER;
        } else if (dto.getType() == MessDto.BAN) {
            return TYPE_BAN;
        } else {
            return 0;
        }

    }


    static class AnchorHolder {
        TextView content;
    }

    static class UserHolder {
        TextView username;
        TextView content;
    }

    static class BanHolder {
        TextView content;
    }

}
