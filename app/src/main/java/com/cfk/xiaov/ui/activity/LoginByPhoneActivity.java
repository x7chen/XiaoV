package com.cfk.xiaov.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cfk.xiaov.R;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginByPhoneActivity extends BaseActivity {
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etVerifyCode)
    EditText etVerifyCode;
    @BindView(R.id.btnGetCode)
    Button btnGetCode;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvLoginByNormal)
    TextView tvLoginByNormal;


    EventHandler eventHandler;

    @Override
    public void initView() {
        super.initView();
        btnLogin.setEnabled(false);
    }

    @Override
    public void init() {
        super.init();
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        SMSSDK.setAskPermisionOnReadContact(true);

        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Toast.makeText(LoginByPhoneActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 处理你自己的逻辑
                        UIUtils.showToastSafely("已发送");
                        runOnUiThread(()->btnGetCode.setEnabled(false));

                        countdown();
                    }
                }
            }
        };

        // 短信验证 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    void countdown() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int time = 60;
            @Override
            public void run() {
                runOnUiThread(() -> btnGetCode.setText(String.valueOf(time) + "s"));
                time--;
                if (time == 0) {
                    runOnUiThread(()->{
                        btnGetCode.setEnabled(true);
                        btnGetCode.setText(R.string.btn_get_vertify);
                    });
                    cancel();
                }
            }
        }, 100, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    public void initListener() {
        btnGetCode.setOnClickListener((View view) -> {
            SMSSDK.getVerificationCode("86", etPhone.getText().toString());
        });
        btnLogin.setOnClickListener(view -> {

        });
        tvLoginByNormal.setOnClickListener(view -> {
            jumpToActivity(LoginActivity.class);
            finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_login_by_phone;
    }
}
