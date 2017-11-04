package com.cfk.xiaov.ui.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
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
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.model.response.GetUserInfoResponse;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComingCallActivity extends AppCompatActivity implements ILVCallListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        wakeUpAndUnlock(this);
        wakeAndUnlock(this);

        setContentView(R.layout.activity_coming_call);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        int callId = mIntent.getIntExtra("CallId", 0);
        String hostId = mIntent.getStringExtra("HostId");
        int callType = mIntent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        String callUserId = mIntent.getStringExtra("CallUserId");
        final String[] nickName = new String[1];
        ApiRetrofit.getInstance().getUserInfoById(callUserId)
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
                }, this::loadError);
        mIbAccept.setOnClickListener(v -> {
            acceptCall(callId, hostId, callType);
            Log.i(TAG, "Accept Call :" + callId);

            finish();
        });
        mIbDeny.setOnClickListener(v -> {
            int ret = ILVCallManager.getInstance().rejectCall(callId);
            Log.i(TAG, "Reject Call:" + ret + "/" + callId);
            finish();
        });
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
        ILVCallManager.getInstance().addCallListener(this);
    }

    private void loadError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        BroadcastUtils.sendBroadcast(AppConst.NET_STATUS, "net_status", "failed");
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
        ILVCallManager.getInstance().removeCallListener(this);
    }

    private void acceptCall(int callId, String hostId, int callType) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CallActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        intent.putExtra("Mode", "video");
        startActivity(intent);
    }

    @Override
    public void onCallEstablish(int callId) {

    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }
}
