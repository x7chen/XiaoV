package com.cfk.xiaov.ui.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.rest.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.db.BondDevice;
import com.cfk.xiaov.misc.exception.ServerException;
import com.cfk.xiaov.rest.model.request.PushRequest;
import com.cfk.xiaov.rest.model.response.GetUserInfoResponse;
import com.cfk.xiaov.ui.adapter.CommonFragmentPagerAdapter;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.fragment.FragmentFactory;
import com.cfk.xiaov.business.service.VideoCallService;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.NetUtils;
import com.cfk.xiaov.util.PopupWindowUtils;
import com.cfk.xiaov.util.UIUtils;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.cfk.xiaov.util.ResponseBodyStore.writeResponseBodyToDisk;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    String TAG = getClass().getSimpleName();
    private List<BaseFragment> mFragmentList = new ArrayList<>(4);

    @BindView(com.cfk.xiaov.R.id.ibAddMenu)
    ImageButton mIbAddMenu;
    @BindView(com.cfk.xiaov.R.id.vpContent)
    ViewPager mVpContent;
    @BindView(R.id.status_message_layout)
    RelativeLayout status_msg_layout;
    @BindView(R.id.status_message)
    TextView statusMessage;

    //底部
    @BindView(com.cfk.xiaov.R.id.llMessage)
    LinearLayout mLlMessage;
    @BindView(com.cfk.xiaov.R.id.tvMessageNormal)
    TextView mTvMessageNormal;
    @BindView(com.cfk.xiaov.R.id.tvMessagePress)
    TextView mTvMessagePress;
    @BindView(com.cfk.xiaov.R.id.tvMessageTextNormal)
    TextView mTvMessageTextNormal;
    @BindView(com.cfk.xiaov.R.id.tvMessageTextPress)
    TextView mTvMessageTextPress;
    @BindView(com.cfk.xiaov.R.id.tvMessageCount)
    public TextView mTvMessageCount;

    @BindView(com.cfk.xiaov.R.id.llDiscovery)
    LinearLayout mLlDiscovery;
    @BindView(com.cfk.xiaov.R.id.tvDiscoveryNormal)
    TextView mTvDiscoveryNormal;
    @BindView(com.cfk.xiaov.R.id.tvDiscoveryPress)
    TextView mTvDiscoveryPress;
    @BindView(com.cfk.xiaov.R.id.tvDiscoveryTextNormal)
    TextView mTvDiscoveryTextNormal;
    @BindView(com.cfk.xiaov.R.id.tvDiscoveryTextPress)
    TextView mTvDiscoveryTextPress;
    @BindView(com.cfk.xiaov.R.id.tvDiscoveryCount)
    public TextView mTvDiscoveryCount;

    @BindView(com.cfk.xiaov.R.id.llMe)
    LinearLayout mLlMe;
    @BindView(com.cfk.xiaov.R.id.tvMeNormal)
    TextView mTvMeNormal;
    @BindView(com.cfk.xiaov.R.id.tvMePress)
    TextView mTvMePress;
    @BindView(com.cfk.xiaov.R.id.tvMeTextNormal)
    TextView mTvMeTextNormal;
    @BindView(com.cfk.xiaov.R.id.tvMeTextPress)
    TextView mTvMeTextPress;
    @BindView(com.cfk.xiaov.R.id.tvMeCount)
    public TextView mTvMeCount;

    @Override
    public void init() {
        if (MyApp.getSplashActivity() != null) {
            MyApp.getSplashActivity().finish();
        }
        registerBR();
        startVideoCallService();
    }

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.app_name));
        mIbAddMenu.setVisibility(View.VISIBLE);

        //等待全局数据获取完毕
//        showWaitingDialog(UIUtils.getString(com.cfk.xiaov.R.string.please_wait));
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//                runOnUiThread(this::hideWaitingDialog);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
        //默认选中第一个
        setTransparency();
        mTvMessagePress.getBackground().setAlpha(255);
        mTvMessageTextPress.setTextColor(Color.argb(255, 38, 170, 253));

        //设置ViewPager的最大缓存页面
        mVpContent.setOffscreenPageLimit(3);

