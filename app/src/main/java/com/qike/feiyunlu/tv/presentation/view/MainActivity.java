package com.qike.feiyunlu.tv.presentation.view;

import android.os.Bundle;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.presentation.view.inter.IActivityOperate;
import com.qike.feiyunlu.tv.presentation.view.widgets.ResultsListView;

public class MainActivity extends BaseActivity implements IActivityOperate{

    private ResultsListView mChatListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        setListener();
        loadDatas();
    }

    @Override
    public void initView() {
        mChatListview = (ResultsListView)findViewById(R.id.chat_menu_listview);


    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void loadDatas() {

    }
}
