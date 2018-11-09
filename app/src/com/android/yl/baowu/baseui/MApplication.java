package com.android.yl.baowu.baseui;

import android.app.Application;

import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.http.OKHttpClientUtil;
import com.android.yl.baowu.basebusiness.http.OkRequestHelper;
import com.android.yl.baowu.basebusiness.util.LogUtil;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareConfig;

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
        SaveDataGlobal.open(this);
        OkRequestHelper.initRequestHelper(this);
        OKHttpClientUtil.init(this,false);
        NetApi.init(this);
        UMConfigure.init(this,"5a12384aa40fa3551f0001d1"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }


}
