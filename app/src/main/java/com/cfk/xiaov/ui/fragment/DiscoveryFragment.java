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
import com.cfk.xiaov.ui.base.BaseFragment;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.lqr.optionitemview.OptionItemView;

import butterknife.BindView;

/**
 * @创建者 Sean
 * @描述 发现界面
 */
public class DiscoveryFragment extends BaseFragment {

    String TAG = getClass().getSimpleName();
    @BindView(R.id.oivScan)
    OptionItemView mOivScan;
    @BindView(R.id.discovery_view)
    WebView mDiscoveryView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

//    String url = "http://m.kugou.com/";
//    String url = "http://music.163.com/";
//    String url = "https://y.qq.com/";
    String url = "http://music.baidu.com/";

    @Override
    public void initListener() {
        mFab.setOnClickListener(v -> Log.i(TAG,mDiscoveryView.getUrl()));
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
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
    protected int provideContentViewId() {
        return R.layout.fragment_discovery;
    }
}
