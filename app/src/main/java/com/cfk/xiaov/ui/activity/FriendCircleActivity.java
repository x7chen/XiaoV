package com.cfk.xiaov.ui.activity;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.FriendCircleAtPresenter;
import com.cfk.xiaov.ui.view.IFriendCircleAtView;

/**
 * @创建者 CSDN_LQR
 * @描述 朋友圈
 */
public class FriendCircleActivity extends BaseActivity<IFriendCircleAtView, FriendCircleAtPresenter> implements IFriendCircleAtView {

    @Override
    protected FriendCircleAtPresenter createPresenter() {
        return new FriendCircleAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return 0;
    }
}
