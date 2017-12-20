package com.cfk.xiaov.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IRegisterAtView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class RegisterAtPresenter extends BasePresenter<IRegisterAtView> {
    String TAG = getClass().getSimpleName();

    private Subscription mSubscription;

    public RegisterAtPresenter(BaseActivity context) {
        super(context);
    }


    public void register() {
        String userId = getView().getEtUserID().getText().toString().trim();
        String password = getView().getEtPwd().getText().toString().trim();
        String nickName = getView().getEtNickName().getText().toString().trim();

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


        ApiRetrofit.getInstance().register(AppConst.REGION, nickName, userId, password)
                .flatMap(registerResponse -> {
                    int code = registerResponse.getCode();
                    if (code == 200) {
                        Log.i(TAG, "hello");
                        return ApiRetrofit.getInstance().login(AppConst.REGION, userId, password);
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
                    mContext.sendBroadcast(new Intent(AppConst.Action.CHANGE_INFO_FOR_ME));
                    mContext.finish();
                    mContext.jumpToActivityAndClearTask(MainActivity.class);
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

}
