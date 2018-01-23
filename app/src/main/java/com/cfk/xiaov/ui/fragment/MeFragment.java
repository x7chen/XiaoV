package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.sp.MyInfoCache;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.MyInfoActivity;
import com.cfk.xiaov.ui.activity.SettingActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.ui.dialog.CustomDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 Sean
 * @描述 我界面
 */
public class MeFragment extends BaseFragment {

    private CustomDialog mQrCardDialog;

    @BindView(com.cfk.xiaov.R.id.llMyInfo)
    LinearLayout mLlMyInfo;
    @BindView(com.cfk.xiaov.R.id.ivHeader)
    ImageView mIvHeader;
    @BindView(com.cfk.xiaov.R.id.tvName)
    TextView mTvName;
    @BindView(com.cfk.xiaov.R.id.tvAccount)
    TextView mTvAccount;
    @BindView(com.cfk.xiaov.R.id.ivQRCordCard)
    ImageView mIvQRCordCard;

    @BindView(com.cfk.xiaov.R.id.oivSetting)
    TextView mOivSetting;

    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initData() {
        loadUserInfo();
    }

    @Override
    public void initView(View rootView) {
        mIvQRCordCard.setOnClickListener(v -> showQRCard());
    }

    @Override
    public void initListener() {
        mLlMyInfo.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(MyInfoActivity.class));
        mOivSetting.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(SettingActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    private void showQRCard() {
        if (mQrCardDialog != null) {
            mQrCardDialog.dismiss();
        }
//        if (mQrCardDialog == null) {
        View qrCardView = View.inflate(getContext(), com.cfk.xiaov.R.layout.include_qrcode_card, null);
        final ImageView ivHeader = qrCardView.findViewById(R.id.ivHeader);
        TextView tvName = qrCardView.findViewById(R.id.tvName);
        ImageView ivCard = qrCardView.findViewById(R.id.ivCard);
        TextView tvTip = qrCardView.findViewById(R.id.tvTip);
        Glide.with(this).load(MyInfoCache.getAvatarUri()).centerCrop().into(ivHeader);
        tvName.setText(MyInfoCache.getNickName());
        tvTip.setText(UIUtils.getString(com.cfk.xiaov.R.string.qr_code_card_tip));
        Observable.just("bond:" + AccountCache.getAccount())
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
        mQrCardDialog = new CustomDialog(getContext(), 300, 400, qrCardView, com.cfk.xiaov.R.style.MyDialog);
//        }
        mQrCardDialog.show();
    }

    private void loadQRCardError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadUserInfo();
        }
    };

    private void registerBR() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.CHANGE_INFO_FOR_ME));
    }

    private void unregisterBR() {
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public void loadUserInfo() {
        fillView();
    }

    public void fillView() {
        Glide.with(this).load(MyInfoCache.getAvatarUri()).centerCrop().into(mIvHeader);
        mTvAccount.setText(UIUtils.getString(com.cfk.xiaov.R.string.my_chat_account) + MyInfoCache.getAccount());
        mTvName.setText(MyInfoCache.getNickName());
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.fragment_me;
    }

}
