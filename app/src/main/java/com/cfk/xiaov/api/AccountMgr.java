package com.cfk.xiaov.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.util.BroadcastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 帐户管理(演示独立模式下的帐号注册与登录)
 */
public class AccountMgr {
    private final static String TAG = "AccountMgr";


    public interface RequestCallBack {
        void onResult(int error, String response);
    }

    private int iReqId = 1;
    private final SparseArray<RequestCallBack> mapRequest = new SparseArray<>();
    private Handler hMsgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            RequestCallBack callBack = null;
            synchronized (mapRequest) {
                callBack = mapRequest.get(msg.what);
                mapRequest.remove(msg.what);
            }
            if (null != callBack) {
                Log.v(TAG, "onResult->requestId:" + msg.what + ", code:" + msg.arg1 + ", info:" + msg.obj);
                callBack.onResult(msg.arg1, (String) msg.obj);
            }
        }
    };


    private int getRequestId() {
        return iReqId++;
    }

    /**
     * 获取HTTP的Get请求返回值(阻塞)
     *
     * @param strAction
     * @return
     * @throws Exception
     */
    public static String getHttpGetRsp(String strAction) throws Exception {
        Log.v(TAG, "getHttpGetRsp->request: \n" + strAction);
        URL _url = new URL(strAction.replace(" ", "%20"));
        HttpURLConnection _conn = (HttpURLConnection) _url.openConnection();
        _conn.setDoInput(true);
        //_conn.setDoOutput(true);
        _conn.setConnectTimeout(1000 * 5);
        _conn.setReadTimeout(1000 * 10);
        _conn.setRequestMethod("GET");

        int _rspCode = _conn.getResponseCode();
        if (_rspCode == 200) {
            InputStreamReader _in = new InputStreamReader(_conn.getInputStream());
            BufferedReader _inReader = new BufferedReader(_in);
            StringBuffer _strBuf = new StringBuffer();
            String _line = null;
            while (null != (_line = _inReader.readLine())) {
                _strBuf.append(_line + "\n");
            }

            _inReader.close();
            _in.close();
            _conn.disconnect();
            Log.v(TAG, "getHttpGetRsp->response info: " + _strBuf.toString());
            return _strBuf.toString();
        } else {
            Log.v(TAG, "getHttpGetRsp->response code: " + _rspCode);
        }

        return null;
    }

    private void doBackGetRequest(final int reqId, final String request) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = reqId;
                try {
                    String rsp = getHttpGetRsp(request);
                    if (TextUtils.isEmpty(rsp)) {
                        msg.arg1 = 1;
                    } else {
                        msg.arg1 = 0;
                        msg.obj = rsp;
                    }
                } catch (Exception e) {
                    msg.arg1 = 2;
                    msg.obj = e.toString();
                }
                hMsgHandler.sendMessage(msg);
            }
        });
        thread.start();
    }

    /**
     * 使用userSig登录iLiveSDK(独立模式下获有userSig直接调用登录)
     */
    public void loginSDK(final String id, final String userSig) {
        ILiveLoginManager.getInstance().iLiveLogin(id, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.i(TAG, "Login CallSDK success:" + id);
//                Intent intent = new Intent(MyApp.ApplicationContext, VideoCallService.class);
//                MyApp.ApplicationContext.startService(intent);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //Toast.makeText(MyApp.ApplicationContext, "Login failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 登录并获取userSig(*托管模式，独立模式下直接用userSig调用loginSDK登录)
     */
    public void t_login(final String id, String password) {
        Log.i(TAG, "login ing:" + id + ":" + password);
        ILiveLoginManager.getInstance().tlsLogin(id, password, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                loginSDK(id, data);
                Log.i(TAG, "login onSuccess:" + id + ":" + password);
                AccountCache.save(id, password);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                 Toast.makeText(MyApp.ApplicationContext, "login failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
                BroadcastUtils.sendBroadcast(AppConst.NET_STATUS,"net_status","failed");
                Log.i(TAG, "login onError:" + id + ":" + password);
            }
        });
    }

    /**
     * 注册用户名(*托管模式，独立模式下请向自己私有服务器注册)
     */
    public void t_regist(String name,String account, String password) {

        Log.i(TAG, "t_regist:" + account + ":" + password);
        ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(MyApp.ApplicationContext, "regist success!", Toast.LENGTH_SHORT).show();
                t_login(account,password);


                //MyApp.ApplicationContext.startActivity(new Intent(MyApp.ApplicationContext, LoginActivity.class));
                Log.i(TAG, "regist onSuccess:" + account + ":" + password);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(MyApp.ApplicationContext, "regist failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "regist onError:" + account + ":" + password);
                Log.i(TAG, "regist onError:" + errCode + ":" + errMsg);
            }
        });

    }


}
