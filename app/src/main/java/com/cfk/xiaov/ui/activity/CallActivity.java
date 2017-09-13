package com.cfk.xiaov.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cfk.xiaov.R;
import com.cfk.xiaov.ui.service.VideoCallService;
import com.kyleduo.switchbutton.SwitchButton;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements ILVCallListener, ILVBCallMemberListener, View.OnClickListener {
    String TAG = getClass().getSimpleName();

    @Bind(R.id.sb_sw_video)
    SwitchButton sbVideo;
    @Bind(R.id.av_root_view)
    AVRootView avRootView;
    @Bind(R.id.btn_end)
    ImageButton btnEndCall;
    @Bind(R.id.btn_speaker)
    Button btnSpeaker;
    @Bind(R.id.tv_call_title)
    TextView tvTitle;
    @Bind(R.id.tv_log)
    TextView tvLog;
    @Bind(R.id.btn_camera)
    Button btnCamera;
    @Bind(R.id.btn_mic)
    Button btnMic;
    @Bind(R.id.ll_beauty_setting)
    LinearLayout llBeauty;
    @Bind(R.id.rl_control)
    RelativeLayout rlControl;
    @Bind(R.id.sb_beauty_progress)
    SeekBar sbBeauty;

    @OnClick(R.id.wlog)
    public void writeLog(Button btn) {
        String logFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cfk";
        File file = new File(logFileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        logFileName += "/Log.txt";
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(logFileName, true);
            fileWriter.append(tvLog.getText().toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String mHostId;
    private int mCallId;
    private int mCallType;
    private int mBeautyRate;

    private boolean bCameraEnable = true;
    private boolean bMicEnable = true;
    private boolean bSpeaker = true;
    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;
    String mode;
    Thread allowVideoThread;
    BroadcastReceiver receiver;
    boolean isAlive = false;
    Timer timer;

    private void initView() {
        btnEndCall.setVisibility(View.VISIBLE);
        avRootView.setVisibility(View.INVISIBLE);

    }

    private void changeCamera() {
        if (bCameraEnable) {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        }
        bCameraEnable = !bCameraEnable;
        btnCamera.setText(bCameraEnable ? R.string.tip_close_camera : R.string.tip_open_camera);
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
        btnMic.setText(bMicEnable ? R.string.tip_close_mic : R.string.tip_open_mic);
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
        btnSpeaker.setText(bSpeaker ? R.string.tip_set_headset : R.string.tip_set_speaker);
    }

    private void switchCamera() {
        mCurCameraId = (ILiveConstants.FRONT_CAMERA == mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
    }

    private void setBeauty() {
        if (null == sbBeauty) {

            sbBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                    Toast.makeText(CallActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
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
            });
        }
        llBeauty.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.INVISIBLE);
    }

    /**
     * 输出日志
     */
    private void addLogMessage(String strMsg) {
        String msg = tvLog.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        msg = msg + "\r\n[" + formatter.format(curDate) + "] " + strMsg;
        tvLog.setText(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);
        initView();

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
                mCallId = ILVCallManager.getInstance().makeMutiCall(nums, option);
            } else {
                mCallId = ILVCallManager.getInstance().makeCall(nums.get(0), option);
            }

        } else {  // 接听呼叫
            ILVCallManager.getInstance().acceptCall(mCallId, option);
        }

        ILiveLoginManager.getInstance().setUserStatusListener((error, message) -> finish());

        tvTitle.setText("New Call From:\n" + mHostId);

        //avRootView.setAutoOrientation(false);
        ILVCallManager.getInstance().initAvView(avRootView);

        avRootView.setAutoOrientation(false);
        //avRootView.setRotation(180);
        mode = intent.getStringExtra("Mode");
        Log.i(TAG, "mode:" + mode);
        sbVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setCamera(true);
                setMic(true);

            } else {
                setCamera(false);
                setMic(false);
            }
        });
        if (mode.equals("monitor")) {

            allowVideoThread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> sbVideo.setVisibility(View.VISIBLE));
            });
            allowVideoThread.start();
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("MSG");
                addLogMessage(msg);
                isAlive = true;
            }
        };
        registerReceiver(receiver, new IntentFilter(VideoCallService.NEW_ILIVE_NOTIFY));

        timer = new Timer();
    }

    @Override
    protected void onResume() {
        ILVCallManager.getInstance().onResume();

        super.onResume();
    }

    @Override
    protected void onPause() {
        ILVCallManager.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        super.onDestroy();
        if (mode.equals("monitor")) {
            if (allowVideoThread.isAlive()) {
                allowVideoThread.interrupt();
            }
        }
        unregisterReceiver(receiver);
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
    }

    @Override
    public void onClick(View v) {
        // library中不能使用switch索引资源id
        if (v.getId() == R.id.btn_end) {
            ILVCallManager.getInstance().endCall(mCallId);
            finish();
        } else if (v.getId() == R.id.btn_camera) {
            //changeCamera();
        } else if (v.getId() == R.id.btn_mic) {
            //changeMic();
        } else if (v.getId() == R.id.btn_switch_camera) {
            switchCamera();
        } else if (v.getId() == R.id.btn_speaker) {
            changeSpeaker();
        } else if (v.getId() == R.id.btn_beauty) {
            setBeauty();
        } else if (v.getId() == R.id.btn_beauty_setting_finish) {
            llBeauty.setVisibility(View.GONE);
            rlControl.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btn_log) {
            if (View.VISIBLE == tvLog.getVisibility()) {
                tvLog.setVisibility(View.INVISIBLE);
            } else {
                tvLog.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 会话建立回调
     *
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
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

        Log.d("ILVB-DBG", "onCallEstablish->0:" + avRootView.getViewByIndex(0).getIdentifier() + "/" + avRootView.getViewByIndex(1).getIdentifier());
        addLogMessage("onCallEstablish->0:" + avRootView.getViewByIndex(0).getIdentifier() + "/" + avRootView.getViewByIndex(1).getIdentifier());
        avRootView.swapVideoView(0, 1);
        avRootView.setRemoteRotationFix(180);
        addLogMessage("setRemoteRotationFix(180)");
        addLogMessage("setLocalRotationFix(0)");
        AVVideoView majorView = avRootView.getViewByIndex(0);
        majorView.setMirror(true);
        addLogMessage("index[0]" + ":w=" + majorView.getImageWidth() + ",h=" + majorView.getImageHeight() + ",a=" + majorView.getImageAngle() + ",r=" + majorView.getRotation());
        // 设置点击小屏切换及可拖动
        for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())) {
                minorView.setMirror(true);      // 本地镜像
            }
            addLogMessage("index[" + i + "]" + ":w=" + minorView.getImageWidth() + ",h=" + minorView.getImageHeight() + ",a=" + minorView.getImageAngle() + ",r=" + minorView.getRotation());
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
        Log.e("XDBG_END", "onCallEnd->id: " + callId + "|" + endResult + "|" + endInfo);
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        addLogMessage("[" + id + "] " + (bEnable ? "open" : "close") + " camera");
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {
        addLogMessage("[" + id + "] " + (bEnable ? "open" : "close") + " mic");
    }
}
