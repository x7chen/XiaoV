package com.cfk.xiaov.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.RongGenerate;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeviceInfoActivity extends BaseActivity {

    @Bind(R.id.ivHeader)
    ImageView ivHeader;
    @Bind(R.id.ivQR_Code)
    ImageView ivQR_Code;
    @Bind(R.id.tvAccount)
    TextView tvAccount;
    @Bind(R.id.tvNickName)
    TextView tvNickName;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.btCancelBond)
    Button btCancelBond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    public void initView() {
        ApiRetrofit.getInstance().getUserInfoById(BondCache.getBondId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.getCode() == 200) {
                        tvStatus.setText("已绑定");
                        tvAccount.setText(response.getResult().getId());
                        tvNickName.setText(response.getResult().getNickname());
                        if (response.getResult().getPortraitUri() == null) {
                            String pic = RongGenerate.generateDefaultAvatar(response.getResult().getNickname(), response.getResult().getId());
                            Glide.with(this).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivHeader) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    view.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                        } else {
                            ApiRetrofit.getInstance().getQiNiuDownloadUrl(response.getResult().getPortraitUri())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(qiNiuDownloadResponse -> {
                                        String pic;
                                        if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                                            pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                                            //Glide.with(this).load(pic).centerCrop().into(ivCamera);

                                        } else {
                                            pic = RongGenerate.generateDefaultAvatar(response.getResult().getNickname(), response.getResult().getId());
                                        }

                                        Glide.with(this).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivHeader) {
                                            @Override
                                            protected void setResource(Bitmap resource) {
                                                RoundedBitmapDrawable circularBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                circularBitmapDrawable.setCircular(true);
                                                view.setImageDrawable(circularBitmapDrawable);
                                            }
                                        });
                                    });
                        }
                    } else {
                        tvAccount.setText(BondCache.getBondId());
                        tvNickName.setText(BondCache.getBondId());
                    }

                });

        genQRBitmap(BondCache.getBondId());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_device_info;
    }

    public void initListener() {
        btCancelBond.setOnClickListener(view -> {
            BondCache.clear();
            finish();
        });
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
                .subscribe(bitmap -> ivQR_Code.setImageBitmap(bitmap)
                );
    }
}
