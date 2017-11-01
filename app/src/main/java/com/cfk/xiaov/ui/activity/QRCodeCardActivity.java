package com.cfk.xiaov.ui.activity;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lqr.ninegridimageview.LQRNineGridImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QRCodeCardActivity extends BaseActivity {

    String TAG = getClass().getSimpleName();
    private String mGroupId;

    @BindView(com.cfk.xiaov.R.id.ivHeader)
    ImageView mIvHeader;
    @BindView(com.cfk.xiaov.R.id.ngiv)
    LQRNineGridImageView mNgiv;
    @BindView(com.cfk.xiaov.R.id.tvName)
    TextView mTvName;
    @BindView(com.cfk.xiaov.R.id.ivCard)
    ImageView mIvCard;
    @BindView(com.cfk.xiaov.R.id.tvTip)
    TextView mTvTip;

    @Override
    public void init() {
        mGroupId = getIntent().getStringExtra("groupId");
    }

    @Override
    public void initView() {
        Glide.with(this).load(MyInfoCache.getAvatarUri()).centerCrop().into(mIvHeader);
        mTvName.setText(MyInfoCache.getNickName());
        mTvTip.setText(UIUtils.getString(com.cfk.xiaov.R.string.qr_code_card_tip));
        genQRBitmap(AccountCache.getAccount());
    }

    public void initData() {

    }

    public void genQRBitmap(String content) {

        Observable.just("bond:" + content)
                .map(str -> {
                    int size = 400; // 图像宽度
                    Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
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
                .subscribe(bitmap -> mIvCard.setImageBitmap(bitmap)
                );
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
