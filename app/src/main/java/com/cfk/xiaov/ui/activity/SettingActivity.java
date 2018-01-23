package com.cfk.xiaov.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.sp.MyInfoCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.dialog.CustomDialog;

import butterknife.BindView;

/**
 * @创建者 Sean
 * @描述 设置界面
 */
public class SettingActivity extends BaseActivity {
    String TAG = getClass().getSimpleName();
    private View mExitView;

    @BindView(R.id.oivAbout)
    TextView mOivAbout;
    @BindView(R.id.oivHelpFeedback)
    TextView mOivHelpFeedback;
    @BindView(R.id.oivExit)
    TextView mOivExit;
    private CustomDialog mExitDialog;

    @Override
    public void initListener() {
        mOivAbout.setOnClickListener(v -> jumpToActivity(AboutActivity.class));
        mOivHelpFeedback.setOnClickListener(v1 -> jumpToWebViewActivity(AppConstants.MyUrl.HELP_FEED_BACK));
        mOivExit.setOnClickListener(v -> {
            if (mExitView == null) {
                mExitView = View.inflate(this, R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(this, mExitView, R.style.MyDialog);
                mExitView.findViewById(R.id.tvExitAccount).setOnClickListener(v1 -> {
                    AccountCache.clear();
                    MyInfoCache.clear();
                    MyApp.getBondDeviceDao().deleteAll();
                    mExitDialog.dismiss();
                    MyApp.exit();
                    jumpToActivityAndClearTask(LoginActivity.class);
                });
                mExitView.findViewById(R.id.tvExitApp).setOnClickListener(v1 -> {
                    mExitDialog.dismiss();
                    MyApp.exit();
                });
            }
            mExitDialog.show();
        });


    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }
}
