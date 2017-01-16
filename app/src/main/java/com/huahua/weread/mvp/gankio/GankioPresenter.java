package com.huahua.weread.mvp.gankio;

import com.huahua.weread.bean.GankioFuliResponse;
import com.huahua.weread.utils.LogUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/8.
 */

public class GankioPresenter {

    private IGankioView mView;
    private GankioModel mModel;

    private Subscription mSubscription;  // 用来解绑  如果执行了onerror 或oncompleted自动解绑的

    private Boolean isHasOnErrorReturn = false;

    public GankioPresenter(IGankioView view) {
        mView = view;
        mModel = new GankioModel();
    }


    // GankioPresenter能干什么了， 干了些什么了

    public void loadGankioFuli(int pageIndex) {
        // 真正去干这事的是model
        mSubscription = mModel.loadGankioFuli(pageIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, GankioFuliResponse>() {
                    @Override
                    public GankioFuliResponse call(Throwable throwable) {
                        LogUtils.LOGI("GankioPresenter", ">>>>>  onErrorReturn   >>>>>");
                        throwable.printStackTrace();
                        isHasOnErrorReturn = true;
                        return null;
                    }
                })
                .subscribe(new Subscriber<GankioFuliResponse>() {
                    @Override
                    public void onCompleted() {
                        isHasOnErrorReturn = false;
                        // 隐藏加载更多底部布局或者下拉刷新控件
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        isHasOnErrorReturn = false;
                        mView.showLoadFailMsg("出错了,请稍后再试");
                        // 隐藏加载更多底部布局或者下拉刷新控件
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onNext(GankioFuliResponse gankioFuliResponse) {
                        if (gankioFuliResponse == null) {
                            if (isHasOnErrorReturn) {
                                // observable发生了错误
                            }
                            mView.showLoadFailMsg("出错了,请稍后再试");
                            return;
                        }

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
        mView = null;  // 这样就没有Fragment（activity）的引用啦
    }

}

























