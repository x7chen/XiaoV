package com.cfk.xiaov.ui.presenter;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.model.UserInfo;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.model.exception.ServerException;
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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    String TAG = getClass().getSimpleName();

    private UploadManager mUploadManager;
    public UserInfo mUserInfo;

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        getView().getOivAccount().setRightText(AccountCache.getAccount());
        getView().getOivName().setRightText(MyInfoCache.getNickName());
        Glide.with(mContext).load(MyInfoCache.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(getView().getIvHeader()) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });
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
                        String token = result.getToken();
                        // 第二步 上传到七牛
                        mUploadManager.put(imageFile, null, token, (key, info, response) -> {
                            if (info.isOK()) {
                                String key1 = response.optString("key");
                                String imageUrl = key1;
                                ApiRetrofit.getInstance().setPortrait(imageUrl)
                                        .flatMap(setPortraitResponse -> {
                                            if (setPortraitResponse != null && setPortraitResponse.getCode() == 200) {
                                                //第五步 加载头像
                                                return ApiRetrofit.getInstance().getQiNiuDownloadUrl(imageUrl);
                                            } else {
                                                return Observable.error(new ServerException((UIUtils.getString(R.string.change_fail))));
                                            }
                                        })
                                        .flatMap(qiNiuDownloadResponse -> {
                                            if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                                                return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                                            } else {
                                                return Observable.error(new ServerException((UIUtils.getString(R.string.change_fail))));
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(ResponseBody -> {
                                            MyInfoCache.setAvatarUri(writeResponseBodyToDisk(ResponseBody));
                                            ImageView mPhoto = getView().getIvHeader();
                                            Glide.with(mContext).load(MyInfoCache.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mPhoto) {
                                                @Override
                                                protected void setResource(Bitmap resource) {
                                                    RoundedBitmapDrawable circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                                    circularBitmapDrawable.setCircular(true);
                                                    mPhoto.setImageDrawable(circularBitmapDrawable);
                                                }
                                            });
                                            mContext.hideWaitingDialog();
                                            BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
//                                            BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
//                                            BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_GROUP);
                                            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_success));
                                        }, this::uploadError);
                            }
                        }, null);
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
