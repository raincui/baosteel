package com.android.baosteel.lan.basebusiness.entity;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: WeChatInfo
 * @Description: 微矩阵实体
 * Create DateTime: 2017/3/23
 */
public class WeChatInfo extends Info {
    private String title;
    private String description;
    private String channelPic;
    private String channelId;

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    private int notReadCount;

    private String href;
    private String createTime;
    private String recordId;
    private int isRead;//1未读，0已读


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChannelPic() {
        return channelPic;
    }

    public void setChannelPic(String channelPic) {
        this.channelPic = channelPic;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public boolean isReaded() {
        return isRead == 0;
    }

    public void setReaded() {
        isRead = 0;
    }
}
