package com.cfk.xiaov.ui.activity;

import android.text.TextUtils;
import android.view.View;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.cache.BondCache;
import com.lqr.optionitemview.OptionItemView;
import com.cfk.xiaov.R;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.widget.CustomDialog;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;

/**
 * @创建者 CSDN_LQR
 * @描述 设置界面
 */
public class SettingActivity extends BaseActivity {

    private View mExitView;

    @Bind(R.id.oivAbout)
    OptionItemView mOivAbout;
    @Bind(R.id.oivHelpFeedback)
    OptionItemView mOivHelpFeedback;
    @Bind(R.id.oivExit)
    OptionItemView mOivExit;
    @Bind(R.id.oivCancelBond)
    OptionItemView mOivCancelBond;
    private CustomDialog mExitDialog;

    @Override
    public void initListener() {
        mOivAbout.setOnClickListener(v -> jumpToActivity(AboutActivity.class));
        mOivHelpFeedback.setOnClickListener(v1 -> jumpToWebViewActivity(AppConst.WeChatUrl.HELP_FEED_BACK));
        mOivExit.setOnClickListener(v -> {
            if (mExitView == null) {
                mExitView = View.inflate(this, R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(this, mExitView, R.style.MyDialog);
                mExitView.findViewById(R.id.tvExitAccount).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().logout();
                    UserCache.clear();
                    BondCache.clear();
                    mExitDialog.dismiss();
                    MyApp.exit();
                    jumpToActivityAndClearTask(LoginActivity.class);
                });
                mExitView.findViewById(R.id.tvExitApp).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().disconnect();
                    mExitDialog.dismiss();
                    MyApp.exit();
                });
            }
            mExitDialog.show();
        });
        mOivCancelBond.setOnClickListener(v -> {
            if(TextUtils.isEmpty(BondCache.getBondId())){
                jumpToActivity(ScanActivity.class);
            }else{
                BondCache.clear();
                mOivCancelBond.setLeftText(R.string.bond_device);
            }

        });
    }

    @Override
    public void initView() {
        super.initView();
        if(TextUtils.isEmpty(BondCache.getBondId())){
            mOivCancelBond.setLeftText(R.string.bond_device);
        }else {
            mOivCancelBond.setLeftText(R.string.cancel_bond);
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }
}
