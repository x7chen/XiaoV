package com.cfk.xiaov.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.ninegridimageview.LQRNineGridImageView;

import butterknife.Bind;

public class QRCodeCardActivity extends BaseActivity {


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
