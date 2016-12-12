package com.huahua.weread.http;

import com.huahua.weread.bean.GankioFuliResponse;
import com.huahua.weread.bean.GuokeHotResponse;
import com.huahua.weread.bean.WeiXinResponse;
import com.huahua.weread.bean.ZhiHuArticle;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;
import com.huahua.weread.http.retrofit.gankioAPI.IGankioAPI;
import com.huahua.weread.http.retrofit.guokeAPI.IGuoKeAPI;
import com.huahua.weread.http.retrofit.weixinAPI.IWeiXinAPI;
import com.huahua.weread.http.retrofit.zhihuAPI.IZhiHuAPI;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Http请求的统一入口
 *
 * Created by Administrator on 2016/12/1.
 */

public class HttpManager {

    private static HttpManager mInstance;

    private IWeiXinAPI mIWeiXinAPI;
    // 其他的api  是不是应该改为tianxingAPI
    private IZhiHuAPI mIZhiHuAPI;
    private IGuoKeAPI mIGuoKeAPI;
    private IGankioAPI mIGannkioAPI;

    public static final String TianXingKey = "427b2058d27376730d8946fbbd63b4c9";


    /**
     * 私有的HttpManager构造方法
     */
    private HttpManager() {
        mIWeiXinAPI = HttpClient.getWXRetrofit().create(IWeiXinAPI.class);
        mIZhiHuAPI = HttpClient.getZHRetrofit().create(IZhiHuAPI.class);
        mIGuoKeAPI = HttpClient.getGKRetrofit().create(IGuoKeAPI.class);
        mIGannkioAPI = HttpClient.getGankioRetrofit().create(IGankioAPI.class);
    }

    /**
     * 返回HttpManager的单例对象
     * @return
     */
    public static HttpManager getInstance() {
        if (mInstance == null) {
            synchronized (HttpManager.class) {
                if (mInstance == null) {
                    mInstance = new HttpManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 各种请求数据的具体方法
     *
     * 不能这样子   要放到具体的模块中 presenter中 model中
     * 不然这个类太大了
     *
     *    你什么时候解绑
     */

    /**
     * 微信精选的请求
     * @param pageIndex
     * @param subscriber
     * @return
     */
    public Subscription loadWX(int pageIndex, Subscriber<WeiXinResponse> subscriber) {
        Subscription s = mIWeiXinAPI.getWeiXinJingXuan(TianXingKey, 10, pageIndex)
                .subscribeOn(Schedulers.io())
//                .map(new Func1<WeiXinResponse, List<WeiXinNews>>() {
//                    @Override
//                    public List<WeiXinNews> call(WeiXinResponse weiXinJSON) {
//                        List<WeiXinNews> newsList = new ArrayList<WeiXinNews>();
//
//                        for(WeiXinNews newListBean : weiXinJSON.getNewslist()) {
//                            WeiXinNews news1 = new WeiXinNews();
//                            news1.setTitle(newListBean.getTitle());
//                            news1.setCtime(newListBean.getCtime());
//                            news1.setDescription(newListBean.getDescription());
//                            news1.setPicUrl(newListBean.getPicUrl());
//                            news1.setUrl(newListBean.getUrl());
//
//                            newsList.add(news1);
//                        }
//
//                        return newsList;
//                    }
//                })
                .observeOn(AndroidSchedulers.mainThread())
                // 观察者去处理，绑定观察者
                .subscribe(subscriber);

        return s;
    }

    /**
     * 知乎日报的请求  最新日期的
     * @param subscriber
     * @return
     */
    public Subscription loadZhiHuDaily(Subscriber<ZhiHuResponse> subscriber) {
        Subscription s = mIZhiHuAPI.getLastDaily()
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
                .subscribeOn(Schedulers.io())  // 是不是这里要new Thread 不然多个请求在这里会阻塞后面的请求
                .observeOn(AndroidSchedulers.mainThread())
                // 绑定观察者，发送(从服务端返回)数据给观察者处理
                .subscribe(subscriber);
        return s;
    }

    /**
     * 获取某个日期前的知乎日报 传入今天的日期获取昨天的知乎日报
     * @param date
     * @param subscriber 需要数据的观察者
     * @return
     */
    public Subscription loadTheDaily(String date, Subscriber<ZhiHuResponse> subscriber) {
        Subscription s = mIZhiHuAPI.getTheDaily(date)
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
                .subscribe(subscriber);
        return s;
    }

    /**
     * 获取知乎日报id的文章
     * @param id 文章的id
     * @param subscriber 需要数据的观察者
     * @return
     */
    public Subscription loadZhihuArticle(String id, Subscriber<ZhiHuArticle> subscriber) {
        Subscription s = mIZhiHuAPI.getZhihuArticle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return s;
    }

    /**
     * 获取果壳热门的文章
     * @param offset
     * @param subscriber
     * @return
     */
    public Subscription loadGuokeHot(int offset, Subscriber<GuokeHotResponse> subscriber) {
        Subscription s = mIGuoKeAPI.getGuokeHot(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return s;
    }

    /**
     * 获取Gankio的妹子
     * @param pageIndex
     * @param subscriber
     * @return
     */
    public Subscription loadGankioFuli(int pageIndex, Subscriber<GankioFuliResponse> subscriber) {
        Subscription s = mIGannkioAPI.getFuli(pageIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return s;
    }
}




















