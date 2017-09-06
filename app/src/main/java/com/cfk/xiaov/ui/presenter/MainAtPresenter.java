package com.cfk.xiaov.ui.presenter;

import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMainAtView;
import com.cfk.xiaov.util.UIUtils;



public class MainAtPresenter extends BasePresenter<IMainAtView> {

    public MainAtPresenter(BaseActivity context) {
        super(context);
        connect(UserCache.getToken());
    }


    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (UIUtils.getContext().getApplicationInfo().packageName.equals(MyApp.getCurProcessName(UIUtils.getContext()))) {


        }
    }
}
