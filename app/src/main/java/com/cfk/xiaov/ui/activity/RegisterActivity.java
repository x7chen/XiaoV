package com.cfk.xiaov.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.rest.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.sp.MyInfoCache;
import com.cfk.xiaov.misc.exception.ServerException;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class RegisterActivity extends BaseActivity {

    String TAG = getClass().getSimpleName();

    private Subscription mSubscription;

    @BindView(R.id.etNick)
    EditText mEtNick;
    @BindView(R.id.vLineNick)
    View mVLineNick;

    @BindView(R.id.etUserId)
    EditText mEtUserId;
    @BindView(R.id.vLineUserId)
    View mVLineUserId;

    @BindView(R.id.etPwd)
    EditText mEtPwd;
    @BindView(R.id.ivSeePwd)
    ImageView mIvSeePwd;
    @BindView(R.id.vLinePwd)
    View mVLinePwd;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    @BindView(R.id.tvRegisterByPhone)
    TextView tvRegisterByPhone;

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
            register();
        });
        tvRegisterByPhone.setOnClickListener(view -> {
            jumpToActivity(RegisterByPhoneActivity.class);
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    private boolean canRegister() {
        String nickName = mEtNick.getText().toString().trim();
        int nickNameLength = nickName.length();
        String passWd = mEtPwd.getText().toString().trim();
        int pwdLength = passWd.length();
        String userId = mEtUserId.getText().toString().trim();
        int userIdLength = userId.length();
        if (nickNameLength > 0 && pwdLength > 0 && userIdLength > 0)
            if (userId.matches("^[a-zA-Z][a-zA-Z0-9_]{5,15}$"))
                if (passWd.matches("^[a-zA-Z0-9_.]{8,18}$")) {
                    return true;
                }
        return false;
    }

    public void register() {
        String userId = mEtUserId.getText().toString().trim();
        String password = mEtPwd.getText().toString().trim();
        String nickName = mEtNick.getText().toString().trim();

        if (TextUtils.isEmpty(userId)) {
            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.account_not_empty));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.password_not_empty));
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.nickname_not_empty));
            return;
        }


        ApiRetrofit.getInstance().register(AppConstants.REGION, nickName, userId, password)
                .flatMap(registerResponse -> {
                    int code = registerResponse.getCode();
                    if (code == 200) {
                        Log.i(TAG, "hello");
                        return ApiRetrofit.getInstance().login(AppConstants.REGION, userId, password);
                    } else {
                        return Observable.error(new ServerException(UIUtils.getString(R.string.register_error) + code));
                    }

                })
                .flatMap(loginResponse -> {
                    int code = loginResponse.getCode();
                    if (code == 200) {
                        AccountCache.save(loginResponse.getResult().getId(), loginResponse.getResult().getToken(), password);
                        return ApiRetrofit.getInstance().getUserInfoById(loginResponse.getResult().getId());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.load_error) + code)));
                    }
                })
                .flatMap(getUserInfoByIdResponse -> {
                    int code = getUserInfoByIdResponse.getCode();
                    if (code == 200) {
                        MyInfoCache.setAccount(getUserInfoByIdResponse.getResult().getId());
                        MyInfoCache.setNickName(getUserInfoByIdResponse.getResult().getNickname());
                        return ApiRetrofit.getInstance().getQiNiuDownloadUrl(getUserInfoByIdResponse.getResult().getPortraitUri());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.load_error) + code)));
                    }
                })
                .flatMap(qiNiuDownloadResponse -> {

                    int code = qiNiuDownloadResponse.getCode();
                    if (code == 200) {
                        return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.load_error) + code)));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    MyInfoCache.setAvatarUri(writeResponseBodyToDisk(responseBody));
                    sendBroadcast(new Intent(AppConstants.Action.CHANGE_INFO_FOR_ME));
                    jumpToActivityAndClearTask(MainActivity.class);
                }, this::registerError);
    }

    private void registerError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public void unsubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_register;
    }


}
