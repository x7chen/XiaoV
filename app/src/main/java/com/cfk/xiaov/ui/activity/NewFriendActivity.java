package com.cfk.xiaov.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.presenter.NewFriendAtPresenter;
import com.cfk.xiaov.ui.view.INewFriendAtView;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.recyclerview.LQRRecyclerView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 新的朋友界面
 */

public class NewFriendActivity extends BaseActivity<INewFriendAtView, NewFriendAtPresenter> implements INewFriendAtView {

    @Bind(com.cfk.xiaov.R.id.llToolbarAddFriend)
    LinearLayout mLlToolbarAddFriend;
    @Bind(com.cfk.xiaov.R.id.tvToolbarAddFriend)
    TextView mTvToolbarAddFriend;

    @Bind(com.cfk.xiaov.R.id.llNoNewFriend)
    LinearLayout mLlNoNewFriend;
    @Bind(com.cfk.xiaov.R.id.llHasNewFriend)
    LinearLayout mLlHasNewFriend;
    @Bind(com.cfk.xiaov.R.id.rvNewFriend)
    LQRRecyclerView mRvNewFriend;

    @Override
    public void initView() {
        mLlToolbarAddFriend.setVisibility(View.VISIBLE);
        setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.new_friend));
    }

    @Override
    public void initData() {
        mPresenter.loadNewFriendData();
    }

    @Override
    public void initListener() {
        mTvToolbarAddFriend.setOnClickListener(v -> jumpToActivity(AddFriendActivity.class));
    }

    @Override
    protected NewFriendAtPresenter createPresenter() {
        return new NewFriendAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_new_friend;
    }

    @Override
    public LinearLayout getLlNoNewFriend() {
        return mLlNoNewFriend;
    }

    @Override
    public LinearLayout getLlHasNewFriend() {
        return mLlHasNewFriend;
    }

    @Override
    public LQRRecyclerView getRvNewFriend() {
        return mRvNewFriend;
    }
}
