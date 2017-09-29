package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.MyInfoActivity;
import com.cfk.xiaov.ui.activity.SettingActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.presenter.MeFgPresenter;
import com.cfk.xiaov.ui.view.IMeFgView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.widget.CustomDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lqr.optionitemview.OptionItemView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
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
            final ImageView ivHeader = (ImageView) qrCardView.findViewById(R.id.ivHeader);
            TextView tvName = (TextView) qrCardView.findViewById(com.cfk.xiaov.R.id.tvName);
            ImageView ivCard = (ImageView) qrCardView.findViewById(com.cfk.xiaov.R.id.ivCard);
            TextView tvTip = (TextView) qrCardView.findViewById(com.cfk.xiaov.R.id.tvTip);
            Friend friend = DBManager.getInstance().getFriendById(AccountCache.getAccount());
            ApiRetrofit.getInstance().getQiNiuDownloadUrl(friend.getPortraitUri())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(qiNiuDownloadResponse -> {
                        if(qiNiuDownloadResponse !=null&&qiNiuDownloadResponse.getCode()==200){
                            String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                            Glide.with(this).load(pic).centerCrop().into(ivHeader);
                        }
                    });
            tvName.setText(friend.getName());
            tvTip.setText(UIUtils.getString(com.cfk.xiaov.R.string.qr_code_card_tip));
            Observable.just(AccountCache.getAccount())
                    .map(str -> {
                        int size = 400; // 图像宽度
                        Map<EncodeHintType, Object> hints = new HashMap<>();
                        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                        BitMatrix matrix = null;
                        try {
                            matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, size, size, hints);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        int[] pixels = new int[size * size];
                        for (int y = 0; y < size; y++) {
                            for (int x = 0; x < size; x++) {
                                if (matrix.get(x, y)) {
                                    pixels[y * size + x] = 0xff006080;
                                } else {
                                    pixels[y * size + x] = 0xffffffff;
                                }
                            }
                        }
                        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                        bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
                        return bitmap;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ivCard::setImageBitmap);
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
