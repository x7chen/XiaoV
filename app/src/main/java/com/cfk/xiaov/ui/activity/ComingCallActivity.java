package com.cfk.xiaov.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    private void acceptCall(int callId, String hostId, int callType) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CallActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        startActivity(intent);
    }
}
