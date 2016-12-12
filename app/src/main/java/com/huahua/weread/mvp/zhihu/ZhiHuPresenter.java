package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.ZhiHuResponse;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ZhiHuPresenter {

    private IZHView mView; // 因为要操纵UI==>view
    private ZHModel mModel; // 再加上一个model

    Subscription mSubscription; // 声明他出来主要是为了能执行解绑操作
    Subscription mSubscription2;

    /**
     * presenter的构造方法
     * 实例化两个成员变量 view 和 model
     * @param view
     */
    public ZhiHuPresenter(IZHView view) {
        mView = view;
        mModel = new ZHModel();
    }

    // presenter作为控制器，有什么功能呀，能干什么呀

    /**
     * 获取最新日期的日报
     */
    public void loadZhiHuDailyLast() {
        mSubscription = mModel.loadZhiHuDailyLast(new Subscriber<ZhiHuResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgressDialog();
                mView.showLoadFailMsg(e.getMessage());
            }

            @Override
            public void onNext(ZhiHuResponse zhiHuResponse) {
                mView.hideProgressDialog();
                mView.updateSuccessData(zhiHuResponse);
            }
        });
    }

    /**
     * 获取某个日期前的知乎日报  传了今天的日期获取昨天的日报
     * @param date
     */
    public void loadTheDaily(String date) {
        mModel.loadTheDaily(date, new Subscriber<ZhiHuResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgressDialog();
                mView.showLoadFailMsg(e.getMessage());
            }

            @Override
            public void onNext(ZhiHuResponse zhiHuResponse) {
                mView.hideProgressDialog();
                mView.updateSuccessData(zhiHuResponse);
            }
        });
    }

    /**
     * 要在destroyview里执行这个方法
     */
    public void unBindSubscriber() {
        if (mSubscription != null) {
                mSubscription.unsubscribe();
        }
        if (mSubscription2 != null) {
                mSubscription2.unsubscribe();
        }

    }

}





























