package com.qike.feiyunlu.tv.presentation.view.widgets.cusdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;


public class CusDialogManager {

	private Context mContext;
	private LayoutInflater mInflater;
	private Dialog mDialog;
	
	
	
	public CusDialogManager( Context context){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
	}

	public void showBanDialog( MessDto dto){
		mDialog = new Dialog(mContext, R.style.MyDialog2);
		View view = mInflater.inflate(R.layout.dialog_ban, null);

		TextView cancleText = (TextView) view.findViewById(R.id.ban_cancle);
		TextView banConfirmText = (TextView) view.findViewById(R.id.ban_confirm);
		TextView banContentText = (TextView) view.findViewById(R.id.ban_content);

		banContentText.setText(dto.getContent());

		cancleText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});

		banConfirmText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissDialog();


			}
		});

		mDialog.setContentView(view);
		mDialog.show();


	}


	public void showExitDialog( OnClickListener onClickListener ){
		
		mDialog = new Dialog(mContext, R.style.MyDialog2);
		View view = mInflater.inflate(R.layout.dialog_exit, null);
		
		TextView cancleText = (TextView) view.findViewById(R.id.cancle);
		TextView logoutText = (TextView) view.findViewById(R.id.logout);
		
		cancleText.setOnClickListener(onClickListener);
		logoutText.setOnClickListener(onClickListener);
		
		mDialog.setContentView(view);
		mDialog.show();
	}
	
	public void dismissDialog(){
		
		if( mDialog != null ){
			mDialog.dismiss();
		}
		
	}
	
}
