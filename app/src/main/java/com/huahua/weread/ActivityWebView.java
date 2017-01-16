package com.huahua.weread;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by Administrator on 2016/9/18.
 */

public class ActivityWebView extends AppCompatActivity {

    private Toolbar toolbar = null;
    private NestedScrollView mNestedScrollView;
    private FloatingActionButton mFab;
    private WebView mWebView;
    private String mUrl = null;
    private String mTitle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weixin_webview);

        /**
         * 拿到intent传过来的数据  url和title
         */
        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra("url");
            mTitle = getIntent().getStringExtra("title");
            Log.i("test", "url : " + mUrl);
        }

        // 初始化以及设置view
        setViews();
    }

    private void setViews() {
        /**
         * 设置toolbar
         */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 这个要在setSupportActionBar前
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_cancel);

        /**
         * 点击FloatingActionButton滚动到顶部
         */
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nest);
        mFab = (FloatingActionButton) findViewById(R.id.fabButton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestedScrollView.smoothScrollTo(0, 0);
            }
        });

        /**
         * 设置webview
         */
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl(mUrl);

        mWebView.setScrollbarFadingEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true); // 设置使支持缩放
        webSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDisplayZoomControls(false); // 隐藏原生的缩放控件
        webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        webSettings.setAllowFileAccess(true); // 设置访问文件
        // 覆盖webview默认使用第三方或系统默认浏览器打开网页的行为
        // 使网页用webview打开
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                toolbar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack(); // 返回上一级浏览的界面 不然直接就回到父activity了
        } else {
            super.onBackPressed(); // 直接回到父activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mTitle + " " + mUrl + getString(R.string.share_tail));
                shareIntent.setType("text/plain");
                // 设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // webview内存泄漏  // 亲测 只写这四行还是会造成内存泄漏的
        // See http://www.cnblogs.com/whoislcj/p/6001422.html
//        if (mWebView != null) {
//            ((ViewGroup)mWebView.getParent()).removeView(mWebView);
//            mWebView.destroy();
//            mWebView = null;
//        }


        destroyWebView();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    private void destroyWebView() {
        if (mWebView != null) {
            mWebView.pauseTimers();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }
}





























