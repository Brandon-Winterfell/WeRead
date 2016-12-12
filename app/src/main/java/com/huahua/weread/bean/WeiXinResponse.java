package com.huahua.weread.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */

public class WeiXinResponse {


    /**
     * code : 200
     * msg : success
     * newslist : [{"ctime":"2016-12-01","title":"能提高睡眠质量，还能让皮肤变好！今晚决定要裸睡了","description":"丁香医生","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/Pvr3FasqXd417IQgBy4dOq3uPStahvZweGPWTKNwOHtibvRsicUWTAhVyRQl24fhdibu0tmgl9uhx6FOCWwmzvEMg/0?wx_fmt=jpeg","url":"http://mp.weixin.qq.com/s/jIPEFX4Y0R401lWQ7p1mSg"},{"ctime":"2016-12-01","title":"豆瓣9.2，这片可不光是让你舔屏的（强烈安利）","description":"电影铺子","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/3N8gA4Fjgv4ibPlEFrPPnu3Gu5MOsSHnbcZYictyoee2lNmsTiaccmoHhGuuTibZ0foErnJeHHJxxPpl0S14kMJu8Q/0?wx_fmt=jpeg","url":"http://mp.weixin.qq.com/s/yC3-HePf_WiWznOVKx9TXw"},{"ctime":"2016-12-01","title":"靠谱，是职场里最高的评价","description":"拉勾网Lagou","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/YvtDG6ZeX7vfb9zQaauvGU1JIX9EoNGAsr56RKZdQrpOCT1RMebSXGJlnEhRwY5TQVG9fD3oYwbGlDzvGRzIwg/0?wx_fmt=jpeg","url":"http://mp.weixin.qq.com/s/YC7vLb-BSWM2spagfX_CeQ"},{"ctime":"2016-12-01","title":"罗尔痛哭：大家没有同情心，我好绝望","description":"咪蒙","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/cnLxyJPKUPmLFiaA5moib9PAm9Cj6BXOIw7k87QOgb9rhib4QwaopjMv1LYJ0PqJQkk9peXn32phx83pawlhPztHQ/0","url":"http://mp.weixin.qq.com/s/VY0dI9jLnqDnH0kx19dR_g"},{"ctime":"2016-12-01","title":"一场诈捐闹剧之后，如何找到真正需要帮助的患儿？","description":"果壳网","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/icZklJrRfHgDBly3AkEGia5q8wTIrtVYLoOIpDaCq5zcaia7jd9qsUo8J9WsnrhTMDImARiamO9AynQicYolexZ5Ejw/0?wx_fmt=jpeg","url":"http://mp.weixin.qq.com/s/Knfz2Oss7jhzNxuwnWU3eg"},{"ctime":"2016-12-01","title":"把钱捐给一个有3套房的土豪，是怎样的酸爽","description":"创伙伴","picUrl":"http://mmbiz.qpic.cn/mmbiz_png/QeO8tPHltJASQbsiaGSwqHO4Ig55VpTciapcgAyZUWBQc2FibcPBwN3hCWcia7QEoKuJGicWsCmKJqkDmEnBQrqEDIQ/0?wx_fmt=png","url":"http://mp.weixin.qq.com/s/tWn_LOSVVVW1pefYfCvcHg"},{"ctime":"2016-12-01","title":"其实从初中开始，我就对公益失望了。","description":"我要WhatYouNeed","picUrl":"http://mmbiz.qpic.cn/mmbiz_png/zynprs47B4RCCUmc94qQMiaAwEic3xjDHcajmsgL4qx4pzcRha6xrJftHOS0Kyc9Z0U8e80PSTic6O1ib1zK3FWp2Q/0?wx_fmt=png","url":"http://mp.weixin.qq.com/s/tWOO8zqKWDe2bv_dJLsL7A"},{"ctime":"2016-12-01","title":"我们都是宁可死也不愿意向人求助的家伙","description":"新世相","picUrl":"http://mmbiz.qpic.cn/mmbiz_jpg/5ROs96OaibInvs8iaAxY3ib0WiaPdJ4sXCru9usicicX3ouRnk99SmZ5p7f7U7ib8YoZ8W2FQV2kEU4hF6nXicDOgNwAzg/0?wx_fmt=jpeg","url":"http://mp.weixin.qq.com/s/gdcO5uFY9hUtYsa9Hjae5g"},{"ctime":"2016-12-01","title":"你是个什么东西？谷歌逆天黑科技现在就可以告诉你。","description":"差评","picUrl":"http://mmbiz.qpic.cn/mmbiz_png/yZPTcMGWibvtzg2hSc3DoVLrF1bKHqQY0fnbIEujN80dmc9FibskMnT9B94YbcPoicjDsHoOb2eibau7brDP9QlwjA/0?wx_fmt=png","url":"http://mp.weixin.qq.com/s/JApzIbmJLJTHwPnehMuuNw"},{"ctime":"2016-12-01","title":"比Google pixel/iPhone 7 plus评分还要高的国产手机。。。","description":"差评","picUrl":"http://mmbiz.qpic.cn/mmbiz_png/yZPTcMGWibvuzmfibbib1QwkZhJziajFHbp2zXbZUsElQFxAD2YtX6GtpJ1C1fX2R6GqOo6Rsiany9iaKWwazhtdhP4Q/0?wx_fmt=png","url":"http://mp.weixin.qq.com/s/f_L0Tegzqfsgz7vvIC20rg"}]
     */

    private int code;
    private String msg;
    private ArrayList<WeiXinNews> newslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<WeiXinNews> getNewslist() {
        return newslist;
    }

    public void setNewslist(ArrayList<WeiXinNews> newslist) {
        this.newslist = newslist;
    }


}
