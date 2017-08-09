package com.cfk.xiaov.ui.presenter;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.imagepicker.bean.ImageItem;
import com.cfk.xiaov.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.ui.view.IMyInfoAtView;
import com.qiniu.android.storage.UploadManager;

import java.io.File;

import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    public UserInfo mUserInfo;
    private UploadManager mUploadManager;

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        mUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (mUserInfo != null) {
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
            getView().getOivName().setRightText(mUserInfo.getName());
            getView().getOivAccount().setRightText(mUserInfo.getUserId());
        }
    }

    public void setPortrait(ImageItem imageItem) {
        mContext.showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));
        //上传头像
        ApiRetrofit.getInstance().getQiNiuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiNiuTokenResponse -> {
                    if (qiNiuTokenResponse != null && qiNiuTokenResponse.getCode() == 200) {
                        if (mUploadManager == null)
                            mUploadManager = new UploadManager();
                        File imageFile = new File(imageItem.path);
                        QiNiuTokenResponse.ResultEntity result = qiNiuTokenResponse.getResult();
                        String domain = result.getDomain();
                        String token = result.getToken();
                        //上传到七牛
                        mUploadManager.put(imageFile, null, token, (s, responseInfo, jsonObject) -> {
                            if (responseInfo.isOK()) {
                                String key = jsonObject.optString("key");
                                String imageUrl = "http://" + domain + "/" + key;
                                //修改自己服务器头像数据
                                ApiRetrofit.getInstance().setPortrait(imageUrl)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(setPortraitResponse -> {
                                            if (setPortraitResponse != null && setPortraitResponse.getCode() == 200) {
                                                Friend friend = DBManager.getInstance().getFriendById(UserCache.getId());
                                                if (friend != null) {
                                                    friend.setPortraitUri(imageUrl);
                                                    DBManager.getInstance().saveOrUpdateFriend(friend);
                                                    DBManager.getInstance().updateGroupMemberPortraitUri(UserCache.getId(), imageUrl);
                                                    Glide.with(mContext).load(friend.getPortraitUri()).centerCrop().into(getView().getIvHeader());
                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_GROUP);
                                                    UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_success));
                                                }
                                                mContext.hideWaitingDialog();
                                            } else {
                                                uploadError(null);
                                            }
                                        }, this::uploadError);
                            } else {
                                uploadError(null);
                            }
                        }, null);
                    } else {
                        uploadError(null);
                    }
                }, this::uploadError);
    }

    private void uploadError(Throwable throwable) {
        if (throwable != null)
            LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_fail));
    }
}
