package com.cfk.xiaov.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.db.model.UserInfo;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.response.GetUserInfoByIdResponse;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMeFgView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MeFgPresenter extends BasePresenter<IMeFgView> {

    String TAG = getClass().getSimpleName();

    private UserInfo mUserInfo;
    private boolean isFirst = true;

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        mUserInfo = DBManager.getInstance().getUserInfo(AccountCache.getAccount());
        if (mUserInfo == null || isFirst) {
            isFirst = false;
            ApiRetrofit.getInstance().getUserInfoById(AccountCache.getAccount())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUserInfoByIdResponse -> {
                        if (getUserInfoByIdResponse != null && getUserInfoByIdResponse.getCode() == 200) {
                            GetUserInfoByIdResponse.ResultEntity result = getUserInfoByIdResponse.getResult();

                            mUserInfo = new UserInfo(AccountCache.getAccount(), result.getNickname(), result.getPortraitUri());
                            if (TextUtils.isEmpty(mUserInfo.getPortraitUri())) {
                                mUserInfo.setPortraitUri(DBManager.getInstance().getPortraitUri(mUserInfo));
                            }

                            DBManager.getInstance().saveOrUpdateFriend(new Friend(mUserInfo.getUserId(), mUserInfo.getName(), mUserInfo.getPortraitUri()));
                            fillView();
                        }
                    }, this::loadError);
        } else {
            fillView();
        }
    }

    public void refreshUserInfo() {
        UserInfo userInfo = DBManager.getInstance().getUserInfo(AccountCache.getAccount());
        if (userInfo == null) {
            loadUserInfo();
        } else {
            mUserInfo = userInfo;
        }
    }

    public void fillView() {
        if (mUserInfo != null) {
            if(mUserInfo.getPortraitUri().startsWith("file://")) {
                Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
            }else {
                ApiRetrofit.getInstance().getQiNiuDownloadUrl(mUserInfo.getPortraitUri())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(qiNiuDownloadResponse -> {
                            if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                                String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                                Glide.with(mContext).load(pic).centerCrop().into(getView().getIvHeader());
                            }
                        });

            }
            Log.i(TAG,mUserInfo.getPortraitUri());
            getView().getTvAccount().setText(UIUtils.getString(com.cfk.xiaov.R.string.my_chat_account)+mUserInfo.getId());
            getView().getTvName().setText(mUserInfo.getName());
        }
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
