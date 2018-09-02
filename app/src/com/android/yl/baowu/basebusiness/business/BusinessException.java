package com.android.yl.baowu.basebusiness.business;

/**
 * 业务异常类
 * Created by Administrator on 2016/3/9.
 */
public class BusinessException extends Exception {

    /**
     * 错误码
     * <BusinessErrorCode>
     */
    private int errorCode;
    /**
     * 异常描述
     */
    private String errorStr;

    public BusinessException(int errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(int errorCode, String errorStr) {
        this.errorCode = errorCode;
        this.errorStr = errorStr;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorStr(String errorStr) {
        this.errorStr = errorStr;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorStr() {
        return errorStr;
    }
}
