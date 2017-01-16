package com.huahua.weread;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.huahua.weread.bean.UpdateInfo;
import com.huahua.weread.mvp.about.AboutFragment;
import com.huahua.weread.mvp.gankio.GankioFragment;
import com.huahua.weread.mvp.guoke.GuokeFragment;
import com.huahua.weread.mvp.main.IMainView;
import com.huahua.weread.mvp.main.MainPresenter;
import com.huahua.weread.mvp.weixin.WeiXinFragment;
import com.huahua.weread.mvp.zhihu.ZhiHuFragment;
import com.huahua.weread.service.UpdateService;
import com.huahua.weread.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IMainView, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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

    private Fragment mCurrentFragment;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    private MainPresenter mPresenter;

    private static final int RC_WRITE = 100;

    private String mAPKUrl;
    private String mAPKFileName;

    Long exitTime = 0L ; // 按两次回退键退出程序

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
        if (savedInstanceState != null) {
            // 解决Fragment重叠的问题 如果在后台被系统因内存不足而杀死 savedInstanceState是不为空的
            mGuokeFragment = (GuokeFragment) mFragmentManager.findFragmentByTag("mGuokeFragment");
            mWeiXinFragment = (WeiXinFragment) mFragmentManager.findFragmentByTag("mWeiXinFragment");
            mZhiHuFragment = (ZhiHuFragment) mFragmentManager.findFragmentByTag("mZhiHuFragment");
            mGankioFragment = (GankioFragment) mFragmentManager.findFragmentByTag("mGankioFragment");
            mAboutFragment = (AboutFragment) mFragmentManager.findFragmentByTag("mAboutFragment");

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (mGuokeFragment != null) {
                mFragments.add(mGuokeFragment);
                transaction.add(R.id.fragment_container, mGuokeFragment, "mGuokeFragment");
            }
            if (mWeiXinFragment != null) {
                mFragments.add(mWeiXinFragment);
                transaction.add(R.id.fragment_container, mWeiXinFragment, "mWeiXinFragment");
            }
            if (mZhiHuFragment != null) {
                mFragments.add(mZhiHuFragment);
                transaction.add(R.id.fragment_container, mZhiHuFragment, "mZhiHuFragment");
            }
            if (mGankioFragment != null) {
                mFragments.add(mGankioFragment);
                transaction.add(R.id.fragment_container, mGankioFragment, "mGankioFragment");
            }
            if (mAboutFragment != null) {
                mFragments.add(mAboutFragment);
                transaction.add(R.id.fragment_container, mAboutFragment, "mAboutFragment");
            }
            transaction.commit();
        }

        /**设置MenuItem默认选中项**/
        mNavView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(mNavView.getMenu().getItem(0)); // 应该是一进来选中第一个item 不要上面的new Fragment 在item的选中事件中new Fragment

        mPresenter = new MainPresenter(this);

        /**
         * 应该这样子 先加载缓存 判断有网络连接后 再请求网络
         * 这里只是检测了网络链接，如果网络没有连接给出提示； 有连接的话就检测更新
         */
        if (!NetUtils.isNetworkConnected(this)) {
            Toast.makeText(this, "网络异常，请检查网络连接", Toast.LENGTH_LONG).show();
        } else {
            // 检测更新
            mPresenter.checkappVersion();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    /**
     * 隐藏所有的Fragment
     * @param transaction
     */
    private void hideAllFragments(FragmentTransaction transaction) {
        if (mFragments == null) return;
        if (mFragments.size() == 0) {
            return;
        }
        for (Fragment f : mFragments) {
            transaction.hide(f);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (id == R.id.nav_guokehot) {

            if (mGuokeFragment == null) {
                mGuokeFragment = new GuokeFragment();
                mFragments.add(mGuokeFragment);
                transaction.add(R.id.fragment_container, mGuokeFragment, "mGuokeFragment");
            }

            // 本来是这个，有颜色区分的，还点这个，不会这么傻吧，不会这么频繁的点吧
            // 如果本来就是这个呢  如果两个都为空了  第一次进来的时候两个都为空吧 放在这里就可以了
            if (mCurrentFragment == mGuokeFragment) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            hideAllFragments(transaction);
            transaction.show(mGuokeFragment);
            mCurrentFragment = mGuokeFragment;

            mToolbar.setTitle("果壳热门");

        } else if (id == R.id.nav_weixinjingxuan) {
            if (mCurrentFragment == mWeiXinFragment) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            if (mWeiXinFragment == null) {
                mWeiXinFragment = new WeiXinFragment();
                mFragments.add(mWeiXinFragment);
                transaction.add(R.id.fragment_container, mWeiXinFragment, "mWeiXinFragment");
            }
            hideAllFragments(transaction);
            transaction.show(mWeiXinFragment);
            mCurrentFragment = mWeiXinFragment;
            mToolbar.setTitle("微信精选");

        } else if (id == R.id.nav_zhihudaily) {

            if (mCurrentFragment == mZhiHuFragment) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            if (mZhiHuFragment == null) {
                mZhiHuFragment = new ZhiHuFragment();
                mFragments.add(mZhiHuFragment);
                transaction.add(R.id.fragment_container, mZhiHuFragment, "mZhiHuFragment");
            }
            hideAllFragments(transaction);
            transaction.show(mZhiHuFragment);
            mCurrentFragment = mZhiHuFragment;
            mToolbar.setTitle("知乎日报");

        } else if (id == R.id.nav_fuli) {

            if (mCurrentFragment == mGankioFragment) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            if (mGankioFragment == null) {
                mGankioFragment = new GankioFragment();
                mFragments.add(mGankioFragment);
                transaction.add(R.id.fragment_container, mGankioFragment, "mGankioFragment");
            }
            hideAllFragments(transaction);
            transaction.show(mGankioFragment);
            mCurrentFragment = mGankioFragment;
            mToolbar.setTitle("福利");

        } else if (id == R.id.nav_about) {

            if (mCurrentFragment == mAboutFragment) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            if (mAboutFragment == null) {
                mAboutFragment = new AboutFragment();
                mFragments.add(mAboutFragment);
                transaction.add(R.id.fragment_container, mAboutFragment, "mAboutFragment");
            }
            hideAllFragments(transaction);
            transaction.show(mAboutFragment);
            mCurrentFragment = mAboutFragment;
            mToolbar.setTitle("关于");

        }

        transaction.commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // IMainView的回调方法
    @Override
    public void showUpdateDialog(UpdateInfo updateInfo) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            int currentVersionCode = pi.versionCode;

            if (currentVersionCode < updateInfo.getVersionCode()) {
                String content = "版本号: v" + updateInfo.getVersionName() + "\r\n" +
                        "版本大小: " + updateInfo.getSize() + "\r\n" +
                        "更新内容: \r\n" + updateInfo.getReleaseNote().replace("\\r\\n", "\r\n");

                mAPKUrl = updateInfo.getDownloadUrl(); // 需要传到service里去下载
                mAPKFileName = updateInfo.getFileName();  // 需要传到service里保存文件时使用
                // 显示是否更新对话框
                toShowUpdateDialog(content);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void toShowUpdateDialog(String versionContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("检测到新版本");
        builder.setMessage(versionContent);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("马上更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateAppTask();
            }
        });
        // show出来
        builder.show();
    }

    // @AfterPermissionGranted(RC_WRITE)
    // 这样应该就不会有两个下载任务了吧
    public void updateAppTask() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent(this, UpdateService.class);
            intent.putExtra("apkUrl", mAPKUrl);
            intent.putExtra("apkFileName", mAPKFileName);
            startService(intent);
        } else {
            EasyPermissions.requestPermissions(this, "下载应用需要文件写入权限哦~",
                    RC_WRITE, perms);
        }
    }


    // EasyPermissions.PermissionCallbacks的三个回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (RC_WRITE == requestCode) {
            Intent intent = new Intent(this, UpdateService.class);
            intent.putExtra("apkUrl", mAPKUrl);
            intent.putExtra("apkFileName", mAPKFileName);
            startService(intent);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_WRITE)
                    .build()
                    .show();
        }
    }
}





















