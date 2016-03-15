package com.qike.feiyunlu.tv.presentation.view.widgets.cuspopupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.qike.feiyunlu.tv.R;


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
