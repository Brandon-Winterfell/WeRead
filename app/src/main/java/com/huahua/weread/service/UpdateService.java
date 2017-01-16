package com.huahua.weread.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.huahua.weread.R;

import java.io.File;

/**
 * Created by Administrator on 2016/12/12.
 */

public class UpdateService extends Service {
    private BroadcastReceiver receiver;

    private String mApkFileName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 拿到apk的链接
        String apkUrl = intent.getStringExtra("apkUrl");
        // apk的文件名称
        mApkFileName = intent.getStringExtra("apkFileName");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 直接在这里解绑监听器
                unregisterReceiver(receiver);

                // 自动打开安装app页面
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(
                        new File(Environment.getExternalStorageDirectory() +
                                "/download/" + mApkFileName)),
                        "application/vnd.android.package-archive");
                startActivity(intent);

                // 销毁本service
                stopSelf();
            }
        };
        // 注册广播   DownloadManager.ACTION_DOWNLOAD_COMPLETE这个广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // 开始下载
        startDownload(apkUrl);

        return Service.START_STICKY;
    }

    private void startDownload(String apkUrl) {
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        //request.setTitle("WeRead");
        request.setTitle(getResources().getString(R.string.app_name));
        request.setDescription("新版本下载中");
        request.setMimeType("application/vnd.android.package-archive");
        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mApkFileName);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 默认的(下载时显示 下载完成后消失)
        // 这个是下载完还在显示的
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        dm.enqueue(request);
        Toast.makeText(this, "后台下载中，请稍候...", Toast.LENGTH_SHORT).show();
    }
}



















