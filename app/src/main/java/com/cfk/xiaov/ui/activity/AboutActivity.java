package com.cfk.xiaov.ui.activity;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;

/**
 * @创建者 CSDN_LQR
 * @描述 关于界面
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_about;
    }
}
