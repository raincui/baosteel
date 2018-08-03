package com.android.baosteel.lan.baseui;

import android.app.Application;

import com.android.baosteel.lan.basebusiness.http.OKHttpClientUtil;
import com.android.baosteel.lan.basebusiness.http.OkRequestHelper;
import com.android.baosteel.lan.basebusiness.util.LogUtil;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.baseui
 * @Title: MApplication
 * Create DateTime: 2017/3/1
 */
public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.setDebugMode(true);
        OkRequestHelper.initRequestHelper(this);
        OKHttpClientUtil.init(this,false);
    }
}
