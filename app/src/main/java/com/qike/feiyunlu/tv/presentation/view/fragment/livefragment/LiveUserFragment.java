package com.qike.feiyunlu.tv.presentation.view.fragment.livefragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.view.fragment.BaseFragment;
import com.qike.feiyunlu.tv.presentation.view.widgets.CircleImg;
import com.qike.feiyunlu.tv.presentation.view.widgets.cusdialog.CusDialogManager;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ImageLoader;

/**
 * Created by cherish on 2016/3/15.
 */
public class LiveUserFragment extends BaseFragment {

    private User mUser;


    private ImageButton mBackIB;

    private TextView mNameText;
    private CircleImg mIconImage;
    private TextView mRoomText;
    private ImageView mShareImage;

    @Override
    public void initView() {
        mBackIB = (ImageButton)findViewById(R.id.back_btn);


        mIconImage = (CircleImg)findViewById(R.id.person_icon);
        mNameText = (TextView)findViewById(R.id.personal_nickname);
        mRoomText = (TextView)findViewById(R.id.user_room_id);
        mShareImage = (ImageView)findViewById(R.id.room_share);

    }

    @Override
    public void initData() {
        mUser = AccountManager.getInstance(getContext()).getUser();

        if( mUser != null){
            ImageLoader.getBitmap(mIconImage,mUser.getAvatar());
            mNameText.setText(mUser.getNick());
            mRoomText.setText(getContext().getText(R.string.room_id)+mUser.getUser_id());
        }

    }




    @Override
    public void setListener() {
        mBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CusDialogManager dialogManager = new CusDialogManager(getContext());
                dialogManager.showExitDialog(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        switch (v.getId()) {
                            case R.id.cancle:
                                dialogManager.dismissDialog();
                                break;
                            case R.id.logout:
                                AccountManager.getInstance(getContext()).logout();
                                dialogManager.dismissDialog();
                                ActivityUtil.startLoginActivity(getContext());
                                getActivity().finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
                AccountManager.getInstance(getContext()).logout();


            }
        });
    }

    @Override
    public void loadData() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = AccountManager.getInstance(getContext()).getUser();
    }
}
