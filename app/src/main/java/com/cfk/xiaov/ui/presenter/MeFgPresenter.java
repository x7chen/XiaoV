package com.cfk.xiaov.ui.presenter;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMeFgView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;


public class MeFgPresenter extends BasePresenter<IMeFgView> {


    private boolean isFirst = true;

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {

    }

    public void refreshUserInfo() {

    }

    public void fillView() {

    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }
}
