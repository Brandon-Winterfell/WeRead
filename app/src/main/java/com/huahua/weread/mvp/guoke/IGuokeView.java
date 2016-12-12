package com.huahua.weread.mvp.guoke;

import com.huahua.weread.bean.GuokeHotItem;
import com.huahua.weread.bean.WeiXinNews;

import java.util.List;

/**
 * 果壳热门的 view层接口
 *
 * Created by Administrator on 2016/12/7.
 */

public interface IGuokeView {

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
     * @param guokeHotItemslist
     */
    void updateSuccessData(List<GuokeHotItem> guokeHotItemslist);

    /**
     * 显示加载失败
     */
    void showLoadFailMsg(String errMessage);

}

























