package com.android.yl.baowu.moduleApi;

import com.android.yl.baowu.news.NewsFragment;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.moduleApi
 * @Title: NewsApi
 * @Description: 资讯模块接口
 * Create DateTime: 2017/2/27
 */
public class NewsApi {

    private NewsFragment fragment;

    private NewsApi() {
        fragment = new NewsFragment();
    }

    static class Instance {
        static NewsApi api = new NewsApi();
    }

    public static NewsApi getInstance() {
        return NewsApi.Instance.api;
    }

    public NewsFragment getFragment() {
        return fragment;
    }

}
