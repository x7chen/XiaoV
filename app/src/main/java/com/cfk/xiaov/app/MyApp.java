package com.cfk.xiaov.app;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.cfk.xiaov.api.AccountMgr;
import com.cfk.xiaov.app.base.BaseApp;
import com.tencent.ilivesdk.ILiveSDK;

import org.litepal.LitePal;


/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  神兽保佑
 * 　　　　┃　　　┃  代码无bug
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 * @创建者 CSDN_LQR
 * @描述 BaseApp的拓展，用于设置其他第三方的初始化
 */
public class MyApp extends BaseApp {
    String TAG = getClass().getSimpleName();
    public static Context ApplicationContext;
    public static AccountMgr mAccountMgr;
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext = this;
        mAccountMgr = new AccountMgr();
        LitePal.initialize(this);
        initILVLive();
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initILVLive(){
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400037041, 14464);
        Log.i(TAG,"init iLive!");
    }



}
