package com.android.yl.baowu.basebusiness.entity;

/**
 * Created by Administrator on 2017/3/19.
 * 专题类型信息
 */

public class SpecialInfo extends Info{
    private String title;
    private String channelId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
