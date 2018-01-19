package com.cfk.xiaov.api;

import android.util.Log;

import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.util.UIUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * 帐户管理(演示独立模式下的帐号注册与登录)
 */
public class AccountMgr {
    private final static String TAG = "AccountMgr";

    private boolean loginSuccess;
    private boolean isLogining;
    private boolean isRunning = false;

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    public void loginSDK(String id, String userSig) {
        if (isRunning) {
            return;
        }
        loginSuccess = false;
        isLogining = false;
        new Thread(() -> {
            isRunning = true;
            //开始登录，最多10次
            for (int i = 0; i < 5; i++) {
                //如果正在登陆，等待返回结果...
                while (true) {
                    if (isLogining) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        break;
                    }

                }

                if (!loginSuccess) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "----------Login try:" + i);
                    ILiveLoginManager.getInstance().iLiveLogin(id, userSig, callBack);
                    isLogining = true;
                } else {

                    break;
                }

            }
            isRunning = false;
        }).start();


    }

    private ILiveCallBack callBack = new ILiveCallBack() {
        @Override
        public void onSuccess(Object data) {
            Log.i(TAG, "Login CallSDK success.");
            loginSuccess = true;
            isLogining = false;

            MyApp.isLogin = true;
            UIUtils.showToastSafely("登录成功！");
        }

        @Override
        public void onError(String module, int errCode, String errMsg) {
            Log.i(TAG, "Login CallSDK error:" + errCode + "/" + errMsg);
            loginSuccess = false;
            isLogining = false;
        }
    };


}
