package com.cfk.xiaov.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;

import butterknife.Bind;
import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 CSDN_LQR
 * @描述 更改名字界面
 */
public class ChangeMyNameActivity extends BaseActivity {

    @Bind(com.cfk.xiaov.R.id.btnToolbarSend)
    Button mBtnToolbarSend;
    @Bind(com.cfk.xiaov.R.id.etName)
    EditText mEtName;

    @Override
    public void initView() {
        mBtnToolbarSend.setText(UIUtils.getString(com.cfk.xiaov.R.string.save));
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        UserInfo userInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (userInfo != null)
            mEtName.setText(userInfo.getName());
        mEtName.setSelection(mEtName.getText().toString().trim().length());
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> changeMyName());
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtName.getText().toString().trim().length() > 0) {
                    mBtnToolbarSend.setEnabled(true);
                } else {
                    mBtnToolbarSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeMyName() {
        showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));
        String nickName = mEtName.getText().toString().trim();
        ApiRetrofit.getInstance().setName(nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(setNameResponse -> {
                    hideWaitingDialog();
                    if (setNameResponse.getCode() == 200) {
                        Friend friend = DBManager.getInstance().getFriendById(UserCache.getId());
                        if (friend != null) {
                            friend.setName(nickName);
                            friend.setDisplayName(nickName);
                            DBManager.getInstance().saveOrUpdateFriend(friend);
                            BroadcastManager.getInstance(ChangeMyNameActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                            BroadcastManager.getInstance(ChangeMyNameActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_CHANGE_NAME);
                        }
                        finish();
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        hideWaitingDialog();
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_change_name;
    }
}
