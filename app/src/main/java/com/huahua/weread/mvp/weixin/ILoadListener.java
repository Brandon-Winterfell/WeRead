package com.huahua.weread.mvp.weixin;

/**
 * 加载情况的回调
 *
 * Created by Administrator on 2016/12/2.
 */

public interface ILoadListener<T> {

    void onSuccess(T data);

    void onFail(Throwable e);

}
