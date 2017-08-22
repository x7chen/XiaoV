package com.cfk.xiaov.ui.fragment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.activity.ScanActivity;
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.presenter.DiscoveryFgPresenter;
import com.cfk.xiaov.ui.view.IDiscoveryFgView;
import com.lqr.optionitemview.OptionItemView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 发现界面
 */
public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter> implements IDiscoveryFgView {

    String TAG = getClass().getSimpleName();
    @Bind(R.id.oivScan)
    OptionItemView mOivScan;
    @Bind(R.id.oivShop)
    OptionItemView mOivShop;
    @Bind(R.id.oivGame)
    OptionItemView mOivGame;
    @Bind(R.id.discovery_view)
    WebView mDiscoveryView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

//    String url = "http://m.kugou.com/";
//    String url = "http://music.163.com/";
//    String url = "https://y.qq.com/";
    String url = "http://music.baidu.com/";

    @Override
    public void initListener() {
        mOivScan.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(ScanActivity.class));
        mOivShop.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.JD));
        mOivGame.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.GAME));
        mFab.setOnClickListener(v -> Log.i(TAG,mDiscoveryView.getUrl()));
    }

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        WebSettings webSettings = mDiscoveryView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mDiscoveryView.loadUrl(url);
//        mDiscoveryView.setWebChromeClient(new MyWebViewClient());
        mDiscoveryView.setWebViewClient(new MyWebViewClient());

    }
    private class  MyWebViewClient extends WebViewClient {
        MyWebViewClient(){
            super();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            //Log.i(TAG,request.getUrl().toString());
            return super.shouldOverrideUrlLoading(view, request);
        }
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
