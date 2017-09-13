package com.cfk.xiaov.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.RegisterAtPresenter;
import com.cfk.xiaov.ui.view.IRegisterAtView;
import com.cfk.xiaov.util.UIUtils;

import butterknife.Bind;

public class RegisterActivity extends BaseActivity<IRegisterAtView, RegisterAtPresenter> implements IRegisterAtView {

    @Bind(R.id.etNick)
    EditText mEtNick;
    @Bind(R.id.vLineNick)
    View mVLineNick;

    @Bind(R.id.etUserId)
    EditText mEtUserId;
    @Bind(R.id.vLineUserId)
    View mVLineUserId;

    @Bind(R.id.etPwd)
    EditText mEtPwd;
    @Bind(R.id.ivSeePwd)
    ImageView mIvSeePwd;
    @Bind(R.id.vLinePwd)
    View mVLinePwd;
    @Bind(R.id.btnRegister)
    Button mBtnRegister;

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnRegister.setEnabled(canRegister());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initView() {
        super.initView();
        mBtnRegister.setEnabled(false);
    }

    @Override
    public void initListener() {
        mEtNick.addTextChangedListener(watcher);
        mEtPwd.addTextChangedListener(watcher);
        mEtUserId.addTextChangedListener(watcher);

        mEtNick.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtUserId.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineUserId.setBackgroundColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                mVLineUserId.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });

        mIvSeePwd.setOnClickListener(v -> {

            if (mEtPwd.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }

            mEtPwd.setSelection(mEtPwd.getText().toString().trim().length());
        });

        mBtnRegister.setOnClickListener(v -> {
            mPresenter.register();
            if(MyApp.mAccountMgr!=null)
            MyApp.mAccountMgr.t_regist(mEtNick.getText().toString().trim(),mEtUserId.getText().toString().trim(),mEtPwd.getText().toString().trim());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    private boolean canRegister() {
        String nickName = mEtNick.getText().toString().trim();
        int nickNameLength = nickName.length();
        String passWd = mEtPwd.getText().toString().trim();
        int pwdLength = passWd.length();
        String userId = mEtUserId.getText().toString().trim();
        int userIdLength = userId.length();
        if (nickNameLength > 0 && pwdLength > 0 && userIdLength > 0)
        if(userId.matches("^[a-zA-Z][a-zA-Z0-9_]{5,15}$"))
        if(passWd.matches("^[a-zA-Z0-9_.]{8,18}$")){
            return true;
        }
        return false;
    }


    @Override
    protected RegisterAtPresenter createPresenter() {
        return new RegisterAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    public EditText getEtNickName() {
        return mEtNick;
    }

    @Override
    public EditText getEtUserID() {
        return mEtUserId;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }

}
