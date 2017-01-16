package com.huahua.weread.mvp.guoke;

import android.util.Log;

import com.huahua.weread.bean.GuokeHotResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.huahua.weread.utils.LogUtils.LOGI;
import static com.huahua.weread.utils.LogUtils.makeLogTag;

/**
 * Created by Administrator on 2016/12/7.
 */

public class GuokePresenter {

    private IGuokeView mView;
    private GuokeModel mModel;

    private Subscription mSubscription;  // 声明出来是为了解绑  如果执行了onerror或者oncompleted 自动解绑的

    // 如果要混淆，就不能用这个方法，用参数为String类型那个方法
    private final static String TAG = makeLogTag(GuokePresenter.class);
    // 是否执行了onErrorReturn 用于错误处理 适当的时候置为false
    private boolean hasOnErrorReturn = false;

    GuokePresenter(IGuokeView view) {
        mView = view;
        mModel = new GuokeModel();
    }

    public void loadGuokeHot(int offset) {
        mSubscription = mModel.loadGuokeHot(offset)
                .subscribeOn(Schedulers.io()) // 在io线程进行网络访问
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程对数据流处理
                .onErrorReturn(new Func1<Throwable, GuokeHotResponse>() {
                    @Override
                    public GuokeHotResponse call(Throwable throwable) {
                        LOGI(TAG, "这里是GuokePresenter，onErrorReturn --->> call");
                        hasOnErrorReturn = true;  // 进来了onErrorReturn，表明Observable发生了错误
                        throwable.printStackTrace();

                        // 这里是分析发生onErrorReturn具体原因的
                        if (throwable instanceof SocketTimeoutException) {
                            mView.showMsgWithLongToast("出错了,连接服务器超时,请稍后再试");
                            LOGI(TAG, "这里是GuokePresenter，onErrorReturn --->> call，连接服务器超时");
                            // 其实这里可以用Snackbar，加上右边一个重试按钮的
                        } else if (throwable instanceof UnknownHostException) {
                            // 不ping了
                            mView.showMsgWithLongToast("出错了(未知主机异常),请稍后再试");
                        } else {
                            mView.showMsgWithLongToast("出错了,请稍后再试");
                        }
                        return null;
                    }
                })
                .subscribe(new Subscriber<GuokeHotResponse>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        // This method is invoked when the Subscriber and Observable have been connected but the Observable has
                        // not yet begun to emit items or send notifications to the Subscriber.
                        // 这个方法可以做数据的初始化工作嘛
                        hasOnErrorReturn = false;  // 这样就不用 在onCompleted onError设置为false了吧
                    }

                    @Override
                    public void onCompleted() {
                        hasOnErrorReturn = false;
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 打印错误信息 getClass().getSimpleName()这个不管用呀
                        Log.e("这里是GuokeP层 onError", "出错了," + e.getMessage());

                        // 这里表示的是Subscriber发生了错误，上面的onErrorReturn表明Observable发生了错误
                        hasOnErrorReturn = false;
                        // 隐藏加载更多底部布局或者下拉刷新控件
                        mView.hideSwipeRefreshLayoutOrFooter();
                        // 不应该将原始的错误信息展示出来给用户
                        mView.showMsgWithLongToast("出错了,请稍后再试");
                    }

                    @Override
                    public void onNext(GuokeHotResponse guokeHotResponse) {
                        if (guokeHotResponse != null) {
                            LOGI(TAG, "这里是GuokePresenter，onNext方法，guokeHotResponse != null");
                            mView.updateSuccessData(guokeHotResponse.getResult());
                        } else {
                            if (hasOnErrorReturn) {
                                // 加个判断就表明 这是发生了onErrorReturn，进来的，而不是服务器已经没有数据了
                            } else {
                                // 服务器已经没有数据了，其实这里可以显示个隐藏的UI，告诉用户 the end
                                // 然后可以禁止上拉加载更多请求了，增多一个变量，因为已经没有数据了。
                                mView.showMsgWithLongToast("已经没有更多了...");
                            }
                        }
                    }
                });
    }

    public void unBindSubscriber() {
        if (mSubscription != null) {
                mSubscription.unsubscribe();
        }
        mView = null;  // 这样就没有Fragment（activity）的引用啦
    }

}































