package com.cfk.xiaov.ui.fragment;

import com.lqr.optionitemview.OptionItemView;
import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.ScanActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.presenter.DiscoveryFgPresenter;
import com.cfk.xiaov.ui.view.IDiscoveryFgView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 发现界面
 */
public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter> implements IDiscoveryFgView {

    @Bind(R.id.oivScan)
    OptionItemView mOivScan;
    @Bind(R.id.oivShop)
    OptionItemView mOivShop;
    @Bind(R.id.oivGame)
    OptionItemView mOivGame;

    @Override
    public void initListener() {
        mOivScan.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(ScanActivity.class));
        mOivShop.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.JD));
        mOivGame.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.GAME));
    }

    @Override
    protected DiscoveryFgPresenter createPresenter() {
        return new DiscoveryFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_discovery;
    }
}
