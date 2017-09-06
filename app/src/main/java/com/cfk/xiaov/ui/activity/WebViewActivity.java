package com.cfk.xiaov.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.widget.ProgressWebView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 内置浏览器界面
 */
public class WebViewActivity extends BaseActivity {

    private Intent mIntent;
    private Bundle mExtras;
    private String mUrl;
    private String mTitle;

    private boolean isLoading = false;

    @Bind(com.cfk.xiaov.R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;
    @Bind(com.cfk.xiaov.R.id.webview)
    public ProgressWebView mWebView;

    @Override
    public void init() {
        //得到url
        try {
            mIntent = getIntent();
            mExtras = mIntent.getExtras();
            if (mExtras == null) {
                finish();
                return;
            }
            mUrl = mExtras.getString("url");
            if (TextUtils.isEmpty(mUrl)) {
                finish();
                return;
            }
            mTitle = mExtras.getString("title");
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }
    }

    @Override
    public void initView() {
        mIbToolbarMore.setVisibility(View.VISIBLE);
        //设置webView
        WebSettings settings = mWebView.getSettings();
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMinimumFontSize(settings.getMinimumLogicalFontSize() + 8);
        settings.setAllowFileAccess(false);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mUrl);
        setToolbarTitle(TextUtils.isEmpty(mTitle) ? mWebView.getTitle() : mTitle);
    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> showShare());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_webview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        isLoading = false;

        //如果当前浏览器可以后退，则后退上一个页面
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            try {
                mWebView.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mWebView = null;
        }
    }

    private void initToolbar() {

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //在自己浏览器中跳转
            mWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isLoading = true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isLoading = false;
        }
    }

    private void showShare() {
    }

}
