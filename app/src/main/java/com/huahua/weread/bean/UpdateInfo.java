package com.huahua.weread.bean;

/**
 * Created by Administrator on 2016/12/12.
 */

public class UpdateInfo {
    /**
     * versionCode : 5
     * versionName : 1.3.8
     * fileName : microreader_1.3.8.apk
     * size : 9.9M
     * downloadUrl : http://caiyao.name/releases/microreader_1.3.8.apk
     * releaseNote : 1.【新增】稀土掘金\\r\\n2.【修复】部分已知BUG\\r\\n\n.
     */

    private int versionCode;
    private String versionName;
    private String size;
    private String downloadUrl;
    private String releaseNote;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }
}
























