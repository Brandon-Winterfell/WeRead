package com.huahua.weread.mvp.gankio;

import com.huahua.weread.bean.GankioFuliItem;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */

public interface IGankioView {

    /**
     * 显示成功加载的数据
     * @param gankioFuliItemList
     */
    void updateSuccessData(List<GankioFuliItem> gankioFuliItemList);

    /**
     * 显示加载失败
     */
    void showLoadFailMsg(String errMessage);

    /**
     * 最后根据是下拉更新还是加载更多而隐藏掉下拉刷新控件或者底部加载更多布局
     */
    void hideSwipeRefreshLayoutOrFooter();

}




















