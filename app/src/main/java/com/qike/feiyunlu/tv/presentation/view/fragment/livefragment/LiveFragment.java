package com.qike.feiyunlu.tv.presentation.view.fragment.livefragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.module.network.DLResultData;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;
import com.qike.feiyunlu.tv.presentation.presenter.Service.FloatService;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.room.RoomPresenter;
import com.qike.feiyunlu.tv.presentation.view.OnlineLiveSettingActivity;
import com.qike.feiyunlu.tv.presentation.view.fragment.BaseFragment;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.LiveScreenDto;

/**
 * Created by cherish on 2016/3/15.
 */
public class LiveFragment extends BaseFragment {

    private RadioGroup sharpRadioGroup;
    private RadioGroup orientRadioGroup;

    private DrawerLayout mDrawerLayout;
    private Button mStartButton;
    private RelativeLayout mTagLayout;
    private TextView mTagText;
    private LiveScreenDto mLiveDto;

    private RoomPresenter mRoomPresenter;

    private User mUser;


    @Override
    public void initView() {
        mLiveDto = new LiveScreenDto();

//        String rtmp_uri = "rtmp://publish.huizu100.com/g_live/songguangyu?key=d2b755c06e892172";
        mStartButton = (Button) findViewById(R.id.online_live_control_btn);
        orientRadioGroup = (RadioGroup) findViewById(R.id.orientation_radio_btn);
        sharpRadioGroup = (RadioGroup) findViewById(R.id.live_density_radio_btn);
        mTagText = (TextView) findViewById(R.id.live_tag_tv);

        mTagLayout = (RelativeLayout) findViewById(R.id.select_live_tag);

        initActivityView();

    }

    private void initDrawerLayout() {

        final TextView text1 = (TextView) getActivity().findViewById(R.id.text1);
        final TextView text2 = (TextView) getActivity().findViewById(R.id.text2);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
                mTagText.setText(text1.getText().toString());
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
                mTagText.setText(text2.getText().toString());
            }
        });
    }

    public void initActivityView(){
        initDrawerLayout();
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerlayout);
    }


    @Override
    public void initData() {

        mRoomPresenter = new RoomPresenter();
    }


    @Override
    public void setListener() {


        mTagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrawerLayout.openDrawer(GravityCompat.END);

            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startRecord(1);
                ActivityUtil.startMenuActivity(getContext());
            }

        });

        orientRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.horizontal_btn) {//横屏
                    mLiveDto.setOrientation(0);
                    if( mLiveDto.getHeight() > mLiveDto.getWidth()){
                        swap();
                    }

                } else if (checkedId == R.id.portrait_btn) {//竖屏
                    mLiveDto.setOrientation(1);
                    if( mLiveDto.getHeight() < mLiveDto.getWidth()){
                        swap();
                    }
                }

            }
        });

        sharpRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.normal_density:

                        mLiveDto.setBitrate(500000);
                        break;

                    case R.id.high_density:
                        mLiveDto.setBitrate(1000000);
                        break;

                    case R.id.super_density:
                        mLiveDto.setBitrate(2000000);

                        break;
                }
                /****************************************/
                WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Point point = new Point();
                windowManager.getDefaultDisplay().getRealSize(point);
                float f = ((float) point.y) / ((float) point.x);

                if (mLiveDto.getBitrate() == 1000000) {
                    mLiveDto.setHeight(480);
                } else if (mLiveDto.getBitrate() == 2000000) {
                    mLiveDto.setHeight(720);
                } else if (mLiveDto.getBitrate() == 500000) {
                    mLiveDto.setHeight(360);
                }
                int width = (int) (f * ((float) mLiveDto.getHeight()));
                if (width % 4 != 0) {
                    width -= width % 4;
                }
                mLiveDto.setWidth(width);

                if( mLiveDto.getOrientation() == 1 && mLiveDto.getWidth()>mLiveDto.getHeight()){
                    swap();
                }

            }
        });

        orientRadioGroup.check(R.id.horizontal_btn);
        sharpRadioGroup.check(R.id.high_density);

    }


    private void swap(){
        int temp = mLiveDto.getHeight();
        mLiveDto.setHeight(mLiveDto.getWidth());
        mLiveDto.setWidth(temp);
    }

    @Override
    public void loadData() {
        mUser = AccountManager.getInstance(getContext()).getUser();
        if( mUser != null){

            mRoomPresenter.getRoomUrl(mUser.getUser_id(), mUser.getUser_verify(), new BaseCallbackPresenter() {
                @Override
                public boolean callbackResult(Object obj) {

                    if (obj != null && obj instanceof String) {
                        String publishurl = (String) obj;
                        Log.e("test", publishurl);
                        mLiveDto.setRtmp_url(publishurl);
                    }

                    return false;
                }

                @Override
                public void onErrer(int code, String msg) {

                }
            });

        }


    }



    private void startRecord(int i) {

        Intent intent = new Intent(getContext(), FloatService.class);
        intent.putExtra("liveDto", mLiveDto);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startService(intent);
    }


    @Override
    public void onResume() {
        super.onResume();

        mUser = AccountManager.getInstance(getContext()).getUser();

    }

    @Override
    public void onDestroy() {

        mRoomPresenter.closeRoom(mUser.getUser_id(), mUser.getUser_verify(), new BaseCallbackPresenter() {
            @Override
            public boolean callbackResult(Object obj) {
                if (obj != null && obj instanceof DLResultData) {
                    DLResultData dlResultData = (DLResultData) obj;
                    if (dlResultData.getCode() == 200) {
                        Log.i(OnlineLiveSettingActivity.class.getName(), "关闭房间");
                    }
                }

                return false;
            }

            @Override
            public void onErrer(int code, String msg) {

            }
        });
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_online_live_info_layout;
    }


}
