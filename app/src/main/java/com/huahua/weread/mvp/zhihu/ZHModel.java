package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.ZhiHuResponse;
import com.huahua.weread.http.HttpManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * 知乎日报 model层
 *
 * Created by Administrator on 2016/12/6.
 */

public class ZHModel {

    // 获取最新日期的日报
    public Observable<ZhiHuResponse> loadZhiHuDailyLast() {
        return HttpManager.getInstance().loadZhiHuDaily();

    }

    // 获取某个日期前的知乎日报  传了今天的日期获取昨天的日报
    public Observable<ZhiHuResponse> loadTheDaily(String date) {
        return HttpManager.getInstance().loadTheDaily(date);
    }

}

























