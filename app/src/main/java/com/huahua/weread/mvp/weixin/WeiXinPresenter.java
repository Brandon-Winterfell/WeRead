package com.huahua.weread.mvp.weixin;

import com.huahua.weread.bean.WeiXinResponse;

import rx.Subscriber;
import rx.Subscription;

/**
 *
 *
 * Created by Administrator on 2016/12/2.
 */

public class WeiXinPresenter {

    IWXView mView;   // 这个在构造函数里被作为参数传进来初始化，就是说持有view的一个引用
    WXModel mModel;  // 这个直接在构造函数里new出来

    public WeiXinPresenter(IWXView view) {
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
        // 用这句的话，不能加载第二次请求 估计
        // 由于Subscriber一旦调用了unsubscribe方法之后，就没有用了。
        // 且当事件传递到onError或者onCompleted之后，也会自动的解绑。

        // 这样出现的一个问题就是每次发送请求都要创建新的Subscriber对象。
        // mSubscription = mModel.loadWXjingxuan(pageIndex, mSubscriber);
        /**
         * 职责分明
         * model去执行加载数据，回调不用你model处理
         * 将回调返回来presenter处理
         */

        mSubscription = mModel.loadWXjingxuan(pageIndex, new Subscriber<WeiXinResponse>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgressDialog();
                        mView.showLoadFailMsg(e.getMessage());
                    }

                    @Override
                    public void onNext(WeiXinResponse weiXinResponse) {
                        mView.hideProgressDialog();
                        if (weiXinResponse.getCode() == 200) {
                            mView.updateSuccessData(weiXinResponse.getNewslist());
                        } else {
                            mView.showLoadFailMsg("服务器内部错误!");
                        }
                    }
        });
    }

    Subscription mSubscription = null;

    Subscriber<WeiXinResponse> mSubscriber = new Subscriber<WeiXinResponse>() {

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            mView.hideProgressDialog();
            mView.showLoadFailMsg(e.getMessage());
        }

        @Override
        public void onNext(WeiXinResponse weiXinResponse) {
            mView.hideProgressDialog();
            if (weiXinResponse.getCode() == 200) {
                mView.updateSuccessData(weiXinResponse.getNewslist());
            } else {
                mView.showLoadFailMsg("服务器内部错误!");
            }
        }
    };

    /**
     * 要在destroyview里执行这个方法
     */
    public void unBindSubscriber() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

}
















