package com.android.baosteel.lan.baseui;

import android.app.Application;

import com.android.baosteel.lan.basebusiness.util.LogUtil;
import com.android.baosteel.lan.basebusiness.util.SharedPrefAction;
import com.baosight.iplat4mlibrary.core.uitls.Iplat4mHelper;
import com.baosight.lan.R;
import com.facebook.drawee.backends.pipeline.Fresco;

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
        Iplat4mHelper.install(this);
        Iplat4mHelper.setIplat4mCode(getString(R.string.iplat4m_code));
        Iplat4mHelper.setIplat4mAPKUrl(getString(R.string.iplat4m_apk_url));
        Iplat4mHelper.setServiceAgentUrl(getString(R.string.LoginService),getString(R.string.AgentService));
        Iplat4mHelper.setCheckServiceDef(getString(R.string.checkStatus_serviceUrl));

    }
}
