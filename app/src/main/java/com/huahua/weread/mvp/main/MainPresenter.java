package com.huahua.weread.mvp.main;

import com.huahua.weread.bean.UpdateInfo;
import com.huahua.weread.bean.UpdateResponse;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/12.
 */

public class MainPresenter {

    private IMainView mView;
    private MainModel mModel;

    private Subscription mSubscription; // 解绑

    public MainPresenter(IMainView iMainView) {
        mView = iMainView;
        mModel = new MainModel();
    }

    public void checkappVersion() {
        mSubscription = mModel.checkAppVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, UpdateResponse<UpdateInfo>>() {
                    @Override
                    public UpdateResponse<UpdateInfo> call(Throwable throwable) {
                        // 相当于try catch 捕获observable的异常
                        return null;
                    }
                })
                .subscribe(new Subscriber<UpdateResponse<UpdateInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UpdateResponse<UpdateInfo> updateInfoUpdateResponse) {
                        if (updateInfoUpdateResponse == null) {
                            // 如果observable发生了错误，onErrorReturn会发送一个null过来的
                            return;
                        }

                        if (updateInfoUpdateResponse.getCode() == 200) {
                            mView.showUpdateDialog(updateInfoUpdateResponse.getData());
                        }
                    }
                });
    }


    public void unbindSubscription() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        mView = null;  // 这样就没有Fragment（activity）的引用啦
    }

}






















