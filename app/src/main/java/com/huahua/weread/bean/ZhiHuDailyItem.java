package com.huahua.weread.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */

public class ZhiHuDailyItem {


    /**
     * images : ["http://pic4.zhimg.com/bfb39d931c0bdd282c29542965653efb.jpg"]
     * type : 0
     * id : 9038901
     * ga_prefix : 120607
     * title : 生殖隔离先不管了，我们来愉快地交配吧
     */

    private int type;
    private String id;
    private String ga_prefix;
    private String title;
    private String[] images;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date; // 自己加一个date字段，ZhiHuResponse里有一个date字段，服务器返回的


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}































