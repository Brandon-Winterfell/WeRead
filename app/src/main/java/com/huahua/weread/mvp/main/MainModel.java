package com.huahua.weread.mvp.main;

import com.huahua.weread.bean.UpdateInfo;
import com.huahua.weread.bean.UpdateResponse;
import com.huahua.weread.http.HttpManager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/12.
 */

public class MainModel {

    public Observable<UpdateResponse<UpdateInfo>> checkAppVersion() {
        return HttpManager.getInstance().checkAppUpdate();
    }

}
