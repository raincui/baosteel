package com.android.yl.baowu.basebusiness.http;

/**
 * Created by Administrator on 2015/9/7.
 */
public interface OkHttpAsyncCallback {
    void onData(int Tag, String data);
    void onFailed(int tag, Exception e);
}
