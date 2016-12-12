package com.huahua.weread.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.huahua.weread.http.CacheInterceptor;
import com.huahua.weread.http.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2016/12/10.
 */

public class MyApplication extends Application {

    // 离线数据缓存文件夹名字
    private static final String OKHTTP3_CACHE = "okhttp3-cache";

    // picasso实例
    private static Picasso mPicasso;

    // okhttp3实例
    private static OkHttpClient mOkHttp3Client;

    @Override
    public void onCreate() {
        super.onCreate();

        // 构造自定义缓存的OKHttpClient实例
        CacheInterceptor cacheInterceptor = new CacheInterceptor(getApplicationContext());
        mOkHttp3Client = setupOkHttpClient(setOkHttpClientCache(), cacheInterceptor);

        // 构造Picasso实例
        mPicasso = setupPicasso(this, mOkHttp3Client);
    }

    /**
     * 自定义OKHttpClient
     *
     * @param cache
     * @param cacheInterceptor
     * @return
     */
    private OkHttpClient setupOkHttpClient(Cache cache, CacheInterceptor cacheInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(cacheInterceptor)
                .build();
    }

    /**
     * 得到30M大小的Cache目录
     * @return
     */
    @NonNull
    private Cache setOkHttpClientCache() {
        return new Cache(
                createExternalCacheDir(this, OKHTTP3_CACHE),
                30*1024*1024);
    }

    /**
     * 在内置SD卡，本应用包下创建私有的cache目录
     *
     * @param context
     * @param cacheName
     * @return
     */
    private File createExternalCacheDir(Context context, String cacheName) {
        File cacheFile = new File(context.getExternalCacheDir(), cacheName);

        if (!cacheFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheFile.mkdirs();
        }

        return cacheFile;
    }

    /**
     * 自定义Picasso
     *
     * @param context
     * @param okHttp3Client
     * @return
     */
    private Picasso setupPicasso(Context context, OkHttpClient okHttp3Client) {
        Picasso picasso = new Picasso.Builder(context)
                // 自定义Downloader
                .downloader(new OkHttp3Downloader(okHttp3Client))
                .build();
        Picasso.setSingletonInstance(picasso);
        // 红色：代表从网络下载的图片
        // 绿色：代表从内存中加载的图片
        // 深蓝色：代表从磁盘缓存加载的图片
        picasso.setIndicatorsEnabled(true);

        return picasso;
    }

    /**
     * 返回整个app的单例Picasso   是单例吧
     * @return
     */
    public static Picasso getPicasso() {
        return mPicasso;
    }

    /**
     * 返回整个app的单例OKHttpClient  是单例吧
     * @return
     */
    public static OkHttpClient getOkHttp3Client() {
        return mOkHttp3Client;
    }
}















