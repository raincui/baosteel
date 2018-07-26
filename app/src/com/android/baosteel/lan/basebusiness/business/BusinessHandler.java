package com.android.baosteel.lan.basebusiness.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.baosight.iplat4mlibrary.core.ei.eiinfo.EiInfo;
import com.baosight.iplat4mlibrary.core.uitls.json.EiInfo2Json;

import static com.baosight.iplat4mlibrary.core.ei.agent.AbstractEiServiceAgent.TAG;

/**
 * 异步回调用handler
 * Created by Administrator on 2016/3/9.
 */
public abstract class BusinessHandler extends Handler {

    public final static int WHAT_SUCCESS = 1000;
    public final static int WHAT_EXCEPTION = 1001;
    public final static int WHAT_PROGRESS = 1002;

    private Object mContext;

    /**
     * 传当前调用者，activity或fragment
     * @param context 上下文
     */
    public BusinessHandler(Object context) {
        mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(mContext instanceof Activity) {
            if(((Activity) mContext).isFinishing()) return;
        }
        if(mContext instanceof Fragment) {
            if(!((Fragment) mContext).isAdded()) return;
        }
        switch (msg.what) {
            case WHAT_SUCCESS:
                onSuccess(msg.obj);
                break;
            case WHAT_EXCEPTION:
                onException(msg.arg1,msg.obj != null ? msg.obj.toString() : "");
                break;
            case WHAT_PROGRESS:
                onProgress(msg.arg1);
                break;
        }
    }

    /**
     * 业务正常
     */
    public abstract void onSuccess(Object data);

    /**
     * 业务异常
     * @param errorCode 错误码
     */
    public abstract void onException(int errorCode,String msg);

    /**
     * 进度回调
     * @param progress
     */
    public void onProgress(int progress){}

    public Object getmContext() {
        return mContext;
    }

    public void callback(EiInfo info) {
        Log.i(TAG, "info = " + info);
        //服务调用成功
        if (info.getStatus() >= 0) {
            Log.i(TAG, "调用服务成功！");
            Log.i(TAG, EiInfo2Json.toJsonString(info));
            onSuccess(info);
        } else {
            Log.v(TAG, "调用服务失败！");
        }
    }

}
