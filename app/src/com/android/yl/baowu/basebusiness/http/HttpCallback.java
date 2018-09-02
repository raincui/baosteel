package com.android.yl.baowu.basebusiness.http;



/**
 * Created by Administrator on 2015/9/7.
 */
public interface HttpCallback {

    void onData(int tag, boolean isSuccess, int httpCode, String data);

    void onFail(int tag, StatusCode code, HttpException e);
}
