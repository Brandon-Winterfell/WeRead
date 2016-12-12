package com.huahua.weread.mvp.gankio.fuliGallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.huahua.weread.bean.GankioFuliItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看单张图片的Fragment的adapter
 *
 * Created by Administrator on 2016/10/12.
 */
public class FragmentViewAdapter extends FragmentStatePagerAdapter {

    List<GankioFuliItem> mGirlList = new ArrayList<GankioFuliItem>();

    public FragmentViewAdapter(FragmentManager fm, List<GankioFuliItem> girlList) {
        super(fm);
        mGirlList = girlList;

    }

    @Override
    public Fragment getItem(int position) {
        // 传对应位置图片的url
        String url = mGirlList.get(position).getUrl();
        // 传对应位置图片在图片集合的索引
        String index = (position + 1 ) + "/" + mGirlList.size();
        // Return the Fragment associated with a specified position.
        return fragmentView.newInstance(url, index);

    }

    @Override
    public int getCount() {
        return mGirlList.size(); // Return the number of views available.
    }
}
