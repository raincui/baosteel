package com.android.yl.baowu.moduleApi;

import android.content.Context;
import android.content.Intent;

import com.android.yl.baowu.express.ExpressActivity;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.moduleApi
 * @Title: ExpressApi
 * @Description: 快递矩阵模块接口
 * Create DateTime: 2017/2/27
 */
public class ExpressApi {
    private ExpressApi() {
    }

    static class Instance {
        static ExpressApi api = new ExpressApi();
    }

    public static ExpressApi getInstance() {
        return ExpressApi.Instance.api;
    }

    /**
     * 打开快递矩阵主页
     * @param context
     */
    public void openExpressActivity(Context context) {
        if (context == null) return;
        context.startActivity(new Intent(context, ExpressActivity.class));
    }


}
