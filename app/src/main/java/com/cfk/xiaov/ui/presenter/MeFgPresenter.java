package com.cfk.xiaov.ui.presenter;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.db.model.UserInfo;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IMeFgView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;


public class MeFgPresenter extends BasePresenter<IMeFgView> {

    String TAG = getClass().getSimpleName();

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        fillView();
    }

    public void fillView() {
        Glide.with(mContext).load(MyInfoCache.getAvatarUri()).centerCrop().into(getView().getIvHeader());
        getView().getTvAccount().setText(UIUtils.getString(com.cfk.xiaov.R.string.my_chat_account) + MyInfoCache.getAccount());
        getView().getTvName().setText(MyInfoCache.getNickName());
    }

}
