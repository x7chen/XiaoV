package com.cfk.xiaov.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.db.model.BondDevice;
import com.cfk.xiaov.ui.service.VideoCallService;
import com.cfk.xiaov.util.UIUtils;
import com.kyleduo.switchbutton.SwitchButton;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements ILVCallListener, ILVBCallMemberListener, View.OnClickListener {
    String TAG = getClass().getSimpleName();
    boolean self_call_end = false;
    @BindView(R.id.sb_sw_video)
    SwitchButton sbVideo;
    @BindView(R.id.av_root_view)
    AVRootView avRootView;
    @BindView(R.id.btn_end)
    ImageButton btnEndCall;
    @BindView(R.id.ibDeny)
    ImageButton mDeny;
    @BindView(R.id.btn_beauty)
    ImageButton mBtnBeauty;

    @BindView(R.id.sb_beauty_progress)
    SeekBar sbBeauty;
    @BindView(R.id.port)
    RelativeLayout portView;
    @BindView(R.id.land)
    RelativeLayout landView;
    @BindView(com.cfk.xiaov.R.id.ivHeader)
    ImageView mIvHeader;

    private String mHostId;
    private int mCallId;
    private int mCallType;
    private int mBeautyRate;

    private boolean bCameraEnable = true;
    private boolean bMicEnable = true;
    private boolean bSpeaker = true;
    private boolean bsSpeaker = true;
    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;
    String mode;
    BroadcastReceiver receiver;
    boolean isAlive = false;
    Timer timer;
    boolean isInCall = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);
        init();
        initView();
        initListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ILVCallManager.getInstance().removeCallListener(this);

        if (allowVideoThread.isAlive()) {
            allowVideoThread.interrupt();
            try {
                allowVideoThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(receiver);
        timer.cancel();

    }


    protected void initView() {
        portView.setVisibility(View.VISIBLE);
        landView.setVisibility(View.INVISIBLE);
        sbBeauty.setOnSeekBarChangeListener(beautyChangeListener);
        mBtnBeauty.setEnabled(false);
    }

    protected void initListener() {
        sbVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setCamera(true);
                setMic(true);
                sbVideo.setEnabled(false);
                mBtnBeauty.setEnabled(true);
            } else {
                setCamera(false);
                setMic(false);
            }
        });
    }

    protected void init() {


        avRootView.setAutoOrientation(false);
        // 添加通话回调
        ILVCallManager.getInstance().addCallListener(this);

        Intent intent = getIntent();
        mHostId = intent.getStringExtra("HostId");
        mCallType = intent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        mCallId = intent.getIntExtra("CallId", 0);
        ILVCallOption option = new ILVCallOption(mHostId)
                .callTips("CallSDK Demo")
                .setMemberListener(this)
                .heartBeatInterval(10)      //心跳
                .setCallType(mCallType);

        if (0 == mCallId) { // 发起呼叫
            List<String> nums = intent.getStringArrayListExtra("CallNumbers");
            if (nums.size() > 1) {
                mCallId = ILVCallManager.getInstance().makeMutiCall(nums, option, mILiveCallBack);
            } else {
                mCallId = ILVCallManager.getInstance().makeCall(nums.get(0), option, mILiveCallBack);
            }

            List<BondDevice> devices = MyApp.getBondDeviceDao().loadAll();
            BondDevice device = null;
            for (BondDevice d : devices) {
                if (d.getAccount().equals(nums.get(0))) {
                    device = d;
                    break;
                }
            }
            if (device != null) {
                Glide.with(this).load(device.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mIvHeader) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }

        } else {  // 接听呼叫
            ILVCallManager.getInstance().acceptCall(mCallId, option);
            portView.setVisibility(View.INVISIBLE);
            landView.setVisibility(View.VISIBLE);
        }
        ILiveLoginManager.getInstance().setUserStatusListener((error, message) -> {
            Log.i(TAG, "setUserStatusListener" + message);
            finish();

        });
        //avRootView.setAutoOrientation(false);
        ILVCallManager.getInstance().initAvView(avRootView);

        mode = intent.getStringExtra("Mode");
        Log.i(TAG, "mode:" + mode);


        allowVideoThread.start();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("MSG");
                Log.i(TAG, "notify:" + msg);
                isAlive = true;
            }
        };
        registerReceiver(receiver, new IntentFilter(VideoCallService.NEW_ILIVE_NOTIFY));

        timer = new Timer();
    }


    private void changeCamera() {
        if (bCameraEnable) {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        }
        bCameraEnable = !bCameraEnable;

    }

    private void setCamera(boolean enable) {
        if (enable == bCameraEnable) {
            return;
        }
        if (enable) {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, true);

        } else {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        }
        bCameraEnable = !bCameraEnable;
    }

    private void changeMic() {
        if (bMicEnable) {
            ILVCallManager.getInstance().enableMic(false);
        } else {
            ILVCallManager.getInstance().enableMic(true);
        }

        bMicEnable = !bMicEnable;

    }

    private void setMic(boolean enable) {
        if (enable == bMicEnable) {
            return;
        }
        if (enable) {
            ILVCallManager.getInstance().enableMic(true);

        } else {
            ILVCallManager.getInstance().enableMic(false);

        }

        bMicEnable = !bMicEnable;
    }

    private void changeSpeaker() {
        if (bSpeaker) {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        } else {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        }
        bSpeaker = !bSpeaker;

    }

    private void setSpeaker() {
        if (bsSpeaker) {
            ILVCallManager.getInstance().enableSpeaker(false);
        } else {
            ILVCallManager.getInstance().enableSpeaker(true);
        }
        bsSpeaker = !bsSpeaker;

    }

    private void switchCamera() {
        mCurCameraId = (ILiveConstants.FRONT_CAMERA == mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
        if (mCurCameraId == ILiveConstants.FRONT_CAMERA) {
            avRootView.setRemoteRotationFix(90);
            //avRootView.setRemoteRotationFix(270);
        } else {
            avRootView.setRemoteRotationFix(90);
            avRootView.setLocalRotationFix(90);
            avRootView.getViewByIndex(0).setRemoteRotationFix(90);
        }
    }

    SeekBar.OnSeekBarChangeListener beautyChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            Toast.makeText(CallActivity.this, "美颜 " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            mBeautyRate = progress;
            ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(9.0f * progress / 100.0f);
        }
    };
    boolean beauty = true;

    private void setBeauty() {
        beauty = !beauty;
        if (beauty) {
            sbBeauty.setVisibility(View.VISIBLE);
        } else {
            sbBeauty.setVisibility(View.INVISIBLE);
        }

    }


    Thread allowVideoThread = new Thread(() -> {
        try {
            Thread.sleep(8000);
            if (!isInCall) {
                UIUtils.showToastSafely("对方不在线");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runOnUiThread(() -> sbVideo.setVisibility(View.VISIBLE));
    });

    ILiveCallBack mILiveCallBack = new ILiveCallBack() {
        @Override
        public void onSuccess(Object data) {
            Log.i(TAG, "mILiveCallBack:success:" + data.toString());
        }

        @Override
        public void onError(String module, int errCode, String errMsg) {
            Log.i(TAG, "mILiveCallBack:error:" + errMsg);
        }
    };

    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
    }

    @Override
    public void onClick(View v) {
        // library中不能使用switch索引资源id
        if (v.getId() == R.id.btn_end) {
            self_call_end = true;
            ILVCallManager.getInstance().endCall(mCallId);
            finish();
        } else if (v.getId() == R.id.btn_switch_camera) {
            switchCamera();
        } else if (v.getId() == R.id.btn_beauty) {
            setBeauty();
        } else if (v.getId() == R.id.ibDeny) {
            ILVCallManager.getInstance().endCall(mCallId);
            finish();
        } else if (v.getId() == R.id.btn_capture) {
//            Bitmap bitmap = avRootView.getDrawingCache();
//            Log.i(TAG, bitmap.toString());
//            saveBitmap(bitmap,String.valueOf(System.currentTimeMillis())+".jpeg");
        } else if (v.getId() == R.id.silent) {
            setSpeaker();
        }
    }

    private void saveBitmap(Bitmap bm, String imageUrlName) {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/xiaoV";
        File file = new File(fileName + "/" + imageUrlName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 会话建立回调
     *
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
        isInCall = true;
        portView.setVisibility(View.INVISIBLE);
        landView.setVisibility(View.VISIBLE);
        btnEndCall.setVisibility(View.VISIBLE);
        avRootView.setVisibility(View.VISIBLE);
        if (mode != null) {
            if (mode.equals("monitor")) {
                setCamera(false);
                setMic(false);
                Log.i(TAG, "mode==monitor?" + mode);
            } else {
                sbVideo.setChecked(true);
            }
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isAlive) {
                    isAlive = false;
                } else {
                    ILVCallManager.getInstance().endCall(mCallId);
                }
            }
        }, 25000, 25000);
        avRootView.swapVideoView(0, 1);
        avRootView.setRemoteRotationFix(180);

        AVVideoView majorView = avRootView.getViewByIndex(0);
        if (ILiveLoginManager.getInstance().getMyUserId().equals(majorView.getIdentifier())) {
            // majorView.setMirror(false);
            //avRootView.swapVideoView(0, 1);
        }
        Log.d(TAG, "onCallEstablish->" + 0 + ":" + avRootView.getViewByIndex(0).getIdentifier());
        // 设置点击小屏切换及可拖动
        for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())) {
                //minorView.setMirror(true);      // 本地镜像
            }
            Log.d(TAG, "onCallEstablish->" + i + ":" + avRootView.getViewByIndex(i).getIdentifier());
            minorView.setDragable(true);    // 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    avRootView.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
    }

    /**
     * 会话结束回调
     *
     * @param callId
     * @param endResult 结束原因
     * @param endInfo   结束描述
     */
    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Log.e(TAG, "onCallEnd->id: " + callId + "|" + endResult + "|" + endInfo);
        if (!self_call_end)
            UIUtils.showToastSafely("已挂断");
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {

    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {

    }
}
