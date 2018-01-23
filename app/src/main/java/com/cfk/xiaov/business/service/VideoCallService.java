package com.cfk.xiaov.business.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.data.TCallParameter;
import com.cfk.xiaov.ui.activity.TComingCallActivity;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;

public class VideoCallService extends Service implements ILVIncomingListener, ILVCallListener, ILVCallNotificationListener {
    String TAG = getClass().getSimpleName();
    Handler handler;

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
        initCallManager();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void initCallManager() {
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

    private void uninitCallManager() {
        ILVCallManager.getInstance().removeIncomingListener(this);
        ILVCallManager.getInstance().removeCallListener(this);
    }

    @Override
    public void onDestroy() {
        ILVCallManager.getInstance().onDestory();
        uninitCallManager();
        super.onDestroy();
        unRegisterBR();
        Log.i(TAG, "Service onDestroy");
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void onCallEstablish(int callId) {

    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Log.i(TAG, "End Call:" + endResult + "-" + endInfo + "/" + callId);
        Log.e("XDBG_END", "onCallEnd->id: " + callId + "|" + endResult + "|" + endInfo);
        if (endResult == 101) {
            ((MyApp) getApplication()).initILVLive();
            uninitCallManager();
            initCallManager();
        }
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    @Override
    public void onRecvNotification(int callid, ILVCallNotification notification) {
        Log.i(TAG, "onRecvNotification->notify id:" + notification.getNotifId() + "|" + notification.getUserInfo() + "/" + notification.getSender());
        Intent intent = new Intent(AppConstants.Action.NEW_ILIVE_NOTIFY);
        intent.putExtra("MSG", "onRecvNotification->notify id:" + notification.getNotifId() + "|" + notification.getUserInfo() + "/" + notification.getSender());
        sendBroadcast(intent);
    }

    @Override
    public void onNewIncomingCall(final int callId, final int callType, final ILVIncomingNotification notification) {
        Log.i(TAG, "New Call from:" + notification.getSender() + "/" + callId + "-" + notification);

        Intent intent = new Intent();
        intent.setClass(this, TComingCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TCallParameter parameter = new TCallParameter(notification.getSponsorId(),
                callId,
                callType,
                notification.getSender(),
                AccountCache.getAccount());
        intent.putExtra("json", parameter.toJson());
        startActivity(intent);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private void registerBR() {
        registerReceiver(receiver, new IntentFilter(AppConstants.Action.CONNECTIVITY_CHANGE_ACTION));
    }

    private void unRegisterBR() {
        unregisterReceiver(receiver);
    }

}
