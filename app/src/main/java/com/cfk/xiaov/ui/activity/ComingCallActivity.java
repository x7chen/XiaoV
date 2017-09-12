package com.cfk.xiaov.ui.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.cfk.xiaov.R;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallManager;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComingCallActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    @Bind(R.id.ibAccept)
    ImageButton mIbAccept;
    @Bind(R.id.ibDeny)
    ImageButton mIbDeny;

    MediaPlayer mediaPlayer;

    Thread timeoutThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wakeUpAndUnlock(this);

        setContentView(R.layout.activity_coming_call);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        int callId = mIntent.getIntExtra("CallId", 0);
        String hostId = mIntent.getStringExtra("HostId");
        int callType = mIntent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
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
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeoutThread = new Thread(()->{
            try {
                Thread.sleep(30*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(this::finish);
        });
        timeoutThread.start();

    }
    public static void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        timeoutThread.interrupt();
    }

    private void acceptCall(int callId, String hostId, int callType) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CallActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        intent.putExtra("Mode","video");
        startActivity(intent);
    }
}
