package com.huahua.weread.mvp.weixin;

import com.huahua.weread.bean.WeiXinNews;

import java.util.List;

/**
 * 微信精选的    view层的接口
 *
 * UI的操作就下面几个方法
 * Created by Administrator on 2016/12/2.
 */

public interface IWXView {

    /**
     * 显示成功加载的数据
     * @param wxNewslist
     */
    void updateSuccessData(List<WeiXinNews> wxNewslist);

    /**
     * 显示加载失败
     */
    void showMsgWithLongToast(String message);

    /**
     * 最后根据是下拉更新还是加载更多而隐藏掉下拉刷新控件或者底部加载更多布局
     */
    void hideSwipeRefreshLayoutOrFooter();
}


















