package com.cfk.xiaov.api;

import android.util.Log;
import android.widget.Toast;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.util.BroadcastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * 帐户管理(演示独立模式下的帐号注册与登录)
 */
public class AccountMgr {
    private final static String TAG = "AccountMgr";

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    public void loginSDK(final String id, final String userSig) {
        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.i(TAG, "Login CallSDK success:" + id);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }




}
