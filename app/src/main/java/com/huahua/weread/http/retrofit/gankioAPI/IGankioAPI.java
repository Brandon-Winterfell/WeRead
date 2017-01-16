package com.huahua.weread.http.retrofit.gankioAPI;

import com.huahua.weread.bean.GankioFuliResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * baseUrl总是以/结束，@URL不要以/开头
 * http://gank.io/api/data/福利/10/1
 * Created by Administrator on 2016/12/8.
 */

public interface IGankioAPI {

    @GET("api/data/福利/10/{pageIndex}")
    Observable<GankioFuliResponse> getFuli(@Path("pageIndex") int pageIndex);
}






















