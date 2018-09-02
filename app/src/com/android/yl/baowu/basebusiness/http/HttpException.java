package com.android.yl.baowu.basebusiness.http;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ruisong.li 2016/6/12
 * @Title: HttpException
 * Create DateTime: 2016/6/12
 */
public class HttpException extends Exception {

    /**
     * 网络原因失败状态码
     */
    private int httpCode = -1;
    /**
     * 本地原因失败状态码
     */
    private StatusCode statusCode = StatusCode.ERROR_OTHER;
    /**
     * 错误信息
     */
    private String msg;

    public HttpException() {
    }

    public HttpException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public HttpException(StatusCode code, String msg) {
        super(msg);
        this.statusCode = code;
        this.msg = msg;
        if (code == StatusCode.ERROR_CONNECTION) {
            if (!TextUtils.isEmpty(msg) && msg.contains("ENETUNREACH")) {
                this.msg = "当前网络不可用，请检查网络设置";
            } else {
                this.msg = "请求失败";
            }
        } else if (code == StatusCode.ERROR_BIND) {
            this.msg = "请求失败";
        } else if (code == StatusCode.ERROR_SOCKET_TIME_OUT) {
            this.msg = "请求超时";
        } else if (code != StatusCode.OK && !TextUtils.isEmpty(msg) && msg.contains("Canceled")) {
            this.msg = "请求已取消";
        } else if (code == StatusCode.ERROR_OTHER) {
            if (!TextUtils.isEmpty(msg) && msg.startsWith("Unable to resolve host")) {
                this.msg = "网络不稳定，请稍后重试";
            } else {
                this.msg = "请求失败";
            }
        }
    }

    public HttpException(int httpCode, String msg) {
        super(msg);
        this.httpCode = httpCode;
        this.msg = msg;
        if (httpCode >= 500) {
            this.msg = "请求异常，请稍后重试";
        } else if (httpCode >= 400) {
            this.msg = "请求异常，请稍后重试";
        }
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null) {
            message = this.msg;
        }
        return message;
    }

    @Override
    public String toString() {
        return "httpCode:" + httpCode + ";statusCode:" + statusCode.getMsg() + ";msg:" + msg;
    }

    public String toJsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("httpCode", httpCode);
            obj.put("statusCode", statusCode.getMsg());
            obj.put("msg", msg + "");
        } catch (JSONException e) {
            // empty here
        }
        return obj.toString();
    }
}
