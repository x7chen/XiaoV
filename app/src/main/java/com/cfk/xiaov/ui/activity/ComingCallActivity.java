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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.exception.ServerException;
import com.cfk.xiaov.model.request.PushRequest;
import com.cfk.xiaov.model.response.GetUserInfoResponse;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComingCallActivity extends AppCompatActivity {
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
    PushRequest session_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        wakeUpAndUnlock(this);
        wakeAndUnlock(this);

        setContentView(R.layout.activity_coming_call);
        ButterKnife.bind(this);
        registerBR();
        Bundle bundle = getIntent().getExtras();
        String json = bundle.getString("json");
        Type mType = new TypeToken<PushRequest>() {
        }.getType();
        Gson gson = new Gson();
        session_info = gson.fromJson(json, mType);
        initView();
        initListener();
    }

    private void rxError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    void initView() {
        final String[] nickName = new String[1];
        ApiRetrofit.getInstance().getUserInfoById(session_info.getFrom())
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
            PushRequest.Extra extra = new PushRequest.Extra();
            extra.setChannel(session_info.getFrom());
            ApiRetrofit.getInstance().push(AppConst.PUSH_METHOD.HANG_UP, session_info.getFrom(), extra)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pushResponse -> {
                    }, this::rxError);
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
        intent.setClass(getApplicationContext(), VideoChatViewActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Type mType = new TypeToken<PushRequest>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(session_info, mType);
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
        registerReceiver(receiver, new IntentFilter(AppConst.Action.HANG_UP_CALL));


    }

    private void unRegisterBR() {

        unregisterReceiver(receiver);
    }
}
