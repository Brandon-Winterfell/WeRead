package com.huahua.weread.http.retrofit.guokeAPI;


import com.huahua.weread.bean.GuokeHotResponse;
import com.huahua.weread.bean.UpdateInfo;
import com.huahua.weread.bean.UpdateResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 果壳热门的API接口
 *
 * baseUrl总是以/结束，@URL不要以/开头
 * Created by Administrator on 2016/12/1.
 */

public interface IGuoKeAPI {

    @GET("minisite/article.json?retrieve_type=by_minisite")
    Observable<GuokeHotResponse> getGuokeHot(@Query("offset")int offset);


    // app更新
    @GET("https://brandon-winterfell.github.io/WeRead/releases/WeReadUpdate.json")
    Observable<UpdateResponse<UpdateInfo>> checkAppUpdate();
}






















