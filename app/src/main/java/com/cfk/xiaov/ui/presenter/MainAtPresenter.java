package com.cfk.xiaov.ui.presenter;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.ui.view.IMainAtView;

import io.rong.imlib.RongIMClient;

public class MainAtPresenter extends BasePresenter<IMainAtView> {

    public MainAtPresenter(BaseActivity context) {
        super(context);
        connect(UserCache.getToken());
        //同步所有用户信息
        DBManager.getInstance().getAllUserInfo();
    }


    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (UIUtils.getContext().getApplicationInfo().packageName.equals(MyApp.getCurProcessName(UIUtils.getContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtils.e("--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.e("--onSuccess---" + userid);
                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtils.e("--onError" + errorCode);
                    UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.disconnect_server));
                }
            });
        }
    }
}
