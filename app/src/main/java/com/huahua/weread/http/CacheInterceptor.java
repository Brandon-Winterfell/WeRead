package com.huahua.weread.http;

import android.content.Context;

import com.huahua.weread.utils.NetUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 主要是重写了Cache-Control这个头
 *
 * Created by Administrator on 2016/9/22.
 */

public class CacheInterceptor implements Interceptor {

    Context mContext;

    public CacheInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (!NetUtils.isNetworkConnected(mContext)) {
            //没网强制从缓存读取
            // (必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        /**
         * proceed方法是会 throws IOException的
         */
        Response response = chain.proceed(request);
        Response responseLatest;
        if (NetUtils.isNetworkConnected(mContext)) {
            int maxAge = 60; // 有网失效一分钟，意思是有网情况下缓存一分钟（60秒）
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
           int maxStale = 60 * 60 * 6;// 没网失效6小时，意思是没网情况下文件缓存时间为6小时
            // int maxStale = 60 * 2;  // 两分钟
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }


        return responseLatest;
    }
}



















