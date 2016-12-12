package com.huahua.weread.bean;

/**
 * Created by Administrator on 2016/12/7.
 */

public class GuokeHotItem {

    private String id;
    private String title;
    private String small_image;
    private String summary;
    private String date_published;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmall_image() {
        return small_image;
    }

    public void setSmall_image(String small_image) {
        this.small_image = small_image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDate_published() {
        return date_published.replace("+08:00","").replace("T"," ");
    }

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }
}




























