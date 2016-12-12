package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;
import com.huahua.weread.http.HttpManager;

import rx.Subscriber;
import rx.Subscription;

/**
 * 知乎日报 model层
 *
 * Created by Administrator on 2016/12/6.
 */

public class ZHModel {

    /**
     * 需要传入一个subscriber观察者，作用是为了方便进行回调
     *
     * @param subscriber
     * @return
     */
    public Subscription loadZhiHuDailyLast(Subscriber<ZhiHuResponse> subscriber) {
        Subscription s = HttpManager.getInstance().loadZhiHuDaily(subscriber);
        return s;
    }


    public Subscription loadTheDaily(String date, Subscriber<ZhiHuResponse> subscriber) {
        Subscription s = HttpManager.getInstance().loadTheDaily(date, subscriber);
        return s;
    }

}

























