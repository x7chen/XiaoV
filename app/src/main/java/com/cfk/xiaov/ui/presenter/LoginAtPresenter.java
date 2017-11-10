package com.cfk.xiaov.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.ILoginAtView;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class LoginAtPresenter extends BasePresenter<ILoginAtView> {
    String TAG = getClass().getSimpleName();

    public LoginAtPresenter(BaseActivity context) {
        super(context);
    }

    public void login() {
        String userId = getView().getEtUserId().getText().toString().trim();
        String pwd = getView().getEtPwd().getText().toString().trim();

        if (TextUtils.isEmpty(userId)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            UIUtils.showToast(UIUtils.getString(R.string.password_not_empty));
            return;
        }

        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().login(AppConst.REGION, userId, pwd)
                .flatMap(loginResponse -> {
                    int code = loginResponse.getCode();
                    Log.i(TAG, "code:" + code);
                    if (code == 200) {
                        // 1. 获取用户信息
                        AccountCache.save(userId, loginResponse.getResult().getToken(), pwd);
                        return ApiRetrofit.getInstance().getUserInfoById(userId);
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.login_error) + code)));
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
                    mContext.hideWaitingDialog();
                    MyInfoCache.setAvatarUri(writeResponseBodyToDisk(responseBody));
                    mContext.sendBroadcast(new Intent(AppConst.CHANGE_INFO_FOR_ME));
                    mContext.jumpToActivityAndClearTask(MainActivity.class);
                    mContext.finish();
                }, this::loginError);
    }

    private void loginError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        BroadcastUtils.sendBroadcast(AppConst.NET_STATUS, "net_status", "failed");
        mContext.hideWaitingDialog();
    }
}
