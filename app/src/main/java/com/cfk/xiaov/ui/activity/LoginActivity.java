package com.cfk.xiaov.ui.activity;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.LoginAtPresenter;
import com.cfk.xiaov.ui.view.ILoginAtView;
import com.cfk.xiaov.util.UIUtils;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 登录界面
 */
public class LoginActivity extends BaseActivity<ILoginAtView, LoginAtPresenter> implements ILoginAtView {

    @Bind(com.cfk.xiaov.R.id.ibAddMenu)
    ImageButton mIbAddMenu;

    @Bind(com.cfk.xiaov.R.id.etUserId)
    EditText mEtUserId;
    @Bind(com.cfk.xiaov.R.id.vLineUserId)
    View mVLineUserId;

    @Bind(com.cfk.xiaov.R.id.etPwd)
    EditText mEtPwd;
    @Bind(com.cfk.xiaov.R.id.vLinePwd)
    View mVLinePwd;

    @Bind(com.cfk.xiaov.R.id.tvProblems)
    TextView mTvProblems;
    @Bind(com.cfk.xiaov.R.id.btnLogin)
    Button mBtnLogin;
    @Bind(com.cfk.xiaov.R.id.tvOtherLogin)
    TextView mTvOtherLogin;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnLogin.setEnabled(canLogin());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initView() {
        mIbAddMenu.setVisibility(View.GONE);
    }

    @Override
    public void initListener() {
        mEtPwd.addTextChangedListener(watcher);
        mEtUserId.addTextChangedListener(watcher);
        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.line));
            }
        });
        mEtUserId.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineUserId.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.colorPrimary));
            } else {
                mVLineUserId.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.line));
            }
        });

        mBtnLogin.setOnClickListener(v -> {
//            new Thread(() -> {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MyApp.mAccountMgr.t_login(prefix+mEtUserId.getText().toString().trim(),mEtPwd.getText().toString().trim());
//            }).start();
            mPresenter.login();



        });
    }


    private boolean canLogin() {
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtUserId.getText().toString().trim().length();
        if (pwdLength > 0 && phoneLength > 0) {
            return true;
        }
        return false;
    }


    @Override
    protected LoginAtPresenter createPresenter() {
        return new LoginAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_login;
    }

    @Override
    public EditText getEtUserId() {
        return mEtUserId;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }
}