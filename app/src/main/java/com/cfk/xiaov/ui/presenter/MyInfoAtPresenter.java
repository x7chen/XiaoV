package com.cfk.xiaov.ui.presenter;

import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.db.model.UserInfo;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMyInfoAtView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.imagepicker.bean.ImageItem;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    String TAG = getClass().getSimpleName();

    private UploadManager mUploadManager;
    public UserInfo mUserInfo;

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        Friend friend = DBManager.getInstance().getFriendById(AccountCache.getAccount());
        mUserInfo = new UserInfo(friend.getUserId(),friend.getName(),friend.getPortraitUri());
        getView().getOivAccount().setRightText(AccountCache.getAccount());
        getView().getOivName().setRightText(friend.getName());

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
//        ApiRetrofit.getInstance().getQiNiuDownloadUrl(friend.getPortraitUri())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(qiNiuDownloadResponse -> {
//                    if(qiNiuDownloadResponse !=null&&qiNiuDownloadResponse.getCode()==200){
//                        String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
//                        Glide.with(mContext).load(pic).centerCrop().into(getView().getIvHeader());
//                    }
//                });
    }

    public void setPortrait(ImageItem imageItem) {
        mContext.showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));

        //第一步 获取七牛token
        ApiRetrofit.getInstance().getQiNiuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiNiuTokenResponse -> {
                    if (qiNiuTokenResponse != null && qiNiuTokenResponse.getCode() == 200) {
                        if (mUploadManager == null) {
                            Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
                            mUploadManager = new UploadManager(config);
                        }
                        File imageFile = new File(imageItem.path);
                        QiNiuTokenResponse.ResultEntity result = qiNiuTokenResponse.getResult();
                        String domain = result.getDomain();
                        String token = result.getToken();
                        // 第二步 上传到七牛
                        mUploadManager.put(imageFile, null, token, (s, responseInfo, jsonObject) -> {
                            if (responseInfo.isOK()) {
                                String key = jsonObject.optString("key");
                                String imageUrl = key;
                                Log.i(TAG,"imageUrl:"+imageUrl);
                                // 第三步 修改自己服务器头像链接
                                ApiRetrofit.getInstance().setPortrait(imageUrl)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        // 第四步 设置本地数据库头像链接
                                        .subscribe(setPortraitResponse -> {
                                            if (setPortraitResponse != null && setPortraitResponse.getCode() == 200) {
                                                Friend friend = DBManager.getInstance().getFriendById(AccountCache.getAccount());
                                                if (friend != null) {
                                                    friend.setPortraitUri(imageUrl);
                                                    DBManager.getInstance().saveOrUpdateFriend(friend);
                                                    DBManager.getInstance().updateGroupMemberPortraitUri(AccountCache.getAccount(), imageUrl);
                                                    ApiRetrofit.getInstance().getQiNiuDownloadUrl(imageUrl)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(qiNiuDownloadResponse -> {
                                                                if(qiNiuDownloadResponse !=null&&qiNiuDownloadResponse.getCode()==200){
                                                                 String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                                                                    Glide.with(mContext).load(pic).centerCrop().into(getView().getIvHeader());
                                                                }
                                                            },this::uploadError);
//                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
//                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
//                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_GROUP);
                                                    UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_success));
                                                }
                                                mContext.hideWaitingDialog();
                                            } else {
                                                uploadError(null);
                                            }
                                        }, this::uploadError);
                            } else {
                                Log.i(TAG,"responseInfo:"+responseInfo.error);
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
