package com.qike.feiyunlu.tv.presentation.view.widgets.cuspopupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.socket.MessSocket;


/**
 * 
 *<p>TODO(统一管理弹出的popupwindow)</p><br/>
 *<p>TODO (类的详细的功能描述)</p>
 * @since 1.0.0
 * @author cherish
 */
public class PopupWinManager {

	private PopupWindow mPopupWin;
	private Context mContext;
	private LayoutInflater mInflater;
	private OnDismissListener mOnDismissListener;


	public PopupWinManager(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	public void showShield(final View v){
		View popupView = mInflater.inflate(R.layout.pop_shiled, null);

		CheckBox checkBoxGift = (CheckBox) popupView.findViewById(R.id.shield_gift);
		CheckBox checkBoxSystem = (CheckBox) popupView.findViewById(R.id.shield_system);
		CheckBox checkBoxContent = (CheckBox) popupView.findViewById(R.id.shield_chat);

		User user = AccountManager.getInstance(mContext).getUser();
		final MessSocket socket = MessSocket.getSocket(user.getUser_id(),user.getUser_id());

		if (socket.getIsChatShield()){
			checkBoxContent.setChecked(true);
		}
		if (socket.getIsSystemShield()){
			checkBoxSystem.setChecked(true);
		}
		if (socket.getIsGiftShield()){
			checkBoxGift.setChecked(true);
		}
		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch (buttonView.getId()){
					case R.id.shield_gift:
						if (isChecked){
							socket.setShieldMessage(MessSocket.ShieldMessageType.ShieldGift);
						}else {
							socket.unShieldMessage(MessSocket.ShieldMessageType.ShieldGift);
						}
						break;
					case R.id.shield_system:
						if (isChecked){
							socket.setShieldMessage(MessSocket.ShieldMessageType.ShieldSystem);
						}else {
							socket.unShieldMessage(MessSocket.ShieldMessageType.ShieldSystem);
						}
						break;
					case R.id.shield_chat:
						if (isChecked){
							socket.setShieldMessage(MessSocket.ShieldMessageType.ShieldContent);
						}else {
							socket.unShieldMessage(MessSocket.ShieldMessageType.ShieldContent);
						}
						break;
					default:
						break;
				}

				if ( socket.getIsShield()){
					((ImageView)v).setImageResource(R.drawable.shield_chat);
				}else {
					((ImageView)v).setImageResource(R.mipmap.chat_icon);
				}

			}
		};

		checkBoxGift.setOnCheckedChangeListener(onCheckedChangeListener);
		checkBoxSystem.setOnCheckedChangeListener(onCheckedChangeListener);
		checkBoxContent.setOnCheckedChangeListener(onCheckedChangeListener);

		PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int popupWidth = popupView.getMeasuredWidth();
		int popupHeight =  popupView.getMeasuredHeight();
		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation( v, Gravity.NO_GRAVITY, (location[0]+v.getWidth()/2)-popupWidth/2,location[1]-popupHeight);


	}

	public void showShieldWindow(View v){

		View contentView = mInflater.inflate(R.layout.pop_shiled,null);

		CheckBox checkBoxGift = (CheckBox)contentView.findViewById(R.id.shield_gift);
		CheckBox checkBoxSystem = (CheckBox)contentView.findViewById(R.id.shield_system);
		CheckBox checkBoxChat = (CheckBox)contentView.findViewById(R.id.shield_chat);

		mPopupWin = new PopupWindow(mContext);

		mPopupWin.setContentView(contentView);
		mPopupWin.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWin.setFocusable(true);
		mPopupWin.setTouchable(true);
		mPopupWin.setOutsideTouchable(true);
		mPopupWin.setBackgroundDrawable(new ColorDrawable(0x00000000));
//		mPopupWin.setAnimationStyle(R.style.anim_menu_topbar);

//		mPopupWin.setOnDismissListener(onDismissListener);

		int[] location = new int[2];
		v.getLocationOnScreen(location);
		Log.e("test", "mPopupWin.getHeight()=" + mPopupWin.getHeight());
		mPopupWin.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] + mPopupWin.getHeight());

//		mPopupWin.showAsDropDown(v,0,0,Gravity.BOTTOM);

	}

	public void showButtonUpPopupWin(View v, OnDismissListener onDismissListener, OnClickListener onClickListener) {


		View contentView = mInflater.inflate(R.layout.layout_arrow_down, null);
		
		ImageView imageUp = (ImageView) contentView.findViewById(R.id.button_up);

		imageUp.setOnClickListener(onClickListener);

		initPopupWindow(contentView, v,onDismissListener);

	}

	private void initPopupWindow(View contentView, View locationView , OnDismissListener onDismissListener) {

		mPopupWin = new PopupWindow(mContext);

		mPopupWin.setContentView(contentView);
		mPopupWin.setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWin.setFocusable(true);
		mPopupWin.setTouchable(true);
		mPopupWin.setOutsideTouchable(true);
		mPopupWin.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mPopupWin.setAnimationStyle(R.style.anim_menu_topbar);

		mPopupWin.setOnDismissListener(onDismissListener);
		
		mPopupWin.showAtLocation(locationView, Gravity.TOP, 0, 0);

	}

	public void dismissPopupWindow() {
		if (mPopupWin != null) {
			mPopupWin.dismiss();
		}
	}

}
