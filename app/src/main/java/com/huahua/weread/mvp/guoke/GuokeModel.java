package com.huahua.weread.mvp.guoke;

import com.huahua.weread.bean.GuokeHotResponse;
import com.huahua.weread.http.HttpManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * 果壳的model层  真正去请求数据的地方   服务器or数据库
 *
 * Created by Administrator on 2016/12/7.
 */

public class GuokeModel {

    public Observable<GuokeHotResponse> loadGuokeHot(int offset) {
        return HttpManager.getInstance().loadGuokeHot(offset);
    }
}
























