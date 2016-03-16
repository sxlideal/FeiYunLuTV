package com.qike.feiyunlu.tv.presentation.view.adapter;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;

public class SystemInfoAdapter extends BaseAdapter{
	
	private Context mContext;
	
	public SystemInfoAdapter(Context context){
		mContext=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=View.inflate(mContext, R.layout.item_system_info, null);
			holder.mTimeText=(TextView) convertView.findViewById(R.id.tv_time);
			holder.mContentText=(TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_details=(TextView) convertView.findViewById(R.id.tv_details);
			holder.mDetails=(RelativeLayout) convertView.findViewById(R.id.rl_btn_details);
			convertView.setTag(holder);
		}else {
			
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tv_details.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "details", 0).show();
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView mTimeText;
		TextView mContentText;
		TextView tv_details;
		RelativeLayout mDetails;
	}
	
	

}
