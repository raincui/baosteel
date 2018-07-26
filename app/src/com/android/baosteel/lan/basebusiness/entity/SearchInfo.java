package com.android.baosteel.lan.basebusiness.entity;

import android.text.TextUtils;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: SearchInfo
 * @Description: 搜索到的信息
 * Create DateTime: 2017/3/21
 */
public class SearchInfo extends Info {

    private String docId;
    private String title;
    private String docLink;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public boolean isDocLink(){
        return !TextUtils.isEmpty(docLink);
    }

}
