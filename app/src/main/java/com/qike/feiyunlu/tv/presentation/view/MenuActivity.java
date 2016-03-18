package com.qike.feiyunlu.tv.presentation.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.socket.MessSocket;
import com.qike.feiyunlu.tv.presentation.view.adapter.MessAdapter;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;
import com.qike.feiyunlu.tv.presentation.view.inter.IActivityOperate;
import com.qike.feiyunlu.tv.presentation.view.widgets.ControllerAnimation;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;

public class MenuActivity extends BaseActivity implements IActivityOperate{

    private ResultsListView mChatListview;


    private RelativeLayout backLayout;
    private LinearLayout mStopLayout;
    private RelativeLayout mArrowLayout;
    private ImageView mArrowImage;
    private ImageButton mImgBackBtn;
    private ImageButton mStopBtn;
    private ControllerAnimation mAnimControl;

    private LinearLayout mUpDownLayout;
    private ResultsListView mListView;
    private MessAdapter messAdapter;
    private RelativeLayout mEditLayout;
    private EditText mChatEdit;
    private ImageView mCloseEditImg;

    private ImageView mOpenChatEdit;

    private MessSocket mSocket;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mUser = AccountManager.getInstance(getContext()).getUser();
        mSocket = MessSocket.getSocket(mUser.getMobile(),mUser.getUser_id());

        initView();
        initData();
        setListener();
        loadDatas();
    }

    @Override
    public void initView() {



        mChatListview = (ResultsListView)findViewById(R.id.chat_menu_listview);

//        FloatManager.getINSTANCE(MenuActivity.this).setInVisible();

        backLayout = (RelativeLayout)findViewById(R.id.back_layout);
        mUpDownLayout = (LinearLayout)findViewById(R.id.up_down);

        mStopLayout = (LinearLayout)findViewById(R.id.stop);
        mStopLayout.setVisibility(View.GONE);
        mArrowLayout = (RelativeLayout)findViewById(R.id.arrow_down);
        mArrowLayout.setVisibility(View.GONE);

        mArrowImage = (ImageView)findViewById(R.id.button_up);

        mStopBtn = (ImageButton)findViewById(R.id.stopbtn);
        mImgBackBtn = (ImageButton)findViewById(R.id.imgback_btn);

        mListView = (ResultsListView)findViewById(R.id.chat_menu_listview);

        mEditLayout = (RelativeLayout)findViewById(R.id.edit_layout);
        mEditLayout.setVisibility(View.GONE);
        mCloseEditImg = (ImageView)findViewById(R.id.close_chat_edit);
        mChatEdit = (EditText)findViewById(R.id.chat_edit);
        mOpenChatEdit = (ImageView)findViewById(R.id.edit);
    }

    @Override
    public void initData() {
        mAnimControl = new ControllerAnimation(mStopLayout,mArrowLayout );

        messAdapter = new MessAdapter(getContext());
        mListView.setAdapter(messAdapter,getContext());

        mListView.removeHead();
        mListView.setFooterView(ResultsListView.FOOTER_NOT_DATA);


    }

    @Override
    public void setListener() {

        mSocket.registListener(new MessSocket.OnNewMessageListener() {
            @Override
            public void OnNewMessage(final MessDto message) {

                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messAdapter.addData(message);
                    }
                });

            }
        });


        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAnimControl.clickHideShowView();

            }
        });


        mArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAnimControl.clickArrowView();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mOpenChatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLayout.setVisibility(View.VISIBLE);
            }
        });

        mCloseEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void loadDatas() {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        moveTaskToBack(true);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(((Activity)getContext()).getCurrentFocus().getWindowToken(), 0);
            }
            mEditLayout.setVisibility(View.GONE);
            User user =  AccountManager.getInstance(getContext()).getUser();

            MessDto dto = new MessDto();
            dto.setType(1);
            dto.setUser_id(user.getUser_id());
            dto.setUser_nick("@" + user.getNick());
            dto.setUser_avatar(user.getAvatar());
            dto.setContent(mChatEdit.getText().toString());
            mSocket.emitMessage(dto);

            mListView.setSelection(mListView.getAdapter().getCount() - 1);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onStop() {
//        FloatManager.getINSTANCE(MenuActivity.this).setVisible();

        super.onStop();
    }
}
