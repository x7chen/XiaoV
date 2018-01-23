package com.cfk.xiaov.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.rest.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.sp.MyInfoCache;
import com.cfk.xiaov.misc.exception.ServerException;
import com.cfk.xiaov.rest.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.optionitemview.OptionItemView;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;


/**
 * @创建者 Sean
 * @描述 我的个人信息
 */
public class MyInfoActivity extends BaseActivity {

    public static final int REQUEST_IMAGE_PICKER = 1000;

    @BindView(R.id.llHeader)
    LinearLayout mLlHeader;
    @BindView(R.id.ivHeader)
    ImageView mIvHeader;
    @BindView(R.id.oivName)
    OptionItemView mOivName;
    @BindView(R.id.oivAccount)
    OptionItemView mOivAccount;
    @BindView(R.id.oivQRCodeCard)
    OptionItemView mOivQRCodeCard;

    private UploadManager mUploadManager;


    @Override
    public void init() {
        super.init();
        registerBR();
    }

    @Override
    public void initData() {
        loadUserInfo();
    }

    @Override
    public void initListener() {
        mIvHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfoActivity.this, ShowBigImageActivity.class);
            jumpToActivity(intent);
        });
        mLlHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        });
        mOivQRCodeCard.setOnClickListener(v -> jumpToActivity(QRCodeCardActivity.class));
        mOivName.setOnClickListener(v -> jumpToActivity(ChangeMyNameActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            ImageItem imageItem = images.get(0);
                            setPortrait(imageItem);
                        }
                    }
                }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadUserInfo();
        }
    };

    private void registerBR() {
        registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.CHANGE_INFO_FOR_CHANGE_NAME));

    }

    private void unregisterBR() {
        unregisterReceiver(broadcastReceiver);
    }

    public void loadUserInfo() {
        mOivAccount.setRightText(AccountCache.getAccount());
        mOivName.setRightText(MyInfoCache.getNickName());
        Glide.with(this).load(MyInfoCache.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mIvHeader) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void setPortrait(ImageItem imageItem) {
        showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));

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
                                            Glide.with(this).load(MyInfoCache.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mIvHeader) {
                                                @Override
                                                protected void setResource(Bitmap resource) {
                                                    RoundedBitmapDrawable circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                    circularBitmapDrawable.setCircular(true);
                                                    view.setImageDrawable(circularBitmapDrawable);
                                                }
                                            });
                                            hideWaitingDialog();
                                            sendBroadcast(new Intent(AppConstants.Action.CHANGE_INFO_FOR_ME));
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
        hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_fail));
    }


    @Override
    protected int provideContentViewId() {
        return R.layout.activity_my_info;
    }

}
