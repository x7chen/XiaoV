package com.cfk.xiaov.ui.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.GroupMember;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;

import java.util.List;

import butterknife.Bind;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QRCodeCardActivity extends BaseActivity {

    private UserInfo mUserInfo;
    private String mGroupId;

    @Bind(com.cfk.xiaov.R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(com.cfk.xiaov.R.id.ngiv)
    LQRNineGridImageView mNgiv;
    @Bind(com.cfk.xiaov.R.id.tvName)
    TextView mTvName;
    @Bind(com.cfk.xiaov.R.id.ivCard)
    ImageView mIvCard;
    @Bind(com.cfk.xiaov.R.id.tvTip)
    TextView mTvTip;

    @Override
    public void init() {
        mGroupId = getIntent().getStringExtra("groupId");
    }

    @Override
    public void initView() {
        mTvTip.setText(UIUtils.getString(com.cfk.xiaov.R.string.qr_code_card_tip));
    }

    public void initData() {
        if (TextUtils.isEmpty(mGroupId)) {
            mUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
            if (mUserInfo != null) {
                Glide.with(this).load(mUserInfo.getPortraitUri()).centerCrop().into(mIvHeader);
                mTvName.setText(mUserInfo.getName());
                setQRCode(AppConst.QrCodeCommon.ADD + mUserInfo.getUserId());
            }
        } else {
            mNgiv.setVisibility(View.VISIBLE);
            mIvHeader.setVisibility(View.GONE);
            Observable.just(DBManager.getInstance().getGroupsById(mGroupId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(groups -> {
                        if (groups == null)
                            return;
                        mTvName.setText(groups.getName());
                    });
            mNgiv.setAdapter(new LQRNineGridImageViewAdapter<GroupMember>() {
                @Override
                protected void onDisplayImage(Context context, ImageView imageView, GroupMember groupMember) {
                    Glide.with(context).load(groupMember.getPortraitUri()).centerCrop().into(imageView);
                }
            });
            List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(mGroupId);
            mNgiv.setImagesData(groupMembers);
            setQRCode(AppConst.QrCodeCommon.JOIN + mGroupId);
            mTvTip.setVisibility(View.GONE);
        }
    }

    private void setQRCode(String content) {
        Observable.just(QRCodeEncoder.syncEncodeQRCode(content, UIUtils.dip2Px(100)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> mIvCard.setImageBitmap(bitmap), this::loadQRCardError);
    }

    private void loadQRCardError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_qr_code_card;
    }
}
