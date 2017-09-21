package com.cfk.xiaov.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.activity.adapter.DeviceManagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceManagerActivity extends Activity {

    @Bind(R.id.rvDevices)
    RecyclerView mrvDevices;
    @Bind(com.cfk.xiaov.R.id.ivToolbarNavigation)
    public ImageView mToolbarNavigation;
    DeviceManagerAdapter deviceManagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        ButterKnife.bind(this);
        mrvDevices.setLayoutManager(new LinearLayoutManager(this));
        mrvDevices.setItemAnimator(new DefaultItemAnimator());
        deviceManagerAdapter = new DeviceManagerAdapter(this);
        //contactsListAdapter.setAdapterData(BondCache.getContactList());
        mrvDevices.setAdapter(deviceManagerAdapter);


        mToolbarNavigation.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceManagerAdapter.setAdapterData(BondCache.getContactList());
        deviceManagerAdapter.notifyDataSetChanged();
    }
}
