package com.qike.feiyunlu.tv.presentation.view.fragment.livefragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.library.util.Device;
import com.qike.feiyunlu.tv.presentation.view.fragment.BaseFragment;

/**
 * Created by cherish on 2016/3/15.
 */
public class LiveMessFragment extends BaseFragment {


    private ImageView mMessageImg;
    private ImageView mAnnounceImg;
    private ImageView mNoticeImg;

    private TextView mVersionTV;

    @Override
    public void initView() {

        mMessageImg = (ImageView)findViewById(R.id.message);
        mAnnounceImg = (ImageView)findViewById(R.id.announce);
        mNoticeImg = (ImageView)findViewById(R.id.notice);

        mVersionTV = (TextView)findViewById(R.id.version);


    }

    @Override
    public void initData() {
        mVersionTV.setText(Device.getVersion(getContext()));
    }

    @Override
    public void setListener() {
        mMessageImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startMessageActivity(getContext());
            }
        });
        mAnnounceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startAnnounceActivity(getContext());
            }
        });

        mNoticeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void loadData() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }
}
