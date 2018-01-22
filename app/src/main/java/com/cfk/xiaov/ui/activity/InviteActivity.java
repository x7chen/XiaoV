package com.cfk.xiaov.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.model.db.BondDevice;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.model.request.PushRequest;
import com.cfk.xiaov.model.response.GetUserInfoResponse;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class InviteActivity extends BaseActivity {

    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivHeader)
    ImageView ivHeader;
    @BindView(R.id.btInvite)
    Button btInvite;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    String mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initView() {
        mAccount = getIntent().getStringExtra("account");
        //获取用户信息
        String[] nickname = new String[1];
        ApiRetrofit.getInstance().getUserInfoById(mAccount)
                .flatMap(getUserInfoResponse -> {
                    if (getUserInfoResponse != null && getUserInfoResponse.getCode() == 200) {
                        GetUserInfoResponse.ResultEntity res = getUserInfoResponse.getResult();
                        nickname[0] = res.getNickname();
                        return ApiRetrofit.getInstance().getQiNiuDownloadUrl(res.getPortraitUri());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown))));
                    }
                })
                .flatMap(qiNiuDownloadResponse -> {
                    if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                        return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown))));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ResponseBody -> {
                    BondDevice device = new BondDevice(mAccount, nickname[0], writeResponseBodyToDisk(ResponseBody));
                    Glide.with(this).load(device.getAvatarUri()).centerCrop().into(ivHeader);
                    tvAccount.setText(device.getAccount());
                    tvName.setText(device.getNickname());
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public void initListener() {
        btInvite.setOnClickListener(v -> {
            PushRequest.Extra extra = new PushRequest.Extra();
            extra.setMessage(tvMessage.getText().toString());
            ApiRetrofit.getInstance().push(AppConstants.PUSH_METHOD.INVITE, mAccount, extra)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pushResponse -> {
                        btInvite.setEnabled(false);
                        tvMessage.setEnabled(false);
                        finish();
                    }, this::rxError);
        });
    }


    @Override
    protected int provideContentViewId() {
        return R.layout.activity_invite;
    }

    private void rxError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }
}
