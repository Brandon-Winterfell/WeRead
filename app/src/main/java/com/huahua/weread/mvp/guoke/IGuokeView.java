package com.huahua.weread.mvp.guoke;

import com.huahua.weread.bean.GuokeHotItem;

import java.util.List;

/**
 * 果壳热门的 view层接口
 *
 * Created by Administrator on 2016/12/7.
 */

public interface IGuokeView {

    /**
     * 显示成功加载的数据
     * @param guokeHotItemslist
     */
    void updateSuccessData(List<GuokeHotItem> guokeHotItemslist);

    /**
     * 显示加载失败
     */
    void showMsgWithLongToast(String errMessage);

    /**
     * 最后根据是下拉更新还是加载更多而隐藏掉下拉刷新控件或者底部加载更多布局
     */
    void hideSwipeRefreshLayoutOrFooter();

}

























