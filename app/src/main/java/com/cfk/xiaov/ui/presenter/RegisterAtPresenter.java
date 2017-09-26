package com.cfk.xiaov.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.response.LoginResponse;
import com.cfk.xiaov.model.response.RegisterResponse;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.view.IRegisterAtView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.ui.base.BasePresenter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.phone_not_empty));
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


        ApiRetrofit.getInstance().register(nickName, userId, password)
                .flatMap(new Func1<RegisterResponse, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> call(RegisterResponse registerResponse) {
                        int code = registerResponse.getCode();
                        if (code == 200) {
                            Log.i(TAG,"hello");
                            return ApiRetrofit.getInstance().login(AppConst.REGION, userId, password);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(com.cfk.xiaov.R.string.register_error) + code));
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    int responseCode = loginResponse.getCode();
                    if (responseCode == 200) {
                        AccountCache.save(userId,loginResponse.getResult().getToken());
                        mContext.jumpToActivityAndClearTask(MainActivity.class);
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.login_error));

                    }
                },this::registerError);
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
