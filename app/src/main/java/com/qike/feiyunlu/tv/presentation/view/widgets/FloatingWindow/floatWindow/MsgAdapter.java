package com.qike.feiyunlu.tv.presentation.view.widgets.FloatingWindow.floatWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.MsgDto;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cherish on 2016/3/1.
 */
public class MsgAdapter extends BaseAdapter {

    private List<MsgDto> mList;

    private LayoutInflater mInflater;

    private Context mContext;

    private static final int TYPE_ANCHOR = 0;

    private static final int TYPE_USER = 1;


    public MsgAdapter( Context context){
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mList = new ArrayList<MsgDto>();
        for ( int i=0; i<10; i++){

            MsgDto dto = new MsgDto();
            dto.setContent("聊天内容" + i);
            if (i%2==0){
                dto.setType(0);
            }else{
                dto.setType(1);
            }
            dto.setUsername("username"+i);
            dto.setUserid("userid"+i);

            mList.add(dto);
        }

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

        MsgDto dto = mList.get(position);


        int type = getItemViewType(position);

        if( convertView ==null ){

            if( type == TYPE_ANCHOR){
                anchorHolder = new AnchorHolder();
                convertView = mInflater.inflate(R.layout.float_msg_anchor,null);
                anchorHolder.content = (TextView)convertView.findViewById(R.id.anchor_chat);

                anchorHolder.content.setText(dto.getContent());

                convertView.setTag( anchorHolder );


            }else if( type == TYPE_USER){

                userHolder = new UserHolder();
                convertView = mInflater.inflate( R.layout.float_msg_item,null);
                userHolder.content = (TextView)convertView.findViewById(R.id.user_chat);
                userHolder.username = (TextView)convertView.findViewById(R.id.username);

                userHolder.username.setText(dto.getUserid());
                userHolder.content.setText(dto.getContent());

                convertView.setTag( userHolder );
            }

        }else{
            if (type == TYPE_ANCHOR){
                anchorHolder = (AnchorHolder)convertView.getTag();
                anchorHolder.content.setText(dto.getContent());

            }else if( type == TYPE_USER){

                userHolder = (UserHolder)convertView.getTag();
                userHolder.content.setText(dto.getContent());
                userHolder.username.setText(dto.getUserid());
            }

        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }


    static class AnchorHolder{
        TextView content;
    }

    static class UserHolder{
        TextView username;
        TextView content;
    }

}
