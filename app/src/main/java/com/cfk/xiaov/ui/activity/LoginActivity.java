package com.cfk.xiaov.ui.activity;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    @Bind(com.cfk.xiaov.R.id.etPhone)
    EditText mEtPhone;
    @Bind(com.cfk.xiaov.R.id.vLinePhone)
    View mVLinePhone;

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
        mEtPhone.addTextChangedListener(watcher);
        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.green0));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.line));
            }
        });
        mEtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.green0));
            } else {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.line));
            }
        });

        mBtnLogin.setOnClickListener(v -> mPresenter.login());
    }

    private boolean canLogin() {
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtPhone.getText().toString().trim().length();
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
    public EditText getEtPhone() {
        return mEtPhone;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }
}