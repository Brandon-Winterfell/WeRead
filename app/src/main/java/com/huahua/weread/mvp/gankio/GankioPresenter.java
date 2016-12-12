package com.huahua.weread.mvp.gankio;

import android.util.Log;

import com.huahua.weread.bean.GankioFuliResponse;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioPresenter {

    private IGankioView mView;
    private GankioModel mModel;

    private Subscription mSubscription;  // 用来解绑  如果执行了onerror 或oncompleted自动解绑的

    public GankioPresenter(IGankioView view) {
        mView = view;
        mModel = new GankioModel();
    }


    // GankioPresenter能干什么了， 干了些什么了

    public void loadGankioFuli(int pageIndex) {
        // 真正去干这事的是model
        mSubscription = mModel.loadGankioFuli(pageIndex, new Subscriber<GankioFuliResponse>() {
            @Override
            public void onCompleted() {

            }

            // 这个匿名内部类持有activity的引用了 Subscription解绑会不会释放这个引用 不释放 是否就内存泄漏的可能
            // new Subscriber这个匿名内部类持有GankioPresenter的引用，而GankioPresenter持有GuokeFragment的引用，dangerous
            // 在Activity的onDestroy()方法中调用unsubscribe()方法，会阻止泄露的发生。
            // 然后你调用的是Fragment的onDestroyView吧，因为回调的是mView.xxx，view都没了，空指针
            @Override
            public void onError(Throwable e) {
                mView.hideProgressDialog();
                mView.showLoadFailMsg(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(GankioFuliResponse gankioFuliResponse) {
                mView.hideProgressDialog();
                mView.updateSuccessData(gankioFuliResponse.getResults());
            }
        });
    }

    /**
     * 在onDestroyview执行这个
     */
    public void unBindSubscription() {
        if (mSubscription != null) {
                mSubscription.unsubscribe();
        }
    }

}

























