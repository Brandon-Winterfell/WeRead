package com.huahua.weread.mvp.zhihu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.huahua.weread.R;
import com.huahua.weread.bean.ZhiHuArticle;
import com.huahua.weread.http.HttpManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/9/18.
 */

public class ZhiHuArticleActivity extends AppCompatActivity {

    public static final int TYPE_ZHIHU = 1;
    public static final int TYPE_GUOKR = 2;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.webview)
    WebView mWebview;

    @BindView(R.id.nest)
    NestedScrollView mNest;

    @BindView(R.id.fabButton)
    FloatingActionButton mFabButton;

    private int mType;
    private String mId;
    private String mGuokeUrl = null; // 这是果壳文章的 本来想与下面那个区分的
    private String mHtmlUrl = null; // 直接访问html，用webview加载 // 这是知乎日报文章的 不用分了
    private String mTitle = null;

    private Subscription mSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihuarticle_webview);
        ButterKnife.bind(this);

        /**
         * 拿到intent传过来的数据  url和title
         */
        if (getIntent() != null) {
            Intent getIntent = getIntent();
            mType = getIntent.getIntExtra("type", 0);
            mId = getIntent.getStringExtra("id");
            mTitle = getIntent.getStringExtra("title");
        }


        setViews();

        if (mType == TYPE_ZHIHU) {
            // 直接在这个方法里面加载html页面了
            getHtml(mId);
        } else if (mType == TYPE_GUOKR) {
            StringBuilder builder = new StringBuilder("http://www.guokr.com/article/");
            mHtmlUrl = builder.append(mId).append("/").toString();

            // webview去加载这个html页面
            mWebview.loadUrl(mHtmlUrl);
        }
    }

    /**
     * 对view控件的设置
     */
    private void setViews() {
        // setTitle得在setSupportActionBar前设置
        mToolbar.setTitle(mTitle);
        // toolbar的设置
        setSupportActionBar(mToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_cancel);


        // fabButton  滑动到顶部
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNest.smoothScrollTo(0, 0);
            }
        });

        // webview的设置
        mWebview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true); // 设置使支持缩放
        webSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDisplayZoomControls(false); // 隐藏原生的缩放控件
        webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        webSettings.setAllowFileAccess(true); // 设置访问文件
        // 覆盖webview默认使用第三方或系统默认浏览器打开网页的行为
        // 使网页用webview打开
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                mToolbar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }

    /**
     * 返回的是json数据，从中拿到mHtmlUrl链接，然后再用webview加载
     *
     * @param articleId
     */
    private void getHtml(String articleId) {
        // 增加个mSubscription字段，主要是为了解绑操作
        mSubscription = HttpManager.getInstance().loadZhihuArticle(articleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, ZhiHuArticle>() {
                    @Override
                    public ZhiHuArticle call(Throwable throwable) {
                        // 相当于try catch
                        Toast.makeText(ZhiHuArticleActivity.this,
                                "出错了,请稍后重试", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                })
                .subscribe(new Subscriber<ZhiHuArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ZhiHuArticleActivity.this,
                                "出错了,请稍后重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ZhiHuArticle zhiHuArticle) {
                        if (zhiHuArticle == null) {
                            // observable发生了错误，会发送一个null过来的
                            return;
                        }

                        mHtmlUrl = zhiHuArticle.getShare_url();
                        mWebview.loadUrl(mHtmlUrl);

                    }
                });
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, mTitle + " " + mHtmlUrl + getString(R.string.share_tail));
                shareIntent.setType("text/plain");
                // 设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (mWebview.canGoBack()) {
            mWebview.goBack(); // 返回上一级浏览的界面 不然直接就回到父activity了
        } else {
            super.onBackPressed(); // 直接回到父activity
        }
    }

    @Override
    protected void onDestroy() {
        // 解绑操作 如果执行了onerror 或者 onfail 自己会自动解绑
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
//        // webview内存泄漏  // 这样子还是会内存泄漏的
        // See http://www.cnblogs.com/whoislcj/p/6001422.html
//        if (mWebview != null) {
//            ((ViewGroup)mWebview.getParent()).removeView(mWebview);
//            mWebview.destroy();
//            mWebview = null;
//        }

        destroyWebView();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    private void destroyWebView() {
        if (mWebview != null) {
            mWebview.pauseTimers();
            mWebview.removeAllViews();
            mWebview.destroy();
            mWebview = null;
        }
    }
}





























