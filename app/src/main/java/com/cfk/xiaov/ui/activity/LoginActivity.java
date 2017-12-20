package com.cfk.xiaov.ui.activity;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.LoginAtPresenter;
import com.cfk.xiaov.ui.view.ILoginAtView;
import com.cfk.xiaov.util.UIUtils;

import butterknife.BindView;

/**
 * @创建者 Sean
 * @描述 登录界面
 */
public class LoginActivity extends BaseActivity<ILoginAtView, LoginAtPresenter> implements ILoginAtView {

    @BindView(R.id.ibAddMenu)
    ImageButton mIbAddMenu;

    @BindView(R.id.etUserId)
    EditText mEtUserId;
    @BindView(R.id.vLineUserId)
    View mVLineUserId;

    @BindView(R.id.etPwd)
    EditText mEtPwd;
    @BindView(R.id.vLinePwd)
    View mVLinePwd;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    @BindView(R.id.tvLoginByPhone)
    TextView tvLoginByPhone;

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
            mPresenter.login();
        });
        tvLoginByPhone.setOnClickListener(v -> {
            jumpToActivity(LoginByPhoneActivity.class);
            finish();
        });
    }


    private boolean canLogin() {
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtUserId.getText().toString().trim().length();
        return pwdLength > 0 && phoneLength > 0;
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