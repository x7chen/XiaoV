package com.cfk.xiaov.ui.activity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;

import butterknife.BindView;

public class RegisterByPhoneActivity extends BaseActivity {

    @BindView(R.id.etNick)
    EditText etNick;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etVerifyCode)
    EditText etVerifyCode;
    @BindView(R.id.btnGetCode)
    Button btnGetCode;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tvRegisterByNormal)
    TextView tvRegisterByNormal;

    @Override
    public void initView(){
        super.initView();
        btnRegister.setEnabled(false);
    }
    @Override
    public void initListener() {
        super.initListener();
        btnGetCode.setOnClickListener(view -> {

        });
        btnRegister.setOnClickListener(view -> {

        });
        tvRegisterByNormal.setOnClickListener(view -> {
            jumpToActivity(RegisterActivity.class);
            finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_register_by_phone;
    }
}
