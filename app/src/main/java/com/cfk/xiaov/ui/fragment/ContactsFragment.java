package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.request.PushRequest;
import com.cfk.xiaov.model.response.PushResponse;
import com.cfk.xiaov.ui.ActionEvent.RecyclerViewClickListener;
import com.cfk.xiaov.ui.activity.VideoChatViewActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.fragment.adapter.ContactsListAdapter;
import com.cfk.xiaov.util.BroadcastUtils;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsFragment extends BaseFragment {
    String TAG = getClass().getSimpleName();
    @BindView(R.id.rvFriend)
    RecyclerView rvContacts;
    @BindView(R.id.bond_device_list)
    RecyclerView rvBondDevice;
    @BindView(R.id.bt_camera)
    ImageView ivCamera;

    @BindView(R.id.bond_device_view)
    RelativeLayout bondDeviceView;
    @BindView(R.id.not_bond_device_view)
    RelativeLayout notBondDeviceView;

    ContactsListAdapter contactsListAdapter;

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

        rvBondDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBondDevice.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        contactsListAdapter = new ContactsListAdapter(getActivity());
        //contactsListAdapter.setAdapterData(BondCache.getBondList());
        rvBondDevice.setAdapter(contactsListAdapter);
        rvBondDevice.setNestedScrollingEnabled(false);
        rvBondDevice.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), rvBondDevice, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent();
//                intent.setAction(AppConst.MAKE_CALL);
//                intent.putExtra("CallId", contactsListAdapter.getAdapterData().get(position).getAccount());
//                getContext().sendBroadcast(intent);
//                Log.i(TAG, "Monitor OnClick");
                Intent intent = new Intent(getContext(), VideoChatViewActivity.class);
                PushResponse.ResultEntity pushMessage = new PushResponse.ResultEntity(
                        AppConst.PUSH_METHOD_CALL,
                        AccountCache.getAccount(),
                        contactsListAdapter.getAdapterData().get(position).getAccount(),
                        AccountCache.getAccount()
                        );

                Type mType = new TypeToken<PushResponse.ResultEntity>() {
                }.getType();
                Gson gson = new Gson();
                String json = gson.toJson(pushMessage,mType);
                intent.putExtra("json", json);
                ApiRetrofit.getInstance().push(pushMessage.getChannel(),AppConst.PUSH_METHOD_CALL,pushMessage.getTo())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(pushResponse -> {},ContactsFragment.this::rxError);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void rxError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        BroadcastUtils.sendBroadcast(AppConst.NET_STATUS, "net_status", "failed");
    }
    public void updateView() {
        if (MyApp.getBondDeviceDao().count() == 0) {
            notBondDeviceView.setVisibility(View.VISIBLE);
        } else {
            notBondDeviceView.setVisibility(View.INVISIBLE);
        }

        contactsListAdapter.setAdapterData(MyApp.getBondDeviceDao().loadAll());
        // UIUtils.showToastSafely("device:"+MyApp.getBondDeviceDao().count()+MyApp.getBondDeviceDao().loadAll().get(0).getAvatarUri());
        contactsListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }


    @Override
    public void initData() {
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
                getConversations();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_CONVERSATIONS);
    }

    public void getConversations() {
        loadData();
        setAdapter();
    }

    private void loadData() {
    }


    private void setAdapter() {

    }
}
