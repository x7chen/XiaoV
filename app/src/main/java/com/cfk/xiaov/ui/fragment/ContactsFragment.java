package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.fragment.adapter.ContactsListAdapter;
import com.cfk.xiaov.ui.presenter.VideoFgPresenter;
import com.cfk.xiaov.ui.view.IVideoFgView;
import com.lqr.recyclerview.LQRRecyclerView;

import butterknife.Bind;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsFragment extends BaseFragment<IVideoFgView, VideoFgPresenter> implements IVideoFgView {
    String TAG = getClass().getSimpleName();
    @Bind(R.id.rvContacts)
    RecyclerView rvContacts;

    ContactsListAdapter contactsListAdapter;
    @Override
    public LQRRecyclerView getRvVideo() {
        return null;
    }

    @Override
    protected VideoFgPresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_contacts;
    }

    @Override
    public void init() {
        registerBR();

    }

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvContacts.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        contactsListAdapter = new ContactsListAdapter(getActivity());
        //contactsListAdapter.setAdapterData(BondCache.getContactList());
        rvContacts.setAdapter(contactsListAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsListAdapter.setAdapterData(BondCache.getContactList());
        contactsListAdapter.notifyDataSetChanged();
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
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_CONVERSATIONS);
    }
}
