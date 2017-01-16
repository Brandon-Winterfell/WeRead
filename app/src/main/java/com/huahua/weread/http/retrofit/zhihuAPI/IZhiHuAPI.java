package com.huahua.weread.http.retrofit.zhihuAPI;

import com.huahua.weread.bean.ZhiHuArticle;
import com.huahua.weread.bean.ZhiHuResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/6.
 */

public interface IZhiHuAPI {

    @GET("api/4/news/latest")
    Observable<ZhiHuResponse> getLastDaily();

    @GET("api/4/news/before/{date}")
    Observable<ZhiHuResponse> getTheDaily(@Path("date") String date);

    @GET("api/4/news/{id}")
    Observable<ZhiHuArticle> getZhihuArticle(@Path("id")String id);
}























