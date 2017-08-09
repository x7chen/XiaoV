package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.MyInfoActivity;
import com.cfk.xiaov.ui.activity.SettingActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.presenter.MeFgPresenter;
import com.cfk.xiaov.ui.view.IMeFgView;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.widget.CustomDialog;
import com.lqr.optionitemview.OptionItemView;
import com.cfk.xiaov.util.LogUtils;

import butterknife.Bind;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 CSDN_LQR
 * @描述 我界面
 */
public class MeFragment extends BaseFragment<IMeFgView, MeFgPresenter> implements IMeFgView {

    private CustomDialog mQrCardDialog;

    @Bind(com.cfk.xiaov.R.id.llMyInfo)
    LinearLayout mLlMyInfo;
    @Bind(com.cfk.xiaov.R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(com.cfk.xiaov.R.id.tvName)
    TextView mTvName;
    @Bind(com.cfk.xiaov.R.id.tvAccount)
    TextView mTvAccount;
    @Bind(com.cfk.xiaov.R.id.ivQRCordCard)
    ImageView mIvQRCordCard;

    @Bind(com.cfk.xiaov.R.id.oivAlbum)
    OptionItemView mOivAlbum;
    @Bind(com.cfk.xiaov.R.id.oivCollection)
    OptionItemView mOivCollection;
    @Bind(com.cfk.xiaov.R.id.oivWallet)
    OptionItemView mOivWallet;
    @Bind(com.cfk.xiaov.R.id.oivCardPaket)
    OptionItemView mOivCardPaket;

    @Bind(com.cfk.xiaov.R.id.oivSetting)
    OptionItemView mOivSetting;

    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initView(View rootView) {
        mIvQRCordCard.setOnClickListener(v -> showQRCard());
        mOivAlbum.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_JIAN_SHU));
        mOivCollection.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_CSDN));
        mOivWallet.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_OSCHINA));
        mOivCardPaket.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_GITHUB));
    }

    @Override
    public void initListener() {
        mLlMyInfo.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(MyInfoActivity.class));
        mOivSetting.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(SettingActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    private void showQRCard() {
        if (mQrCardDialog == null) {
            View qrCardView = View.inflate(getActivity(), com.cfk.xiaov.R.layout.include_qrcode_card, null);
            ImageView ivHeader = (ImageView) qrCardView.findViewById(com.cfk.xiaov.R.id.ivHeader);
            TextView tvName = (TextView) qrCardView.findViewById(com.cfk.xiaov.R.id.tvName);
            ImageView ivCard = (ImageView) qrCardView.findViewById(com.cfk.xiaov.R.id.ivCard);
            TextView tvTip = (TextView) qrCardView.findViewById(com.cfk.xiaov.R.id.tvTip);
            tvTip.setText(UIUtils.getString(com.cfk.xiaov.R.string.qr_code_card_tip));

            UserInfo userInfo = mPresenter.getUserInfo();
            if (userInfo != null) {
                Glide.with(getActivity()).load(userInfo.getPortraitUri()).centerCrop().into(ivHeader);
                tvName.setText(userInfo.getName());
                Observable.just(QRCodeEncoder.syncEncodeQRCode(AppConst.QrCodeCommon.ADD + userInfo.getUserId(), UIUtils.dip2Px(100)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bitmap -> ivCard.setImageBitmap(bitmap), this::loadQRCardError);
            }

            mQrCardDialog = new CustomDialog(getActivity(), 300, 400, qrCardView, com.cfk.xiaov.R.style.MyDialog);
        }
        mQrCardDialog.show();
    }

    private void loadQRCardError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.CHANGE_INFO_FOR_ME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.CHANGE_INFO_FOR_ME);
    }

    @Override
    protected MeFgPresenter createPresenter() {
        return new MeFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.fragment_me;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvHeader;
    }

    @Override
    public TextView getTvName() {
        return mTvName;
    }

    @Override
    public TextView getTvAccount() {
        return mTvAccount;
    }
}
