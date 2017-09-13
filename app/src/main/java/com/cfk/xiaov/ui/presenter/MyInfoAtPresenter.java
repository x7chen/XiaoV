package com.cfk.xiaov.ui.presenter;

import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMyInfoAtView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.imagepicker.bean.ImageItem;
import com.qiniu.android.storage.UploadManager;

public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    private UploadManager mUploadManager;

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        getView().getOivAccount().setRightText(AccountCache.getAccount());
        getView().getOivName().setRightText(AccountCache.getAccount());

    }

    public void setPortrait(ImageItem imageItem) {
        mContext.showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));

    }

    private void uploadError(Throwable throwable) {
        if (throwable != null)
            LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.set_fail));
    }
}
