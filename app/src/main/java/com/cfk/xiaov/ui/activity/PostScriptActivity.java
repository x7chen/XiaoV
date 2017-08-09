package com.cfk.xiaov.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cfk.xiaov.ui.presenter.PostScriptAtPresenter;
import com.cfk.xiaov.ui.view.IPostScriptAtView;
import com.cfk.xiaov.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 附言界面
 */
public class PostScriptActivity extends BaseActivity<IPostScriptAtView, PostScriptAtPresenter> implements IPostScriptAtView {

    @Bind(com.cfk.xiaov.R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @Bind(com.cfk.xiaov.R.id.etMsg)
    EditText mEtMsg;
    @Bind(com.cfk.xiaov.R.id.ibClear)
    ImageButton mIbClear;
    private String mUserId;

    @Override
    public void init() {
        mUserId = getIntent().getStringExtra("userId");
    }

    @Override
    public void initView() {
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mUserId)) {
            finish();
        }
    }

    @Override
    public void initListener() {
        mIbClear.setOnClickListener(v -> mEtMsg.setText(""));
        mBtnToolbarSend.setOnClickListener(v -> mPresenter.addFriend(mUserId));
    }

    @Override
    protected PostScriptAtPresenter createPresenter() {
        return new PostScriptAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_postscript;
    }

    @Override
    public EditText getEtMsg() {
        return mEtMsg;
    }
}
