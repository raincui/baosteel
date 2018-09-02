package com.android.yl.baowu.basebusiness.entity;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: ColumnInfo
 * @Description: 栏目信息
 * Create DateTime: 2017/3/7
 */
public class ColumnInfo extends Info implements Comparable<ColumnInfo> {

    /**
     * {"result":"success",
     * "data":[{"groupId":"98e76096-7cc2-4c66-b853-10a1f91110c3",
     * "groupName":"要闻",
     * "orderNo":4,
     * "description":"要闻, 集团要闻：内容包括，重大活动，改革创新，经营活动。",
     * "isDy":"0"}]}
     */

    private String groupId;
    private String groupName;
    private int orderNo;
    private String description;
    private String isDy;
    private String qzDy;
    private int type;

    public ColumnInfo(){}
    public ColumnInfo(String groupName,int type) {
        this.groupName = groupName;
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsDy() {
        return isDy;
    }

    public void setIsDy(String isDy) {
        this.isDy = isDy;
    }

    public String getQzDy() {
        return qzDy;
    }

    public void setQzDy(String qzDy) {
        this.qzDy = qzDy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 是否订阅
     *
     * @return
     */
    public boolean isLook() {
        return "1".equals(isDy);
    }

    /**
     * 切换状态
     */
    public void changeLook() {
        isDy = isLook() ? "0" : "1";
    }

    /**
     * 是否强制订阅
     * @return
     */
    public boolean isQZLook() {
        return "1".equals(qzDy);
    }

    @Override
    public int compareTo(ColumnInfo another) {
        return orderNo - another.orderNo;
    }
}
