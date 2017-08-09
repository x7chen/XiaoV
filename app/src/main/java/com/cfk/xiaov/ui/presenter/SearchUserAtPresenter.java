package com.cfk.xiaov.ui.presenter;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.response.GetUserInfoByIdResponse;
import com.cfk.xiaov.model.response.GetUserInfoByPhoneResponse;
import com.cfk.xiaov.ui.activity.UserInfoActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.util.RegularUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.ISearchUserAtView;
import com.cfk.xiaov.util.LogUtils;

import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchUserAtPresenter extends BasePresenter<ISearchUserAtView> {

    public SearchUserAtPresenter(BaseActivity context) {
        super(context);
    }

    public void searchUser() {
        String content = getView().getEtSearchContent().getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.content_no_empty));
            return;
        }

        mContext.showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));
        if (RegularUtils.isMobile(content)) {
            ApiRetrofit.getInstance().getUserInfoFromPhone(AppConst.REGION, content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUserInfoByPhoneResponse -> {
                        mContext.hideWaitingDialog();
                        if (getUserInfoByPhoneResponse.getCode() == 200) {
                            GetUserInfoByPhoneResponse.ResultEntity result = getUserInfoByPhoneResponse.getResult();
                            UserInfo userInfo = new UserInfo(result.getId(), result.getNickname(), Uri.parse(result.getPortraitUri()));
                            Intent intent = new Intent(mContext, UserInfoActivity.class);
                            intent.putExtra("userInfo", userInfo);
                            mContext.jumpToActivity(intent);
                        } else {
                            getView().getRlNoResultTip().setVisibility(View.VISIBLE);
                            getView().getLlSearch().setVisibility(View.GONE);
                        }
                    }, this::loadError);
        } else {
            ApiRetrofit.getInstance().getUserInfoById(content)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUserInfoByIdResponse -> {
                        mContext.hideWaitingDialog();
                        if (getUserInfoByIdResponse.getCode() == 200) {
                            GetUserInfoByIdResponse.ResultEntity result = getUserInfoByIdResponse.getResult();
                            UserInfo userInfo = new UserInfo(result.getId(), result.getNickname(), Uri.parse(result.getPortraitUri()));
                            Intent intent = new Intent(mContext, UserInfoActivity.class);
                            intent.putExtra("userInfo", userInfo);
                            mContext.jumpToActivity(intent);
                        } else {
                            getView().getRlNoResultTip().setVisibility(View.VISIBLE);
                            getView().getLlSearch().setVisibility(View.GONE);
                        }
                    }, this::loadError);
        }
    }

    private void loadError(Throwable throwable) {
        mContext.hideWaitingDialog();
        LogUtils.sf(throwable.getLocalizedMessage());
    }
}
