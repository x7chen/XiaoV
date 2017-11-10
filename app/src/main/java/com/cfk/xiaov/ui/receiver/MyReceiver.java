package com.cfk.xiaov.ui.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.response.PushResponse;
import com.cfk.xiaov.ui.activity.ComingCallActivity;
import com.cfk.xiaov.ui.activity.VideoChatViewActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            String src = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.d(TAG, "接受到推送下来的自定义消息:" + src);
            Type mType = new TypeToken<PushResponse.ResultEntity>() {
            }.getType();
            Gson gson = new Gson();
            PushResponse.ResultEntity pushMessage = gson.fromJson(src, mType);
//            String gsonResult = gson.toJson(pushMessage,mType);
//            Log.i(TAG,gsonResult);
            if (pushMessage != null) {
                if(pushMessage.getMethod().equals(AppConst.PUSH_METHOD_CALL)) {
                    Intent intent1 = new Intent(MyApp.ApplicationContext, ComingCallActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("json", src);

                    MyApp.ApplicationContext.startActivity(intent1);
                    // Push Talk messages are push down by custom message format
                }else if(pushMessage.getMethod().equals(AppConst.PUSH_METHOD_HANGUP)){
                    MyApp.ApplicationContext.sendBroadcast(new Intent(AppConst.HANG_UP_CALL));
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知:" + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
