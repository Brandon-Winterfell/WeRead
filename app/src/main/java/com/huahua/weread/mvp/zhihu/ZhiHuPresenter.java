package com.huahua.weread.mvp.zhihu;

import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;
import com.huahua.weread.utils.LogUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ZhiHuPresenter {

    private IZHView mView; // 因为要操纵UI==>view
    private ZHModel mModel; // 再加上一个model

    private Subscription mSubscription; // 声明他出来主要是为了能执行解绑操作
    private Subscription mSubscription2;

    private Boolean dailyLastIsOnErrorReturn = false;
    private Boolean theDailyIsOnErrorReturn = false;

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
        mSubscription = mModel.loadZhiHuDailyLast()
//                // 如果在这map之前发生错误了 Observable的错误
//                // 岂不是在这里onErrorReturn 测试一下就好啦
//                // Observable发生错误的话，立马调用离他最近的onErrorReturn （未知主机异常）
//                // onErrorReturn执行完，正常往下走，然后就是map那里的空指针异常了
//                // 就调用下面那个onErrorReturn，onErrorReturn执行完，也是正常往下走
//                .onErrorReturn(new Func1<Throwable, ZhiHuResponse>() {
//                    @Override
//                    public ZhiHuResponse call(Throwable throwable) {
//                        LogUtils.LOGI("ZhiHuPresenter", ">>>>  onErrorReturn0 >>>> ");
//                        throwable.printStackTrace();
//
//                        return null;
//                    }
//                })
                .map(new Func1<ZhiHuResponse, ZhiHuResponse>() {

                    @Override
                    public ZhiHuResponse call(ZhiHuResponse zhiHuResponse) {
                        // 需要对数据做一下处理 表示时间的字段
                        String date = zhiHuResponse.getDate();
                        for (ZhiHuDailyItem dailyItem : zhiHuResponse.getStories()) {
                            dailyItem.setDate(date);
                        }
                        return zhiHuResponse;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, ZhiHuResponse>() {
                    @Override
                    public ZhiHuResponse call(Throwable throwable) {
                        // 有可能是map之前报未知主机异常等，有可能是map那里空指针异常等
                        // 反正就是Observable处发生了异常，链被打断了
                        LogUtils.LOGI("ZhiHuPresenter", ">>>>  onErrorReturn1 >>>> ");
                        throwable.printStackTrace();

                        dailyLastIsOnErrorReturn = true;
                        mView.showLoadFailMsg("发生错误了,请稍后重试");
                        return null;
                    }
                })
                // 绑定观察者，发送(从服务端返回)数据给观察者处理
                .subscribe(new Subscriber<ZhiHuResponse>() {
                    @Override
                    public void onCompleted() {
                        dailyLastIsOnErrorReturn = false;
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dailyLastIsOnErrorReturn = false;
                        mView.hideSwipeRefreshLayoutOrFooter();
                        mView.showLoadFailMsg(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhiHuResponse zhiHuResponse) {
                        if (zhiHuResponse == null) {
                            if (dailyLastIsOnErrorReturn) {
                                // Observable发生了错误 onErrorReturn
                                // 具体的错误其实应该在onErrorReturn方法里分析，以及他需要跟UI的交互提示用户
                            }
                            return;
                        }
                        mView.updateSuccessData(zhiHuResponse);
                    }
                });
    }

    /**
     * 获取某个日期前的知乎日报  传了今天的日期获取昨天的日报
     * @param date
     */
    public void loadTheDaily(String date) {
        mSubscription2 = mModel.loadTheDaily(date)
                .map(new Func1<ZhiHuResponse, ZhiHuResponse>() {
                    @Override
                    public ZhiHuResponse call(ZhiHuResponse zhiHuResponse) {
                        // 需要对数据做一下处理 表示时间的字段
                        String date = zhiHuResponse.getDate();
                        for (ZhiHuDailyItem dailyItem : zhiHuResponse.getStories()) {
                            dailyItem.setDate(date);
                        }
                        return zhiHuResponse;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, ZhiHuResponse>() {
                    @Override
                    public ZhiHuResponse call(Throwable throwable) {
                        // 有可能是map之前报未知主机异常等，有可能是map那里空指针异常等
                        // 反正就是Observable处发生了异常，链被打断了
                        LogUtils.LOGI("ZhiHuPresenter", ">>>>  onErrorReturn >>>> ");
                        throwable.printStackTrace();

                        theDailyIsOnErrorReturn = true;
                        mView.showLoadFailMsg("发生错误了,请稍后重试");
                        return null;
                    }
                })
                .subscribe(new Subscriber<ZhiHuResponse>() {
                    @Override
                    public void onCompleted() {
                        theDailyIsOnErrorReturn = false;
                        mView.hideSwipeRefreshLayoutOrFooter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        theDailyIsOnErrorReturn = false;
                        mView.hideSwipeRefreshLayoutOrFooter();
                        mView.showLoadFailMsg("发生错误了,请稍后重试");
                    }

                    @Override
                    public void onNext(ZhiHuResponse zhiHuResponse) {
                        if (zhiHuResponse == null) {
                            if (theDailyIsOnErrorReturn) {
                                // Observable发生了错误 onErrorReturn
                                // 具体的错误其实应该在onErrorReturn方法里分析，以及他需要跟UI的交互提示用户
                            }
                            return;
                        }
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
        mView = null;  // 当Fragment销毁view、Activity销毁时，这样就不会Fragment（activity）的引用啦
    }

}





























