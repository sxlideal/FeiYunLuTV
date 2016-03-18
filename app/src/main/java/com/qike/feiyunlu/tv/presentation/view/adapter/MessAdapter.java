package com.qike.feiyunlu.tv.presentation.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;

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


    public MessAdapter( Context context){
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = new ArrayList<MessDto>();
        User user =  AccountManager.getInstance(context).getUser();
        for (int i=0; i<6;  i++){

            MessDto dto = new MessDto();
            dto.setType(1);
            dto.setUser_id(user.getUser_id());
            dto.setUser_nick("@" + user.getNick());
            dto.setUser_avatar(user.getAvatar());
            dto.setContent("聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容聊天内容");
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if( convertView == null ){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_chat,null);

            holder.personIcon = (ImageView)convertView.findViewById(R.id.person_icon);
            holder.userName = (TextView)convertView.findViewById(R.id.username);
            holder.content = (TextView)convertView.findViewById(R.id.chat_content);

            convertView.setTag(holder);

        }else {

            holder = (ViewHolder)convertView.getTag();

        }

        MessDto dto = mList.get(position);

        if( dto.getType() == MessDto.NORMAL){

            ImageLoader.getBitmap(holder.personIcon,dto.getUser_avatar());
            holder.userName.setText(dto.getUser_nick());
            holder.content.setText(dto.getContent());

        }


        return convertView;
    }

    class ViewHolder{

        ImageView personIcon;
        TextView userName;
        TextView content;
    }

}
