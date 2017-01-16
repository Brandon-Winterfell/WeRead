package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.ZhiHuResponse;

/**
 * 知乎日报    view层的接口
 *
 * UI的操作就下面几个方法
 * Created by Administrator on 2016/12/2.
 */

public interface IZHView {

    /**
     * 显示成功加载的数据
     * @param zhiHuResponses
     */
    void updateSuccessData(ZhiHuResponse zhiHuResponses);

    /**
     * 显示加载失败
     */
    void showLoadFailMsg(String errMessage);

    /**
     * 最后根据是下拉更新还是加载更多而隐藏掉下拉刷新控件或者底部加载更多布局
     */
    void hideSwipeRefreshLayoutOrFooter();
}


















