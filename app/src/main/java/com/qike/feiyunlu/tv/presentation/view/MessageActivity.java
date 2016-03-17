package com.qike.feiyunlu.tv.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.message.MessagePresenter;
import com.qike.feiyunlu.tv.presentation.view.adapter.SystemInfoAdapter;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;

public class MessageActivity extends BaseActivity implements IViewOperater{


    private ResultsListView mListView;
    private SystemInfoAdapter mAdapter;

    private MessagePresenter messagePresenter;

    private User mUser;

    private TextView mTitleText;
    private ImageButton mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mUser = AccountManager.getInstance(getContext()).getUser();

        initView();
        initData();
        setListener();
        loadData();
    }

    @Override
    public void initView() {
        mTitleText = (TextView)findViewById(R.id.title_text);
        mTitleText.setText(R.string.system_message);
        mBackButton = (ImageButton)findViewById(R.id.back_btn);

        mListView = (ResultsListView)findViewById(R.id.listview);
        mAdapter = new SystemInfoAdapter(getContext());
        mListView.setAdapter(mAdapter,getContext());

    }

    @Override
    public void initData() {
        messagePresenter = new MessagePresenter();
    }

    @Override
    public void setListener() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void loadData() {
        if( mUser != null){
            messagePresenter.getMessage(mUser.getUser_id(), new BaseCallbackPresenter() {
                @Override
                public boolean callbackResult(Object obj) {

                    Log.e("test","message callback return");
                    return false;
                }

                @Override
                public void onErrer(int code, String msg) {

                }
            });
        }

    }
}