//        mFragmentList.add(FragmentFactory.getInstance().getRecentMessageFragment());
        mFragmentList.add(FragmentFactory.getInstance().getContactsFragment());
        mFragmentList.add(FragmentFactory.getInstance().getDiscoveryFragment());
        mFragmentList.add(FragmentFactory.getInstance().getMeFragment());
        mVpContent.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList));
        checkNetwork();
    }

    @Override
    public void initListener() {
        mIbAddMenu.setOnClickListener(v -> {
            //显示或隐藏popupwindow
            View menuView = View.inflate(MainActivity.this, com.cfk.xiaov.R.layout.menu_main, null);
            PopupWindow popupWindow = PopupWindowUtils.getPopupWindowAtLocation(menuView, getWindow().getDecorView(), Gravity.TOP | Gravity.RIGHT, UIUtils.dip2Px(5), mAppBar.getHeight() + 30);
            menuView.findViewById(com.cfk.xiaov.R.id.tvHelpFeedback).setOnClickListener(v1 -> {
                jumpToWebViewActivity(AppConstants.MyUrl.HELP_FEED_BACK);
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvScan).setOnClickListener(v1 -> {
                Intent intent = new Intent(MyApp.getContext(), CaptureActivity.class);
                intent.setAction(Intents.Scan.ACTION);
                startActivityForResult(intent, 1001);
                popupWindow.dismiss();
            });

        });

        mLlMessage.setOnClickListener(v -> bottomBtnClick(v));
        mLlDiscovery.setOnClickListener(v -> bottomBtnClick(v));
        mLlMe.setOnClickListener(v -> bottomBtnClick(v));
        mVpContent.setOnPageChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {
            String result = data.getStringExtra("qr_result");
            if (result.startsWith(AppConstants.QrCodeCommon.BOND)) {
                String targetId = result.substring(AppConstants.QrCodeCommon.BOND.length());

                if (AppConstants.Solution.equals(AppConstants.Agora)) {
                    Intent intent = new Intent(MainActivity.this, InviteActivity.class);
                    intent.putExtra("account", targetId);
                    startActivity(intent);
                } else if (AppConstants.Solution.equals(AppConstants.Tencent)) {
                    saveNewDevice(targetId);
                }
            }
        }
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    void saveNewDevice(String targetId) {
        //获取用户信息
        final String[] account = new String[1];
        final String[] nickname = new String[1];
        ApiRetrofit.getInstance().getUserInfoById(targetId)
                .flatMap(getUserInfoResponse -> {
                    if (getUserInfoResponse != null && getUserInfoResponse.getCode() == 200) {
                        GetUserInfoResponse.ResultEntity res = getUserInfoResponse.getResult();
                        account[0] = res.getId();
                        nickname[0] = res.getNickname();
                        return ApiRetrofit.getInstance().getQiNiuDownloadUrl(res.getPortraitUri());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown))));
                    }
                })
                .flatMap(qiNiuDownloadResponse -> {
                    if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                        return ApiRetrofit.getInstance().downloadPic(qiNiuDownloadResponse.getResult().getPrivateDownloadUrl());
                    } else {
                        return Observable.error(new ServerException((UIUtils.getString(R.string.unknown))));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ResponseBody -> {
                    BondDevice device = new BondDevice(account[0], nickname[0], writeResponseBodyToDisk(ResponseBody));
                    MyApp.getBondDeviceDao().insertOrReplace(device);
                    FragmentFactory.getInstance().getContactsFragment().updateView();
                }, this::loadError);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public void bottomBtnClick(View view) {
        setTransparency();
        switch (view.getId()) {
            case com.cfk.xiaov.R.id.llMessage:
                mVpContent.setCurrentItem(0, false);
                mTvMessagePress.getBackground().setAlpha(255);
                mTvMessageTextPress.setTextColor(Color.argb(255, 38, 170, 253));
                break;
            case com.cfk.xiaov.R.id.llDiscovery:
                mVpContent.setCurrentItem(1, false);
                mTvDiscoveryPress.getBackground().setAlpha(255);
                mTvDiscoveryTextPress.setTextColor(Color.argb(255, 38, 170, 253));
                break;
            case com.cfk.xiaov.R.id.llMe:
                mVpContent.setCurrentItem(2, false);
                mTvMePress.getBackground().setAlpha(255);
                mTvMeTextPress.setTextColor(Color.argb(255, 38, 170, 253));
                break;
        }
    }

    /**
     * 把press图片、文字全部隐藏(设置透明度)
     */
    private void setTransparency() {
        mTvMessageNormal.getBackground().setAlpha(255);
        mTvDiscoveryNormal.getBackground().setAlpha(255);
        mTvMeNormal.getBackground().setAlpha(255);

        mTvMessagePress.getBackground().setAlpha(1);
        mTvDiscoveryPress.getBackground().setAlpha(1);
        mTvMePress.getBackground().setAlpha(1);

        mTvMessageTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvDiscoveryTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvMeTextNormal.setTextColor(Color.argb(255, 153, 153, 153));

        mTvMessageTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvDiscoveryTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvMeTextPress.setTextColor(Color.argb(0, 69, 192, 26));
    }


    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_main;
    }

    @Override
    protected boolean isToolbarCanBack() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //根据ViewPager滑动位置更改透明度
        int diaphaneity_one = (int) (255 * positionOffset);
        int diaphaneity_two = (int) (255 * (1 - positionOffset));
        switch (position) {
            case 0:
                mTvMessageNormal.getBackground().setAlpha(diaphaneity_one);
                mTvMessagePress.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_one);
                mTvMessageTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                mTvMessageTextPress.setTextColor(Color.argb(diaphaneity_two, 38, 170, 253));
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_one, 38, 170, 253));
                break;
            case 1:
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_two);
                mTvMeNormal.getBackground().setAlpha(diaphaneity_two);
                mTvMePress.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_two, 38, 170, 253));
                mTvMeTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                mTvMeTextPress.setTextColor(Color.argb(diaphaneity_one, 38, 170, 253));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    void checkNetwork() {

        if (!NetUtils.isNetworkAvailable(this)) {
            showNetworkFail();
        } else {
            hideNetworkFail();
            if (!TextUtils.isEmpty(AccountCache.getUserSig())) {
                String account = AccountCache.getAccount();
                String userSig = AccountCache.getUserSig();
                MyApp.mAccountMgr.loginSDK(account, userSig);
            }
        }

    }

    void showNetworkFail() {
        status_msg_layout.setVisibility(View.VISIBLE);
        //statusMessage.setTextColor(0xffff00);
        statusMessage.setText("您的网络不可用！");
    }

    void hideNetworkFail() {
        status_msg_layout.setVisibility(View.GONE);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConstants.Action.AGREE_MY_INVITE)) {
                String json = intent.getStringExtra("json");
                PushRequest pushMessage = PushRequest.parserFromJson(json);

                saveNewDevice(pushMessage.getFrom());
            } else if (intent.getAction().equals(AppConstants.Action.FETCH_COMPLETE)) {
                hideWaitingDialog();
            } else if (intent.getAction().equals(AppConstants.Action.CONNECTIVITY_CHANGE_ACTION)) {
                checkNetwork();
            } else if (intent.getAction().equals(AppConstants.Action.AGREE_MY_INVITE)) {
                showNetworkFail();
            }
        }
    };

    void startVideoCallService() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        boolean isRunning = false;
        if (serviceList == null || serviceList.size() == 0) {
            isRunning = false;
        } else {
            for (ActivityManager.RunningServiceInfo info : serviceList) {
                // Log.i(TAG,info.service.getClassName());

                if (info.service.getClassName().equals(VideoCallService.class.getName())) {
                    isRunning = true;
                    break;
                }
            }
            // Log.i(TAG,VideoCallService.class.getName());
        }
        if (!isRunning) {
            Intent intent1 = new Intent(MyApp.ApplicationContext, VideoCallService.class);
            MyApp.ApplicationContext.startService(intent1);
        }
    }

    private void registerBR() {
        registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.FETCH_COMPLETE));
        registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.CONNECTIVITY_CHANGE_ACTION));
        registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.NET_STATUS));
        registerReceiver(broadcastReceiver, new IntentFilter(AppConstants.Action.AGREE_MY_INVITE));

    }


    private void unRegisterBR() {
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }
}
