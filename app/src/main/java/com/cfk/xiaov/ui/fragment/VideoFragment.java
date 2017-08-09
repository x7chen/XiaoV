package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.ScanActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.presenter.VideoFgPresenter;
import com.cfk.xiaov.ui.view.IVideoFgView;
import com.lqr.recyclerview.LQRRecyclerView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 最近会话列表界面
 */
public class VideoFragment extends BaseFragment<IVideoFgView, VideoFgPresenter> implements IVideoFgView {

    private boolean isFirst = true;
    @Bind(R.id.btAddDevice)
    ImageButton btAddDevice;

    @Override
    public void init() {
        registerBR();

    }

    @Override
    public void initListener() {
        super.initListener();
        btAddDevice.setOnClickListener(view -> ((MainActivity)getActivity()).jumpToActivity(ScanActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            mPresenter.getConversations();
        }
    }

    @Override
    public void initData() {
//        UIUtils.postTaskDelay(() -> {
//        mPresenter.getConversations();
//        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.UPDATE_CONVERSATIONS, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.getConversations();
                isFirst = false;
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_CONVERSATIONS);
    }

    @Override
    protected VideoFgPresenter createPresenter() {
        return new VideoFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.fragment_video;
    }

    @Override
    public LQRRecyclerView getRvVideo() {
        return null;
    }
}
