package com.android.baosteel.lan.basebusiness.http;


import java.io.File;

/**
 * Create DateTime: 2016/6/12
 */
public interface DownloadCallback {
    void onProgress(int tag, long currentBytes, long totalBytes);

    void onSucess(int tag, File file);

    void onFail(int tag, HttpException e);
}
