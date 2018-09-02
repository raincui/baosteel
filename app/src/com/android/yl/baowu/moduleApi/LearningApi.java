package com.android.yl.baowu.moduleApi;

import android.support.v4.app.Fragment;
import android.view.View;

import com.android.yl.baowu.learning.LearningFragment;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.moduleApi
 * @Title: LearningApi
 * @Description: 学习模块接口
 * Create DateTime: 2017/2/27
 */
public class LearningApi {
    private LearningFragment fragment;
    private OnRefreshListener listener;

    private LearningApi() {
        fragment = new LearningFragment();
    }

    /**
     * 注册刷新的监听
     *
     * @param listener
     */
    public void setRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public void notifyRefresh(Object o) {
        if (listener != null) listener.onRefresh(o);
    }

    static class Instance {
        static LearningApi api = new LearningApi();
    }

    public static LearningApi getInstance() {
        return Instance.api;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Fragment getFragment(View head) {
        fragment.setHead(head);
        return fragment;
    }
}
