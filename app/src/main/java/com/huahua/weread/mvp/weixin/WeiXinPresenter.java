package com.huahua.weread.mvp.weixin;

import com.huahua.weread.bean.WeiXinResponse;
import com.huahua.weread.utils.LogUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 *
 * Created by Administrator on 2016/12/2.
 */

public class WeiXinPresenter {

    private IWXView mView;   // 这个在构造函数里被作为参数传进来初始化，就是说持有view的一个引用
    private WXModel mModel;  // 这个直接在构造函数里new出来

    private Subscription mSubscription = null;

    WeiXinPresenter(IWXView view) {
        mView = view;
        mModel = new WXModel();
    }

    // presenter要干什么呀

    /**
     * 最主要就是这个方法
     *
     * @param pageIndex
     */
    public void loadData(int pageIndex) {
        mSubscription = mModel.loadWXjingxuan(pageIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, WeiXinResponse>() {
                    @Override
                    public WeiXinResponse call(Throwable throwable) {
                        LogUtils.LOGI("WeiXinPresenter", ">>>>>  onErrorReturn >>>>");
                        throwable.printStackTrace();
                        mView.showMsgWithLongToast("出错了,请稍后重试");
                        return null;
                    }
                })
                .subscribe(new Subscriber<WeiXinResponse>() {
                    @Override
                    public void onCompleted() {
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showMsgWithLongToast("出错了,请稍后重试");
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onNext(WeiXinResponse weiXinResponse) {
                        if (weiXinResponse == null) {
                            // 为什么weiXinResponse为空
                            //    == >> observable发生了错误 执行了onErrorReturn
                            // 不进行空判断，下面可能会报空指针异常
                            return;
                        }
                        if (weiXinResponse.getCode() == 200) {
                            mView.updateSuccessData(weiXinResponse.getNewslist());
                        } else {
                            mView.showMsgWithLongToast("服务器内部错误!");
                        }
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
        mView = null;  // 这样就没有Fragment（activity）的引用啦
    }

}
















