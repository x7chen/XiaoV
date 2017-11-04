package com.cfk.xiaov.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class LoginByPhoneActivity extends BaseActivity {

    String TAG = getClass().getSimpleName();
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etVerifyCode)
    EditText etVerifyCode;
    @BindView(R.id.btnGetCode)
    Button btnGetCode;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvLoginByNormal)
    TextView tvLoginByNormal;


    EventHandler eventHandler;

    @Override
    public void initView() {
        super.initView();
        btnLogin.setEnabled(true);
    }

    @Override
    public void init() {
        super.init();
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        // SMSSDK.setAskPermisionOnReadContact(true);

        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    UIUtils.showToastSafely(msg);
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 处理你自己的逻辑
                        UIUtils.showToastSafely("已发送");

                    }
                }
            }
        };

        // 短信验证 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    void countdown() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int time = 60;

            @Override
            public void run() {
                runOnUiThread(() -> btnGetCode.setText(String.valueOf(time) + " s"));
                time--;
                if (time == 0) {
                    runOnUiThread(() -> {
                        btnGetCode.setEnabled(true);
                        btnGetCode.setText(R.string.btn_get_vertify);
                    });
                    cancel();
                }
            }
        }, 100, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public void initListener() {
        final String[] account = new String[1];
        btnGetCode.setOnClickListener((View view) -> {
            try {
                SMSSDK.getVerificationCode("86", etPhone.getText().toString());
                 btnGetCode.setEnabled(false);
                countdown();
            }catch (Exception e){

            }
        });

        btnLogin.setOnClickListener(view -> {
            btnLogin.setEnabled(false);
            ApiRetrofit.getInstance().sendVerifyCode(etPhone.getText().toString(), etVerifyCode.getText().toString())
                    .flatMap(verifyCodeResponse -> {
                        int code = verifyCodeResponse.getCode();
                        Log.i(TAG, "code:" + code);
                        if (code == 200)
                            return ApiRetrofit.getInstance().login_by_phone("86", etPhone.getText().toString());
                        else {
                            return Observable.error(new ServerException((UIUtils.getString(R.string.login_error))));
                        }
                    })
                    .flatMap(loginResponse -> {
                        int code = loginResponse.getCode();
                        Log.i(TAG, "code:" + code);
                        if (code == 200) {
                            account[0] = loginResponse.getResult().getId();
                            // 1. 获取用户信息
                            AccountCache.save(account[0], loginResponse.getResult().getToken(), null);
                            return ApiRetrofit.getInstance().getUserInfoById(account[0]);
                        } else {
                            return Observable.error(new ServerException((UIUtils.getString(R.string.login_error) + code)));
                        }
                    })
                    .flatMap(getUserInfoByIdResponse -> {
                        int code = getUserInfoByIdResponse.getCode();
                        if (code == 200) {
                            MyInfoCache.setAccount(account[0]);
                            MyInfoCache.setNickName(getUserInfoByIdResponse.getResult().getNickname());
                            // 2. 获取头像链接
                            return ApiRetrofit.getInstance().getQiNiuDownloadUrl(getUserInfoByIdResponse.getResult().getPortraitUri());
                        } else {
                            return Observable.error(new ServerException((UIUtils.getString(R.string.login_error) + code)));
                        }
                    })
                    .flatMap(qiNiuDownloadResponse -> {
                        int code = qiNiuDownloadResponse.getCode();
                        if (code == 200) {
                            // 下载头像
                            return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                        } else {
                            return Observable.error(new ServerException((UIUtils.getString(R.string.login_error) + code)));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        hideWaitingDialog();
                        MyInfoCache.setAvatarUri(writeResponseBodyToDisk(responseBody));
                        sendBroadcast(new Intent(AppConst.CHANGE_INFO_FOR_ME));
                        MyApp.mAccountMgr.loginSDK(account[0], AccountCache.getUserSig());
                        jumpToActivityAndClearTask(MainActivity.class);
                        finish();
                    }, this::loginError);
        });


        tvLoginByNormal.setOnClickListener(view -> {
            jumpToActivity(LoginActivity.class);
            finish();
        });
    }

    private void loginError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        BroadcastUtils.sendBroadcast(AppConst.NET_STATUS, "net_status", "failed");
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_login_by_phone;
    }
}
