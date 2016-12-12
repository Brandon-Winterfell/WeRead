package com.huahua.weread.http.retrofit.weixinAPI;

import com.huahua.weread.bean.WeiXinResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


/**
 * 微信精选API接口
 *
 * 分别从不同的网站下载数据的局限是 每个网站返回的json字段不一样 不能抽象
 *
 * Created by Administrator on 2016/12/1.
 */

public interface IWeiXinAPI {

    // 完整URL :  http://api.tianapi.com/wxnew/?key=APIKEY&num=10
    // Base URL  :  http://api.tianapi.com/
    // 需要接上的  :  wxnew/?key=APIKEY&num=10


    // 天行的API Key
    public static final String TianXingAPIKey = "427b2058d27376730d8946fbbd63b4c9";

    /**
     * 请求 微信精选  的数据
     * @param key
     * @param num
     * @param page
     * @return
     */
    @GET("wxnew/")
    Observable<WeiXinResponse> getWeiXinJingXuan(
            @Query("key") String key, @Query("num") int num, @Query("page") int page
    );


}






















