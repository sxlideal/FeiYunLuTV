package com.qike.feiyunlu.tv.presentation.view.fragment.livefragment;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.module.network.DLResultData;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.BaseCallbackPresenter;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;
import com.qike.feiyunlu.tv.presentation.presenter.room.RoomPresenter;
import com.qike.feiyunlu.tv.presentation.view.OnlineLiveSettingActivity;
import com.qike.feiyunlu.tv.presentation.view.fragment.BaseFragment;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.LiveScreenDto;
import com.qike.feiyunlu.tv.presentation.view.screenrecord.LiveUtil;

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

    private EditText mLiveTitleEdit;

    @Override
    public void initView() {
        mLiveDto = new LiveScreenDto();

//        String rtmp_uri = "rtmp://publish.huizu100.com/g_live/songguangyu?key=d2b755c06e892172";
        mStartButton = (Button) findViewById(R.id.online_live_control_btn);
        orientRadioGroup = (RadioGroup) findViewById(R.id.orientation_radio_btn);
        sharpRadioGroup = (RadioGroup) findViewById(R.id.live_density_radio_btn);
        mTagText = (TextView) findViewById(R.id.live_tag_tv);

        mTagLayout = (RelativeLayout) findViewById(R.id.select_live_tag);
        mLiveTitleEdit = (EditText)findViewById(R.id.live_title_edit);

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

    public void initActivityView() {
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
                LiveUtil.startRecord(1,getContext(),mLiveDto);


                String roomName = mLiveTitleEdit.getText().toString();
                String gameName = mTagText.getText().toString();
                if ( roomName == null || roomName.equals("")){
                    Toast.makeText(getContext(), R.string.live_title, 0).show();
                    return;
                }
                if (gameName == null || gameName.equals("")){
                    Toast.makeText(getContext(),R.string.live_tag,0).show();
                    return;
                }

                mRoomPresenter.setRoomSetting(getActivity(),mUser.getUser_id(), mUser.getUser_verify(),roomName
                        ,gameName ,
                        new BaseCallbackPresenter() {
                    @Override
                    public boolean callbackResult(Object obj) {

                        Toast.makeText(getContext(),"保存成功",0).show();

                        return false;
                    }

                    @Override
                    public void onErrer(int code, String msg) {
                        Toast.makeText(getContext(),"保存失败",0).show();

                    }
                });

//                ActivityUtil.startMenuActivity(getContext(),gameName);
            }

        });

        orientRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LiveUtil.chooseOrientation(checkedId,mLiveDto);
            }
        });

        sharpRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mLiveDto = LiveUtil.chooseSharpness(checkedId, mLiveDto, getContext());

            }
        });

        orientRadioGroup.check(R.id.horizontal_btn);
        sharpRadioGroup.check(R.id.high_density);

    }

    @Override
    public void loadData() {
        mUser = AccountManager.getInstance(getContext()).getUser();
        if (mUser != null) {

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
