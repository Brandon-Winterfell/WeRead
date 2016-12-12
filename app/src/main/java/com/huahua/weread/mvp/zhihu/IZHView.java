package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.WeiXinNews;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;

import java.util.List;

/**
 * 知乎日报    view层的接口
 *
 * UI的操作就下面几个方法
 * Created by Administrator on 2016/12/2.
 */

public interface IZHView {

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
     * @param zhiHuResponses
     */
    void updateSuccessData(ZhiHuResponse zhiHuResponses);

    /**
     * 显示加载失败
     */
    void showLoadFailMsg(String errMessage);
}


















