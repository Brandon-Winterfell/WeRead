package com.huahua.weread.mvp.gankio;

import com.huahua.weread.bean.GankioFuliResponse;
import com.huahua.weread.http.HttpManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioModel {


    public Observable<GankioFuliResponse> loadGankioFuli(int pageIndex) {
         return HttpManager.getInstance().loadGankioFuli(pageIndex);
    }


}























