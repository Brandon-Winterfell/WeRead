package com.huahua.weread.http;

import com.huahua.weread.application.MyApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 返回Retrofit的实例
 *
 * Created by Administrator on 2016/12/1.
 */

public class HttpClient {

    private static Retrofit sWXRetrofit;  // 微信的retrofit
    private static Retrofit sZHRetrofit; // 知乎的retrofit
    private static Retrofit sGKRetrofit; // 果壳的retrofit
    private static Retrofit sGankioRetrofit; // Gankio的retrofit

    private static OkHttpClient sOkHttpClient; // 共用一个okhttpclient会不会有问题

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
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        if (null == sOkHttpClient) {
//            sOkHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build();
            sOkHttpClient = MyApplication.getOkHttp3Client();
        }
        return sOkHttpClient;
    }

    /**
     * 配置微信的retrofit对象并返回
     * @return
     */
    public static Retrofit getWXRetrofit() {
        if (null == sWXRetrofit) {
            sWXRetrofit = new Retrofit.Builder()
                    .baseUrl(TIANXINGBASEURL)
                    // 添加json转换器
                    .addConverterFactory(GsonConverterFactory.create())
                    // 添加RxJava适配器
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    // 添加自己定义的OKHttpclient，就不是默认的
                    .client(getOkHttpClient())
                    .build();
        }
        return sWXRetrofit;
    }

    /**
     * 配置知乎的retrofit对象并返回
     * @return
     */
    public static Retrofit getZHRetrofit() {
        if (null == sZHRetrofit) {
            sZHRetrofit = new Retrofit.Builder()
                    .baseUrl(ZHIHUBASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return sZHRetrofit;
    }

    /**
     * 配置果壳的retrofit对象并返回
     * @return
     */
    public static Retrofit getGKRetrofit() {
        if (null == sGKRetrofit) {
            sGKRetrofit = new Retrofit.Builder()
                    .baseUrl(GUOKEBASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return sGKRetrofit;
    }

    /**
     * 配置Gankio的retrofit对象并返回
     * @return
     */
    public static Retrofit getGankioRetrofit() {
        if (null == sGankioRetrofit) {
            sGankioRetrofit = new Retrofit.Builder()
                    .baseUrl(GANKIOBASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return sGankioRetrofit;
    }
}



















