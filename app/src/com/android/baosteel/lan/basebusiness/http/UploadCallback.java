package com.android.baosteel.lan.basebusiness.http;


/**
 * @Title: UploadCallback
 * Create DateTime: 2016/6/12
 */
public interface UploadCallback {

    void onProgress(int tag, long currentBytes, long totalBytes);

    void onData(int tag, boolean isSuccess, int httpCode, String data);

    void onFail(int tag, HttpException e);
}
