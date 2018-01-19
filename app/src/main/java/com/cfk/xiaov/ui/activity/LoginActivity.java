package com.cfk.xiaov.ui.activity;


import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

/**
 * @创建者 Sean
 * @描述 登录界面
 */
public class LoginActivity extends BaseActivity {

    String TAG = getClass().getSimpleName();

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
            login();
        });
        tvLoginByPhone.setOnClickListener(v -> {
            jumpToActivity(LoginByPhoneActivity.class);
            finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }


    private boolean canLogin() {
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtUserId.getText().toString().trim().length();
        return pwdLength > 0 && phoneLength > 0;
    }

    public void login() {
        String userId = mEtUserId.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(userId)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            UIUtils.showToast(UIUtils.getString(R.string.password_not_empty));
            return;
        }

        showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().login(AppConstants.REGION, userId, pwd)
                .flatMap(loginResponse -> {
                    int code = loginResponse.getCode();
                    Log.i(TAG, "code:" + code);
                    if (code == 200) {
                        // 1. 获取用户信息
                        AccountCache.save(userId, loginResponse.getResult().getToken(), pwd);
                        return ApiRetrofit.getInstance().getUserInfoById(userId);
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown) + code)));
                    }
                })
                .flatMap(getUserInfoByIdResponse -> {
                    int code = getUserInfoByIdResponse.getCode();
                    if (code == 200) {
                        MyInfoCache.setAccount(userId);
                        MyInfoCache.setNickName(getUserInfoByIdResponse.getResult().getNickname());
                        // 2. 获取头像链接
                        return ApiRetrofit.getInstance().getQiNiuDownloadUrl(getUserInfoByIdResponse.getResult().getPortraitUri());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown) + code)));
                    }
                })
                .flatMap(qiNiuDownloadResponse -> {
                    int code = qiNiuDownloadResponse.getCode();
                    if (code == 200) {
                        // 下载头像
                        return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown) + code)));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    hideWaitingDialog();
                    MyInfoCache.setAvatarUri(writeResponseBodyToDisk(responseBody));
                    sendBroadcast(new Intent(AppConstants.Action.CHANGE_INFO_FOR_ME));
                    jumpToActivityAndClearTop(MainActivity.class);
                }, this::loginError);
    }

    private void loginError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        BroadcastUtils.sendBroadcast(AppConstants.Action.NET_STATUS, "net_status", "failed");
        hideWaitingDialog();
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_login;
    }
}