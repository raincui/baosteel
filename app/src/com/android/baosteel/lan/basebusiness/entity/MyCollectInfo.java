package com.android.baosteel.lan.basebusiness.entity;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class MyCollectInfo extends Info {


    private String docTitle;
    private String collectDate;
    private String groupName;
    private String docId;

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(String collectDate) {
        this.collectDate = collectDate;
    }
}
