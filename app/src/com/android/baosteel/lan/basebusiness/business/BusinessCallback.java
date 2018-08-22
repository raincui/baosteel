package com.android.baosteel.lan.basebusiness.business;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.baosteel.lan.basebusiness.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.business
 * @Title: BusinessCallback
 * @Description: 网络框架请求回调
 * Create DateTime: 2017/3/2
 */
public abstract class BusinessCallback {

    Context context;

    public BusinessCallback(Context context) {
        this.context = context;
    }

    public abstract void subCallback(boolean success,String json);

    public void callback(String json) {
        try {
            JSONObject jo = new JSONObject(json);
            String status = jo.optString("code");
            if ("1000".equals(status)) {
                subCallback(true,json);
            } else {
                String msg = jo.optString("desc");
                String msg1 = jo.optString("msg");
                if("{}".equals(msg))msg = "网络请求超时";
                if(TextUtils.isEmpty(msg)&&TextUtils.isEmpty(msg1))msg1 = "网络请求超时";
                Toast.makeText(context, TextUtils.isEmpty(msg1)?msg:msg1, Toast.LENGTH_SHORT).show();
                subCallback(false,json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e(json);
            Toast.makeText(context, "网络请求超时", Toast.LENGTH_SHORT).show();
            subCallback(false,json);
        }
    }

}
