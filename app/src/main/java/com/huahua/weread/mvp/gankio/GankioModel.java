package com.huahua.weread.mvp.gankio;

import com.huahua.weread.bean.GankioFuliItem;
import com.huahua.weread.bean.GankioFuliResponse;
import com.huahua.weread.http.HttpManager;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioModel {


    public Subscription loadGankioFuli(int pageIndex, Subscriber<GankioFuliResponse> subscriber) {
         return HttpManager.getInstance().loadGankioFuli(pageIndex, subscriber);
    }


}























