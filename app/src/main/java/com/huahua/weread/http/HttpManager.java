package com.huahua.weread.http;

import android.util.Log;

import com.huahua.weread.bean.GankioFuliResponse;
import com.huahua.weread.bean.GuokeHotResponse;
import com.huahua.weread.bean.UpdateInfo;
import com.huahua.weread.bean.UpdateResponse;
import com.huahua.weread.bean.WeiXinResponse;
import com.huahua.weread.bean.ZhiHuArticle;
import com.huahua.weread.bean.ZhiHuDailyItem;
import com.huahua.weread.bean.ZhiHuResponse;
import com.huahua.weread.http.retrofit.gankioAPI.IGankioAPI;
import com.huahua.weread.http.retrofit.guokeAPI.IGuoKeAPI;
import com.huahua.weread.http.retrofit.weixinAPI.IWeiXinAPI;
import com.huahua.weread.http.retrofit.zhihuAPI.IZhiHuAPI;

import java.net.SocketTimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Http请求的统一入口，方便管理
 *
 * Created by Administrator on 2016/12/1.
 */

public class HttpManager {

    private IWeiXinAPI mIWeiXinAPI;  // 来自天行数据
    private IZhiHuAPI mIZhiHuAPI;
    private IGuoKeAPI mIGuoKeAPI;
    private IGankioAPI mIGannkioAPI;

    public static final String TianXingKey = "427b2058d27376730d8946fbbd63b4c9";

    /**
     * 静态内部类实现单例
     */
    private static class SingletonHolder{
        private static final HttpManager INSTANCE = new HttpManager();
    }

    /**
     * 私有的HttpManager构造方法
     */
    private HttpManager() {
        mIWeiXinAPI = HttpClientsProvider.getWXRetrofit().create(IWeiXinAPI.class);
        mIZhiHuAPI = HttpClientsProvider.getZHRetrofit().create(IZhiHuAPI.class);
        mIGuoKeAPI = HttpClientsProvider.getGKRetrofit().create(IGuoKeAPI.class);
        mIGannkioAPI = HttpClientsProvider.getGankioRetrofit().create(IGankioAPI.class);
    }

    /**
     * 返回HttpManager的单例对象
     * @return
     */
    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 各种请求数据的具体方法
     *
     * 不能这样子   要放到具体的模块中 presenter中 model中
     * 不然这个类太大了
     *
     *    你什么时候解绑 (已解决) -->>  返回observable就没有这个问题啦  presenter去处理解绑等事情
     */


    /**
     * 微信精选的请求
     * @param pageIndex
     * @return
     */
    public Observable<WeiXinResponse> loadWX(int pageIndex) {
        return mIWeiXinAPI.getWeiXinJingXuan(TianXingKey, 10, pageIndex);
    }

    /**
     * 知乎日报的请求  最新日期的
     * @return
     */
    public Observable<ZhiHuResponse> loadZhiHuDaily() {
        return mIZhiHuAPI.getLastDaily();
    }

    /**
     * 获取某个日期前的知乎日报 传入今天的日期获取昨天的知乎日报
     * @param date
     * @return
     */
    public Observable<ZhiHuResponse> loadTheDaily(String date) {
        return mIZhiHuAPI.getTheDaily(date);
    }

    /**
     * 获取知乎日报id的文章
     * @param id 文章的id
     * @return
     */
    public Observable<ZhiHuArticle> loadZhihuArticle(String id) {
        return mIZhiHuAPI.getZhihuArticle(id);
    }


    /**
     * 好少的代码，只是去执行网络请求
     *
     * @param offset
     * @return
     */
    public Observable<GuokeHotResponse> loadGuokeHot(int offset) {
        return mIGuoKeAPI.getGuokeHot(offset);
    }

    /**
     * 检测app是否有更新的版本
     * @return
     */
    public Observable<UpdateResponse<UpdateInfo>> checkAppUpdate() {
        return mIGuoKeAPI.checkAppUpdate();
    }

    /**
     * 获取Gankio的妹子
     * @param pageIndex
     * @return
     */
    public Observable<GankioFuliResponse> loadGankioFuli(int pageIndex) {
        return mIGannkioAPI.getFuli(pageIndex);
    }
}




















