package com.android.baosteel.lan.basebusiness.business;

import android.app.Activity;
import android.content.Context;

import com.android.baosteel.lan.basebusiness.util.LogUtil;
import com.baosight.iplat4mandroid.login.UserSession;
import com.baosight.iplat4mlibrary.core.ei.agent.EiServiceAgent;
import com.baosight.iplat4mlibrary.core.ei.eiinfo.EiInfo;
import com.baosight.iplat4mlibrary.core.uitls.Iplat4mHelper;
import com.baosight.iplat4mlibrary.login.HelperConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author yulong.cui
 * @Title: NetApi
 * @Description: 网络代理
 * Create DateTime: 2017/3/1
 */
public class NetApi {
    private static EiServiceAgent api;
    static HelperConfig helperConfig;
    static Context mC;

    public static void init(Activity context) {
        helperConfig = new HelperConfig();
        helperConfig.setLoginService(Iplat4mHelper.getLoginServiceDef());
        helperConfig.setAgentService(Iplat4mHelper.getAgentServiceDef());
        Iplat4mHelper mIplat4mStarter = Iplat4mHelper.getIplat4mHelper(helperConfig);
        mIplat4mStarter.start(context);
        mC = context;
    }

    public static void call(Object info, BusinessCallback callback) {
        call(info, callback, "callback");
    }

    public static void call(Object info, Object callback, String callbackKey) {
        UserSession userSession = UserSession.getUserSession();
        String userTokenId = userSession.getUserTokenId();
        if (helperConfig == null) {
            helperConfig = new HelperConfig();
            helperConfig.setLoginService(Iplat4mHelper.getLoginServiceDef());
            helperConfig.setAgentService(Iplat4mHelper.getAgentServiceDef());
        }
        Iplat4mHelper mIplat4mStarter = Iplat4mHelper.getIplat4mHelper(helperConfig);
        mIplat4mStarter.start();
        EiServiceAgent api = mIplat4mStarter.getServiceAgent();
        if (api.httpAgent != null) {
            api.httpAgent.setUsertokenid(userTokenId);
            api.httpAgent.setEncryptKey("IPFDfb9rhthPbu3k");
            api.httpAgent.setEncryptVector("4kcIDqT27ype2bd7");
            api.httpAgent.setUserID(userSession.getUserId());
        }
        api.callService(info, callback, callbackKey);
    }

    public static String getJsonParam(String requestUrl) {
        return getJsonParam(requestUrl, null, null, true);
    }

    public static String getJsonParam(String requestUrl, List<String> param) {
        return getJsonParam(requestUrl, param, null, true);
    }

    public static String getJsonParam(String requestUrl, Map<String, String> param) {
        return getJsonParam(requestUrl, null, param, false);
    }

    private static String getJsonParam(String requestUrl, List<String> param, Map<String, String> map, boolean isGet) {
        JSONObject json_reject = new JSONObject();
        JSONObject json_reject_attr = new JSONObject();
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
                for (String key : map.keySet()) {
                    json_reject_data.put(key, map.get(key));
                }
            }
            LogUtil.e(requestUrl);
            json_reject_attr.put("methodName", "");
            json_reject_attr.put("projectName", "baowunews");
            json_reject_attr.put("serviceName", requestUrl);
            json_reject_attr.put("parameter_compressdata", "true");
            json_reject_attr.put("parameter_encryptdata", "false");
            json_reject_attr.put("datatype", "json/json");
            json_reject_attr.put("requestType", isGet ? "get" : "post");
            json_reject_attr.put("argsType", "body");

            json_reject.put("attr", json_reject_attr);
            json_reject.put("data", json_reject_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json_reject.toString();
    }
}