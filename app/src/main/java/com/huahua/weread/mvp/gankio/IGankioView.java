package com.huahua.weread.mvp.gankio;

import com.huahua.weread.bean.GankioFuliItem;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */

public interface IGankioView {

    /**
     * 显示正在加载进度条
     */
    public abstract void showProgressDialog();

    /**
     * 隐藏进度条
     */
    void hideProgressDialog();

    /**
     * 显示成功加载的数据
     * @param gankioFuliItemList
     */
    void updateSuccessData(List<GankioFuliItem> gankioFuliItemList);

    /**
     * 显示加载失败
     */
    void showLoadFailMsg(String errMessage);

}




















