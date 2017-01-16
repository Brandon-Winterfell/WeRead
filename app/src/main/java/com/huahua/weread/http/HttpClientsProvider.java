package com.huahua.weread.http;

import com.huahua.weread.application.MyApplication;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 返回Retrofit的实例
 *
 * Created by Administrator on 2016/12/1.
 */

public class HttpClientsProvider {

    // 天行api baseurl  用来请求微信精选
    private static final String TIANXINGBASEURL = "http://api.tianapi.com/";
    // 知乎日报baseurl
    private static final String ZHIHUBASEURL = "http://news-at.zhihu.com/";
    // 果壳精选baseurl
    private static final String GUOKEBASEURL = "http://apis.guokr.com/";
    // Gankio 的baseurl
    private static final String GANKIOBASEURL = "http://gank.io/";

    /**
     * 配置OKHttp并返回OKHttp静态对象
     * 获取整个应用的单例OkHttpClient
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        return MyApplication.getOkHttp3Client();
    }

    /**
     * 配置微信的retrofit对象并返回
     * @return
     */
    public static Retrofit getWXRetrofit() {
        return SingletonHolder.WXRETROFIT_INSTANCE;
    }

    /**
     * 配置知乎的retrofit对象并返回
     * @return
     */
    public static Retrofit getZHRetrofit() {
        return SingletonHolder.ZHRETROFIT_INSTANCE;
    }

    /**
     * 配置果壳的retrofit对象并返回
     * @return
     */
    public static Retrofit getGKRetrofit() {
        return SingletonHolder.GKRETROFIT_INSTANCE;
    }

    /**
     * 配置Gankio的retrofit对象并返回
     * @return
     */
    public static Retrofit getGankioRetrofit() {
        return SingletonHolder.GANKIORETROFIT_INSTANCE;
    }


    /**
     * 静态内部类实现单例模式
     */
    private static class SingletonHolder{

        private static final Retrofit WXRETROFIT_INSTANCE = new Retrofit.Builder()
                .baseUrl(TIANXINGBASEURL)
                // 添加json转换器
                .addConverterFactory(GsonConverterFactory.create())
                // 添加RxJava适配器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                // 添加自己定义的OKHttpclient，就不是默认的
                .client(getOkHttpClient())
                .build();

        private static final Retrofit ZHRETROFIT_INSTANCE = new Retrofit.Builder()
                .baseUrl(ZHIHUBASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();

        private static final Retrofit GKRETROFIT_INSTANCE = new Retrofit.Builder()
                .baseUrl(GUOKEBASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();

        private static final Retrofit GANKIORETROFIT_INSTANCE = new Retrofit.Builder()
                .baseUrl(GANKIOBASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();
    }
}



















