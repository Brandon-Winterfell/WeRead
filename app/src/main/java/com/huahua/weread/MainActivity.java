package com.huahua.weread;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huahua.weread.mvp.about.AboutFragment;
import com.huahua.weread.mvp.gankio.GankioFragment;
import com.huahua.weread.mvp.guoke.GuokeFragment;
import com.huahua.weread.mvp.weixin.WeiXinFragment;
import com.huahua.weread.mvp.zhihu.ZhiHuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.content_main)
    RelativeLayout mContentMain;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private FragmentManager mFragmentManager;
    private WeiXinFragment mWeiXinFragment;
    private ZhiHuFragment mZhiHuFragment;
    private GuokeFragment mGuokeFragment;
    private GankioFragment mGankioFragment; // 这里是不是应该用懒加载 图片这么多数据
    private AboutFragment mAboutFragment;

    private String mCurrentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 这个有效吗
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // Title要在setSupportActionBar(mToolbar);这句前设置才有效
        mToolbar.setTitle("果壳热门");

        // 设置toolbar
        setSupportActionBar(mToolbar);
        // 设置toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        // NavView的item点击事件
        mNavView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (savedInstanceState != null) {
            // 解决Fragment重叠的问题 如果在后台被系统因内存不足而杀死 savedInstanceState是不为空的
            mGuokeFragment = (GuokeFragment) mFragmentManager.findFragmentByTag("mGuokeFragment");
            mWeiXinFragment = (WeiXinFragment) mFragmentManager.findFragmentByTag("mWeiXinFragment");
            mZhiHuFragment = (ZhiHuFragment) mFragmentManager.findFragmentByTag("mZhiHuFragment");
            mGankioFragment = (GankioFragment) mFragmentManager.findFragmentByTag("mGankioFragment");
            mAboutFragment = (AboutFragment) mFragmentManager.findFragmentByTag("mAboutFragment");
        }

        // 默认的Fragment  mWeiXinFragment等于空 应该是刚进来吧 打开默认就是mGuokeFragment
        if (mGuokeFragment == null) {
            // 添加果壳热门Fragment
            mGuokeFragment = new GuokeFragment();
            transaction.add(R.id.fragment_container, mGuokeFragment, "mGuokeFragment");

            // TODO 不如下面几个点他的时候 再初始化

            // 添加微信精选Fragment
            mWeiXinFragment = new WeiXinFragment();
            transaction.add(R.id.fragment_container, mWeiXinFragment, "mWeiXinFragment");
            // 添加知乎日报Fragment
            mZhiHuFragment = new ZhiHuFragment();
            transaction.add(R.id.fragment_container, mZhiHuFragment, "mZhiHuFragment");
            // 添加Gankio福利Fragment
            mGankioFragment = new GankioFragment();
            transaction.add(R.id.fragment_container, mGankioFragment, "mGankioFragment");
            // 添加关于页面Fragment
            mAboutFragment = new AboutFragment();
            transaction.add(R.id.fragment_container, mAboutFragment, "mAboutFragment");
            // 提交transaction
            transaction.commit();
        }

        /**设置MenuItem默认选中项**/
        mNavView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(mNavView.getMenu().getItem(0)); // 应该是一进来选中第一个item 不要上面的new Fragment 在item的选中事件中new Fragment
    }

    /**
     * 在nav菜单那设置岂不是更好
     *
     * @param currentTitle
     */
    private void setToolbarTitle(String currentTitle) {
        Log.i("我看看打印的是什么", currentTitle);
        if (currentTitle.equals("GuokeFragment")) {
            mToolbar.setTitle("果壳热门");
        } else if (currentTitle.equals("WeiXinFragment")) {
            mToolbar.setTitle("微信精选");
        } else if (currentTitle.equals("ZhiHuFragment")) {
            mToolbar.setTitle("知乎日报");
        } else if (currentTitle.equals("GankioFragment")) {
            mToolbar.setTitle("每日一福利");
        } else if (currentTitle.equals("AboutFragment")) {
            mToolbar.setTitle("关于");
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_guokehot) {

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mWeiXinFragment);
            transaction.hide(mZhiHuFragment);
            transaction.hide(mGankioFragment);
            transaction.hide(mAboutFragment);
            transaction.show(mGuokeFragment);
            transaction.commit();

            mCurrentTitle = mGuokeFragment.getClass().getSimpleName();
            setToolbarTitle(mCurrentTitle);

        } else if (id == R.id.nav_weixinjingxuan) {

            // 要hide其他Fragment
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mGuokeFragment);
            transaction.hide(mZhiHuFragment);
            transaction.hide(mGankioFragment);
            transaction.hide(mAboutFragment);
            transaction.show(mWeiXinFragment);
            transaction.commit();

            mCurrentTitle = mWeiXinFragment.getClass().getSimpleName();
            setToolbarTitle(mCurrentTitle);

        } else if (id == R.id.nav_zhihudaily) {

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mGuokeFragment);
            transaction.hide(mWeiXinFragment);
            transaction.hide(mGankioFragment);
            transaction.hide(mAboutFragment);
            transaction.show(mZhiHuFragment);
            transaction.commit();

            mCurrentTitle = mZhiHuFragment.getClass().getSimpleName();
            setToolbarTitle(mCurrentTitle);

        } else if (id == R.id.nav_fuli) {

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mGuokeFragment);
            transaction.hide(mWeiXinFragment);
            transaction.hide(mZhiHuFragment);
            transaction.hide(mAboutFragment);
            transaction.show(mGankioFragment);
            transaction.commit();

            mCurrentTitle = mGankioFragment.getClass().getSimpleName();
            setToolbarTitle(mCurrentTitle);

        } else if (id == R.id.nav_theme) {

            Toast.makeText(this, "施工准备中，正在奔来的路上。。", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_settings) {

            Toast.makeText(this, "施工准备中，正在奔来的路上。。", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_about) {

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mGuokeFragment);
            transaction.hide(mWeiXinFragment);
            transaction.hide(mZhiHuFragment);
            transaction.hide(mGankioFragment);
            transaction.show(mAboutFragment);
            transaction.commit();

            mCurrentTitle = mAboutFragment.getClass().getSimpleName();
            setToolbarTitle(mCurrentTitle);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
