package com.android.yl.baowu.basebusiness.http;

/**
 * @author ruisong.li 2016/6/13
 * @Title: HttpStatusCode
 * Create DateTime: 2016/6/13
 */
public enum StatusCode {
    OK("success"),//成功
    ERROR_PARAM("param error"),//参数错误
    ERROR_URL("url error"),//地址错误
    ERROR_SERVER("server error"),//服务器错误
    ERROR_CONNECTION("connection error"),//网络错误
    ERROR_BIND("bind error"),//请求失败
    ERROR_SOCKET_TIME_OUT("socket time out error"),//请求超时
    ERROR_PERMISSION_DENIED("permission denied error"),//获取权限失败
    ERROR_OTHER("unknown error");//其他原因，请求失败

    private String msg;

    private StatusCode(String msg) {
        this.msg = msg;
    }

    // 成员变量
    public String getMsg() {
        return msg;
    }
}
