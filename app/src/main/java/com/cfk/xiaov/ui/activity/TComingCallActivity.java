package com.cfk.xiaov.ui.activity;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.rest.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.storage.data.TCallParameter;
import com.cfk.xiaov.misc.exception.ServerException;
import com.cfk.xiaov.rest.model.response.GetUserInfoResponse;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.tencent.callsdk.ILVCallManager;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cfk on 2018/1/17.
 */

public class TComingCallActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    @BindView(R.id.ibAccept)
    ImageButton mIbAccept;
    @BindView(R.id.ibDeny)
    ImageButton mIbDeny;
    @BindView(R.id.coming_call_user_pic)
    ImageView mUserPic;
    @BindView(R.id.coming_call_user_name)
    TextView mUserName;
    MediaPlayer mediaPlayer;

    Thread timeoutThread;
    TCallParameter tCallParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        wakeUpAndUnlock(this);
        wakeAndUnlock(this);

        setContentView(R.layout.activity_coming_call);
        ButterKnife.bind(this);
        registerBR();
        Bundle bundle = getIntent().getExtras();
        String json = bundle != null ? bundle.getString("json") : null;
        tCallParameter = TCallParameter.parserFromJson(json);
        initView();
        initListener();
    }

    private void rxError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    void initView() {
        final String[] nickName = new String[1];
        ApiRetrofit.getInstance().getUserInfoById(tCallParameter.getCallUserId())
                .flatMap(getUserInfoResponse -> {
                    if (getUserInfoResponse != null && getUserInfoResponse.getCode() == 200) {
                        GetUserInfoResponse.ResultEntity res = getUserInfoResponse.getResult();
                        nickName[0] = res.getNickname();
                        return ApiRetrofit.getInstance().getQiNiuDownloadUrl(res.getPortraitUri() + "?imageView2/1/w/200/h/200");
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.load_error))));
                    }
                })
                // 线程分配必须放到subscribe之前flatMap之后
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiNiuDownloadResponse -> {
                    if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                        String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                        Glide.with(this).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(mUserPic) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                view.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                        mUserName.setText(nickName[0]);
                    }
                }, this::rxError);

        mediaPlayer = new MediaPlayer();
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = getAssets().openFd("8521.wav");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(this::finish);
        });
        timeoutThread.start();
    }

    void initListener() {
        mIbAccept.setOnClickListener(v -> {
            acceptCall();

            finish();
        });
        mIbDeny.setOnClickListener(v -> {
            int ret = ILVCallManager.getInstance().rejectCall(tCallParameter.getCallId());
            Log.i(TAG, "Reject Call:" + ret + "/" + tCallParameter.getCallId());
            finish();
        });
    }

    public static void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    public static void wakeAndUnlock(Context context) {
        KeyguardManager km;
        KeyguardManager.KeyguardLock kl;
        PowerManager pm;
        PowerManager.WakeLock wl;

        //获取电源管理器对象
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire();

        //得到键盘锁管理器对象
        km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");
        kl.reenableKeyguard();
        //解锁,加上会自动解锁，比较危险
        kl.disableKeyguard();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        timeoutThread.interrupt();
        unRegisterBR();
    }

    private void acceptCall() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CallActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String json = tCallParameter.toJson();
        intent.putExtra("json", json);
        startActivity(intent);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void registerBR() {
        registerReceiver(receiver, new IntentFilter(AppConstants.Action.HANG_UP_CALL));
    }

    private void unRegisterBR() {

        unregisterReceiver(receiver);
    }
}
