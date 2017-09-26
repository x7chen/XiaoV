package com.cfk.xiaov.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.UIUtils;
import com.jaeger.library.StatusBarUtil;

import butterknife.Bind;
import kr.co.namee.permissiongen.PermissionGen;

/**
 * @创建者 CSDN_LQR
 * @描述 微信闪屏页
 */
public class SplashActivity extends BaseActivity {

    @Bind(com.cfk.xiaov.R.id.rlButton)
    RelativeLayout mRlButton;
    @Bind(com.cfk.xiaov.R.id.btnLogin)
    Button mBtnLogin;
    @Bind(com.cfk.xiaov.R.id.btnRegister)
    Button mBtnRegister;

    @Override
    public void init() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        //电话通讯录
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_PHONE_STATE,
                        //位置
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        //相机、麦克风
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.CAMERA,
                        //存储空间
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS,
                        //解锁
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.DISABLE_KEYGUARD
                )
                .request();

        if (!TextUtils.isEmpty(AccountCache.getUserSig())) {
            String account = AccountCache.getAccount();
            String userSig = AccountCache.getUserSig();
            MyApp.mAccountMgr.loginSDK(account, userSig);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            jumpToActivity(intent);
            finish();
        }

    }

    @Override
    public void initView() {

        StatusBarUtil.setColor(this, UIUtils.getColor(com.cfk.xiaov.R.color.black));

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        mRlButton.startAnimation(alphaAnimation);
    }

    @Override
    public void initListener() {
        mBtnLogin.setOnClickListener(v -> {
            jumpToActivity(LoginActivity.class);
            //finish();
        });
        mBtnRegister.setOnClickListener(v -> {
            jumpToActivity(RegisterActivity.class);
            //finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_splash;
    }
}
