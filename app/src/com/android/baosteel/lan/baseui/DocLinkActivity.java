package com.android.baosteel.lan.baseui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.util.FileUtils;
import com.android.baosteel.lan.basebusiness.util.LogUtil;
import com.android.baosteel.lan.baseui.ui.BaseWebViewActivity;
import com.baosight.iplat4mlibrary.core.constant.EiServiceConstant;
import com.baosight.iplat4mlibrary.core.ei.eiinfo.EiInfo;
import com.baosight.iplat4mlibrary.core.uitls.json.EiInfo2Json;
import com.baosight.lan.R;

/**
 * @author yulong.cui
 * @Title: DocLinkActivity
 * @Description: 网页
 * Create DateTime: 2017/3/14
 */
public class DocLinkActivity extends BaseWebViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doclink);
        String docLink = getIntent().getStringExtra("docLink");
        boolean isAttachment = getIntent().getBooleanExtra("isAttachment", false);
        if (isAttachment) {
            adapterLink(docLink);
        } else
            setWebView(docLink, R.id.web, Color.GREEN);
    }

    /**
     * 附件地址转换
     *
     * @param url
     */
    private void adapterLink(String url) {

        EiInfo qinfo = new EiInfo();
        qinfo.set(EiServiceConstant.PROJECT_TOKEN, "FileService");
        qinfo.set(EiServiceConstant.PARAMETER_ENCRYPTDATA, "true");
        qinfo.set(EiServiceConstant.PARAMETER_COMPRESSDATA, "true");
        qinfo.set(EiServiceConstant.PARAMETER_APPCODE, getPackageName());
        qinfo.set("fileurl", url);
//        NetApi.call(qinfo, this, "callBackForDocFile");
    }

    public void callBackForDocFile(EiInfo eiInfo) {
        LogUtil.e(EiInfo2Json.toJsonString(eiInfo));
        if (eiInfo.getStatus() == -1) {
            Toast.makeText(this, "当前文档转换异常，请稍后再试。或通过PC访问协同办公获取该附件内容。", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String newUrl = eiInfo.getString("fileurl");
        if (TextUtils.isEmpty(newUrl)) return;
        setWebView(newUrl, R.id.web, Color.GREEN);
    }
}
