package com.android.baosteel.lan.basebusiness.entity;

/**
 * Created by cuiyulong on 2018/8/14.
 */

public class UserInfo {
    private int id;
    private String loginIp;
    private String token;
    private String userName;
    private String userPhone;
    private String loginName;
    private String userPic;

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

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
