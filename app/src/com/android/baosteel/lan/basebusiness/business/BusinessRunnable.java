package com.android.baosteel.lan.basebusiness.business;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理的子线程runnable
 * Created by Administrator on 2016/3/9.
 */
public abstract class BusinessRunnable implements Runnable {

    private BusinessHandler mHandler;
    private static ExecutorService mExecutor = Executors.newCachedThreadPool();

    public BusinessRunnable(BusinessHandler handler) {
        mHandler = handler;
        mExecutor.submit(this);
    }

    public BusinessRunnable() {
        mExecutor.submit(this);
    }
    @Override
    public void run() {
        try {
            doInBackgroud();
        } catch (BusinessException e) {
            if (mHandler != null) {
                mHandler.obtainMessage(BusinessHandler.WHAT_EXCEPTION, e.getErrorCode(), 0, e.getErrorStr()).sendToTarget();
            }
        }
    }

    /**
     * 后台运行
     *
     * @throws BusinessException
     */
    public abstract void doInBackgroud() throws BusinessException;

}
