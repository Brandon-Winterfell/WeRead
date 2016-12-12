package com.huahua.weread.mvp.gankio.fuliGallery;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.huahua.weread.R;
import com.huahua.weread.bean.GankioFuliItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/25.
 */

public class ActivityImageView extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ViewPager mViewPager;

    private int currentPosition = 0;

    private List<GankioFuliItem> mGirlList = new ArrayList<GankioFuliItem>();
    private FragmentViewAdapter mFragmentViewAdapter;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = this.getWindow();
//            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.black));
//        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("福利");
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // 拿到ViewPager需要首先呈现页卡的位置参数
        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        // 拿到数据集
        mGirlList = (List<GankioFuliItem>) getIntent().getSerializableExtra("girlist");
        Log.i("test", "url : " + mGirlList.toString());


        mViewPager = (ViewPager) findViewById(R.id.viewpager_girl);
        // 创建适配器
        mFragmentViewAdapter = new FragmentViewAdapter(getSupportFragmentManager(), mGirlList);
        // 设置ViewPager的适配器
        mViewPager.setAdapter(mFragmentViewAdapter);
        // 设置ViewPager首先呈现的页卡
        mViewPager.setCurrentItem(currentPosition);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 要是直接按home键 也设置了parent activity的话 会重新加载一次parent activity
                // 调用onBackPressed就不会重新加载一次父activity了  回到在内存中的父activity实例
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}



















