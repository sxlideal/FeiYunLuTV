package com.qike.feiyunlu.tv.presentation.view.fragment.livefragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.view.fragment.BaseFragment;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.ImageLoader;

/**
 * Created by cherish on 2016/3/15.
 */
public class LiveUserFragment extends BaseFragment {

    private User mUser;


    private ImageButton mBackIB;

    private TextView mNameText;
    private ImageView mIconImage;
    private TextView mRoomText;
    private ImageView mShareImage;

    @Override
    public void initView() {
        mBackIB = (ImageButton)findViewById(R.id.back_btn);


        mIconImage = (ImageView)findViewById(R.id.person_icon);
        mNameText = (TextView)findViewById(R.id.personal_nickname);
        mRoomText = (TextView)findViewById(R.id.user_room_id);
        mShareImage = (ImageView)findViewById(R.id.room_share);

    }

    @Override
    public void initData() {
        mUser = AccountManager.getInstance(getContext()).getUser();

        if( mUser != null){
            ImageLoader.getBitmap(mIconImage,mUser.getAvatar(),true);
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
