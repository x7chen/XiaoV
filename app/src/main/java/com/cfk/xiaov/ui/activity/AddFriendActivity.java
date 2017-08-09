package com.cfk.xiaov.ui.activity;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.AddFriendAtPresenter;
import com.cfk.xiaov.ui.view.IAddFriendAtView;
import com.cfk.xiaov.util.UIUtils;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 添加朋友界面
 */

public class AddFriendActivity extends BaseActivity<IAddFriendAtView, AddFriendAtPresenter> implements IAddFriendAtView {

    @Bind(com.cfk.xiaov.R.id.llSearchUser)
    LinearLayout mLlSearchUser;
    @Bind(com.cfk.xiaov.R.id.tvAccount)
    TextView mTvAccount;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.add_friend));
        mTvAccount.setText(UserCache.getId() + "");
    }

    @Override
    public void initListener() {
        mLlSearchUser.setOnClickListener(v -> jumpToActivity(SearchUserActivity.class));
    }

    @Override
    protected AddFriendAtPresenter createPresenter() {
        return new AddFriendAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_add_friend;
    }
}
