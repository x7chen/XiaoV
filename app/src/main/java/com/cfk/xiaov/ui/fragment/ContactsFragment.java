package com.cfk.xiaov.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.manager.BroadcastManager;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.fragment.adapter.ContactsListAdapter;
import com.cfk.xiaov.ui.presenter.VideoFgPresenter;
import com.cfk.xiaov.ui.view.IVideoFgView;
import com.cfk.xiaov.util.RongGenerate;
import com.lqr.recyclerview.LQRRecyclerView;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsFragment extends BaseFragment<IVideoFgView, VideoFgPresenter> implements IVideoFgView {
    String TAG = getClass().getSimpleName();
    @Bind(R.id.rvFriend)
    RecyclerView rvContacts;
    @Bind(R.id.bond_device_list)
    RecyclerView rvBondDevice;
    @Bind(R.id.bt_camera)
    ImageView ivCamera;

    @Bind(R.id.bond_device_view)
    RelativeLayout bondDeviceView;
    @Bind(R.id.not_bond_device_view)
    RelativeLayout notBondDeviceView;

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

        rvBondDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBondDevice.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        contactsListAdapter = new ContactsListAdapter(getActivity());
        //contactsListAdapter.setAdapterData(BondCache.getBondList());
        rvBondDevice.setAdapter(contactsListAdapter);

    }

    public void updateView() {
        if (TextUtils.isEmpty(BondCache.getBondId())) {
            //bondDeviceView.setVisibility(View.INVISIBLE);
            notBondDeviceView.setVisibility(View.VISIBLE);
        } else {
            //bondDeviceView.setVisibility(View.VISIBLE);
            notBondDeviceView.setVisibility(View.INVISIBLE);
            /*
            ApiRetrofit.getInstance().getUserInfoById(BondCache.getBondId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Log.i(TAG, "key:" + response.getResult().getPortraitUri());
                        if (response.getResult().getPortraitUri() == null) {
                            String pic = RongGenerate.generateDefaultAvatar(response.getResult().getNickname(), response.getResult().getId());
                            Glide.with(this).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivCamera) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    view.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                        } else {
                            ApiRetrofit.getInstance().getQiNiuDownloadUrl(response.getResult().getPortraitUri())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(qiNiuDownloadResponse -> {
                                        String pic;
                                        if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                                            pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                                            //Glide.with(this).load(pic).centerCrop().into(ivCamera);

                                        } else {
                                            pic = RongGenerate.generateDefaultAvatar(response.getResult().getNickname(), response.getResult().getId());
                                        }
                                        Log.i(TAG, "key:" + pic);
                                        Glide.with(this).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivCamera) {
                                            @Override
                                            protected void setResource(Bitmap resource) {
                                                RoundedBitmapDrawable circularBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                                circularBitmapDrawable.setCircular(true);
                                                view.setImageDrawable(circularBitmapDrawable);
                                            }
                                        });
                                    });
                        }

                    });
                    */
        }

        contactsListAdapter.setAdapterData(DBManager.getInstance().getFriends());
        contactsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void initListener() {
        super.initListener();
        ivCamera.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(AppConst.MAKE_CALL);
            intent.putExtra("CallId", BondCache.getBondId());
            getActivity().sendBroadcast(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
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
