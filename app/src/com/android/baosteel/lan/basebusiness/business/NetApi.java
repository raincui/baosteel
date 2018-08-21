package com.android.baosteel.lan.basebusiness.business;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.baosteel.lan.basebusiness.entity.UserInfo;
import com.android.baosteel.lan.basebusiness.http.OKHttpClientUtil;
import com.android.baosteel.lan.basebusiness.http.OkHttpAsyncCallback;
import com.android.baosteel.lan.basebusiness.util.LogUtil;
import com.android.baosteel.lan.basebusiness.util.SaveDataGlobal;
import com.baosight.iplat4mandroid.login.UserSession;
import com.baosight.iplat4mlibrary.core.ei.agent.EiServiceAgent;
import com.baosight.iplat4mlibrary.core.ei.eiinfo.EiInfo;
import com.baosight.iplat4mlibrary.core.uitls.Iplat4mHelper;
import com.baosight.iplat4mlibrary.core.uitls.json.JSONObjectUtil;
import com.baosight.iplat4mlibrary.login.HelperConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yulong.cui
 * @Title: NetApi
 * @Description: 网络代理
 * Create DateTime: 2017/3/1
 */
public class NetApi {
    static Context mC;

    public static void init(Context context) {
        mC = context;

    }

    public static void call(JSONObject jsonObject, final BusinessCallback callback) {

        try {
            boolean isGet = jsonObject.optBoolean("isGet");
            String url = jsonObject.optString("url");
            JSONObject paramJ = jsonObject.getJSONObject("data");
            Map<String,Object> param = new Gson().fromJson(paramJ.toString(),Map.class);
            if(param!=null){
                param.put("userId", SaveDataGlobal.getUserId());
                //处理页码从0开始
                Object pageNo = param.get("pageNo");
                if(pageNo!=null){
                    param.put("pageNo",Double.valueOf(pageNo.toString())-1);
                }
            }

            final Handler handler = new Handler(mC.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    callback.callback(msg.obj.toString());
                    return true;
                }
            });
            OkHttpAsyncCallback callback1 = new OkHttpAsyncCallback() {
                @Override
                public void onData(int Tag, String data) {
                    LogUtil.e(data);
                   handler.obtainMessage(0,data).sendToTarget();

                }

                @Override
                public void onFailed(int tag, Exception e) {
                    LogUtil.e(e.getMessage());
                    handler.obtainMessage(0,"").sendToTarget();
                }
            };
            LogUtil.e(param.toString());
            UserInfo userInfo = SaveDataGlobal.getUserInfo();

            Map<String,String> heads = new HashMap<>();
            if(userInfo!=null){
                heads.put("x-user","2/"+userInfo.getToken());
            }else{
                heads.put("x-user","0/default");
            }

            if(isGet){
                param.put("domainId",3);
                OKHttpClientUtil.getEnQueue(url,0,callback1,param,heads);
            }else {
                url+="?domainId=3";
                OKHttpClientUtil.postEnQueue(url,0,callback1,param,heads);
            }
            LogUtil.e(url);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static JSONObject getJsonParam(String requestUrl) {
        return getJsonParam(requestUrl, null, null, true);
    }
    public static JSONObject getJsonParam(String requestUrl,boolean isGet) {
        return getJsonParam(requestUrl, null, null, isGet);
    }
    public static JSONObject getJsonParam(String requestUrl, List<String> param) {
        return getJsonParam(requestUrl, param, null, true);
    }

    public static JSONObject getJsonParam(String requestUrl, Map<String, Object> param) {
        return getJsonParam(requestUrl, null, param, false);
    }

    public static JSONObject getJsonParam(String requestUrl, List<String> param, Map<String, Object> map, boolean isGet) {
        JSONObject json_reject = new JSONObject();
        JSONObject json_reject_data = new JSONObject();

        try {
            if (param != null) {
                if (isGet) {
                    StringBuilder sb = new StringBuilder();
                    for (String value : param) {
                        sb.append("/");
                        sb.append(value);
                    }
                    if (sb.length() > 0) {
                        requestUrl += sb.toString();
                    }
                }
            }
            if (map != null) {
                json_reject_data = new JSONObject(map);
            }
            LogUtil.e(requestUrl);


            json_reject.put("url", requestUrl);
            json_reject.put("isGet", isGet);
            json_reject.put("data", json_reject_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json_reject;
    }
}