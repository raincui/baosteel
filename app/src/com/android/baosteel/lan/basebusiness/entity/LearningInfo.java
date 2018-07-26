package com.android.baosteel.lan.basebusiness.entity;

/**
 * Created by Administrator on 2017/3/1.
 */

public class LearningInfo extends Info {

    String title;
    String content;
    String icon;
    long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
