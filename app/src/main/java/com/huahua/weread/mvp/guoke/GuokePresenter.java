package com.huahua.weread.mvp.guoke;

import android.util.Log;

import com.huahua.weread.bean.GuokeHotResponse;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Administrator on 2016/12/7.
 */

public class GuokePresenter {

    private IGuokeView mView;
    private GuokeModel mModel;

    private Subscription mSubscription;  // 声明出来是为了解绑  如果执行了onerror或者oncompleted 自动解绑的

    public GuokePresenter(IGuokeView view) {
        mView = view;
        mModel = new GuokeModel();
    }

    public void loadGuokeHot(int offset) {
        // model    干活
        mSubscription = mModel.loadGuokeHot(offset, new Subscriber<GuokeHotResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgressDialog();
                mView.showLoadFailMsg(e.getMessage());
                Log.e("这里是" + getClass().getSimpleName(), e.getMessage());
            }

            @Override
            public void onNext(GuokeHotResponse guokeHotResponse) {
                mView.hideProgressDialog();
                mView.updateSuccessData(guokeHotResponse.getResult());
            }
        });
    }

    public void unBindSubscriber() {
        if (mSubscription != null) {
                mSubscription.unsubscribe();
        }
    }

}































