package com.huahua.weread.bean;

/**
 * Created by Administrator on 2016/12/12.
 */

public class UpdateResponse<T> {
    /**
     * code : 200
     * message :
     * data : {"versionCode":138,"versionName":"1.3.8","size":"9.9M","downloadUrl":"http://caiyao.name/releases/microreader_1.3.8.apk","releaseNote":"1. 修复IT直接无法加载的bug(接口又变了)."}
     */

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}





















