package com.qike.feiyunlu.tv.presentation.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.PreferencesUtils;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.room.RoomPresenter;

public class AnnounceActivity extends BaseActivity implements IViewOperater{

    public static final String ANNOUNCE = "announce";
    private TextView mTitleText;
    private ImageButton mBackButton;

    private EditText mAnnounceEdit;
    private Button mSaveButton;

    private RoomPresenter roomPresenter;

    private User mUser;
    private String mAnnounce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        initView();
        initData();
        setListener();
        loadData();

    }


    @Override
    public void initView() {

        mUser = AccountManager.getInstance(getContext()).getUser();
        mTitleText = (TextView)findViewById(R.id.title_text);
        mBackButton = (ImageButton)findViewById(R.id.back_btn);

        mAnnounceEdit = (EditText)findViewById(R.id.announce_edit);
        mSaveButton = (Button)findViewById(R.id.save_announce);


    }

    @Override
    public void initData() {
        roomPresenter = new RoomPresenter();

        mTitleText.setText(R.string.live_announce);
    }

    @Override
    public void setListener() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAnnounce = mAnnounceEdit.getText().toString();

                roomPresenter.setRoomSetting(mUser.getUser_id(), mUser.getUser_verify(), "", "", mAnnounce, new BaseCallbackPresenter() {
                    @Override
                    public boolean callbackResult(Object obj) {

                        Toast.makeText(getContext(),R.string.save_success,0 ).show();
                        PreferencesUtils.savePrefString(getContext(), ANNOUNCE, mAnnounce);
                        finish();
                        return false;
                    }

                    @Override
                    public void onErrer(int code, String msg) {

                    }
                });


            }
        });
    }

    @Override
    public void loadData() {

        mAnnounce = PreferencesUtils.loadPrefString(getContext(), ANNOUNCE, "");
        mAnnounceEdit.setText(mAnnounce);

    }
}
