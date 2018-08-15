package com.android.baosteel.lan.basebusiness.entity;

/**
 * Created by cuiyulong on 2018/8/14.
 */

public class UserInfo {
    private int id;
    private String loginIp;
    private String token;
    private String userName;
    private String loginPhone;

    public int getId() {
        return id;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }
}
