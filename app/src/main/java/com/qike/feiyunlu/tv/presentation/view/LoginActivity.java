package com.qike.feiyunlu.tv.presentation.view;



import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.qike.feiyunlu.tv.R;
import com.qike.feiyunlu.tv.library.util.ActivityUtil;
import com.qike.feiyunlu.tv.presentation.model.dto.User;
import com.qike.feiyunlu.tv.presentation.presenter.IAccountPresenterCallBack;
import com.qike.feiyunlu.tv.presentation.presenter.account.AccountManager;


public class LoginActivity extends BaseActivity implements IViewOperater{

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Button mLoginButton;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        initView();
        initData();
        setListener();
        loadData();

    }

    @Override
    public void initView() {
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (AutoCompleteTextView) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailStr = mEmailView.getText().toString();
                String mPassStr = mPasswordView.getText().toString();

                if (TextUtils.isEmpty(emailStr) || TextUtils.isEmpty(mPassStr)) {
                    Toast.makeText(LoginActivity.this, "请填写用户名和密码！", 0).show();
                } else {
                    AccountManager.getInstance(LoginActivity.this).login(emailStr, mPassStr, loginCallback);
                }
            }
        });
    }

    @Override
    public void loadData() {

    }

    private IAccountPresenterCallBack loginCallback = new IAccountPresenterCallBack() {

        @Override
        public void onErrer(int code, String msg) {
            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean callbackResult(Object obj) {

            if (obj != null && obj instanceof User) {
                finish();
                ActivityUtil.startOnLineLiveSettingActivity( LoginActivity.this);
            }
            return false;
        }
        @Override
        public void callBackStats(int status) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mUser = AccountManager.getInstance(LoginActivity.this).getUser();
        if (mUser!= null){
            ActivityUtil.startOnLineLiveSettingActivity(LoginActivity.this);
            finish();

        }

    }
}

