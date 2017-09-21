package com.cfk.xiaov.ui.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.activity.CallActivity;
import com.cfk.xiaov.ui.activity.ComingCallActivity;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.ArrayList;

public class VideoCallService extends Service implements ILVIncomingListener, ILVCallListener, ILVCallNotificationListener {
    String TAG = getClass().getSimpleName();
    final private String MODE_VIDEO = "video";
    final private String MODE_MONITOR = "monitor";
    private String mode = MODE_MONITOR;

    Handler handler;
    private int mCurIncomingId;
    ArrayList<String> callList = new ArrayList<String>();

    public static final String NEW_ILIVE_NOTIFY = "com.cfk.broadcast.NEW_ILIVE_NOTIFY";

    public VideoCallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();
        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setTimeOut(30)
                .setNotificationListener(this)
                .setAutoBusy(true));
        registerBR();

        // 设置通话回调
        ILVCallManager.getInstance().addIncomingListener(this);
        ILVCallManager.getInstance().addCallListener(this);
        Log.i(TAG, "Init CallSDK...");
    }

    @Override
    public void onDestroy() {
        ILVCallManager.getInstance().removeIncomingListener(this);
        ILVCallManager.getInstance().removeCallListener(this);
        super.onDestroy();
        unRegisterBR();
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void acceptCall(int callId, String hostId, int callType) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", mCurIncomingId);
        intent.putExtra("CallType", callType);
        startActivity(intent);
    }

    private void addCallList(String remoteId) {
        if (!callList.contains(remoteId)) {
            if (callList.add(remoteId)) {
                // adapterCallList.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCallEstablish(int callId) {

    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Log.i(TAG, "End Call:" + endResult + "-" + endInfo + "/" + callId);
        Log.e("XDBG_END", "onCallEnd->id: " + callId + "|" + endResult + "|" + endInfo);
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    @Override
    public void onRecvNotification(int callid, ILVCallNotification notification) {
        Log.i(TAG, "onRecvNotification->notify id:" + notification.getNotifId() + "|" + notification.getUserInfo() + "/" + notification.getSender());
        Intent intent = new Intent(NEW_ILIVE_NOTIFY);
        intent.putExtra("MSG","onRecvNotification->notify id:" + notification.getNotifId() + "|" + notification.getUserInfo() + "/" + notification.getSender());
        sendBroadcast(intent);
    }

    @Override
    public void onNewIncomingCall(final int callId, final int callType, final ILVIncomingNotification notification) {
        Log.i(TAG, "New Call from:" + notification.getSender() + "/" + callId + "-" + notification);
        mCurIncomingId = callId;

        Intent intent = new Intent();
        intent.setClass(this, ComingCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", notification.getSponsorId());
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        startActivity(intent);
//        mIncomingDlg = new AlertDialog.Builder(this)
//                .setTitle("New Call From " + notification.getSender())
//                .setMessage(notification.getNotifDesc())
//                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        acceptCall(callId, notification.getSponsorId(), callType);
//                        Log.i(TAG, "Accept Call :" + mCurIncomingId);
//                    }
//                })
//                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int ret = ILVCallManager.getInstance().rejectCall(mCurIncomingId);
//                        Log.i(TAG, "Reject Call:" + ret + "/" + mCurIncomingId);
//                    }
//                })
//                .create();
//        mIncomingDlg.setCanceledOnTouchOutside(false);
//        mIncomingDlg.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
//        mIncomingDlg.show();
//        Log.i(TAG, "Dialog show!");
        addCallList(notification.getSender());
    }

    /**
     * 发起呼叫
     */
    private void makeCall(int callType, ArrayList<String> nums) {

        Intent intent = new Intent();
        intent.setClass(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
        intent.putExtra("CallId", 0);
        intent.putExtra("CallType", callType);
        intent.putStringArrayListExtra("CallNumbers", nums);
        intent.putExtra("Mode", mode);
        startActivity(intent);
    }

    private void call(String id) {
        ArrayList<String> nums = new ArrayList<>();
        nums.add(id);
        mode = MODE_VIDEO;
        makeCall(ILVCallConstants.CALL_TYPE_VIDEO, nums);

    }

    private void monitor(String id) {
        ArrayList<String> nums = new ArrayList<>();
        nums.add(id);
        mode = MODE_MONITOR;
        makeCall(ILVCallConstants.CALL_TYPE_VIDEO, nums);

    }
    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.MAKE_CALL, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                monitor(intent.getStringExtra("CallId"));
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.FETCH_COMPLETE);
    }

}
