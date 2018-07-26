package com.android.baosteel.lan.basebusiness.entity;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: LabelInfo
 * @Description: 标签
 * Create DateTime: 2017/3/14
 */
public class LabelInfo extends Info {
    //"labelid":"b7484d30-3d22-45d7-a077-5bca10d48d02","labelname":"产业规划"
    private String labelid;
    private String labelname;

    public String getLabelid() {
        return labelid;
    }

    public void setLabelid(String labelid) {
        this.labelid = labelid;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }
}
