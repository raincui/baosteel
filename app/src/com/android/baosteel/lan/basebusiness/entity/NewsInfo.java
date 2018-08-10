package com.android.baosteel.lan.basebusiness.entity;

import android.text.TextUtils;

import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: NewsInfo
 * Create DateTime: 2017/3/14
 */
public class NewsInfo extends Info {

    /**
     * {"data":{"data":
     * [
     * {"iszt":"1","picList":[],"pubDate":"2017-03-13","otherList":[],"isVideo":"1",
     * "labelList":[{"labelid":"b7484d30-3d22-45d7-a077-5bca10d48d02","labelname":"产业规划"}],
     * "docId":"d243312c-0e08-44dc-a01e-186f3d8dab62","videoUrl":null,
     * "channelTitle":"爱心子栏目","showType":"0","title":"产业规划文章",
     * "groupTitle":"爱心","groupId":"783f88f1-9bb7-44bd-8429-24b1b9298302",
     * "recommend":"1","category":null,"channelId":"61900ad0-eced-4e9d-9232-e5ccbf14421a"},
     * {"iszt":"1","picList":[{"attachName":"QQ图片20170223103821.png","attachUrl":"http://bgtest1.baogang.info:9081/NMfiledownloadservlet?fileguid=6c5fabb2-9f72-4764-ad3d-0f71ff6aa915"}],"pubDate":"2017-03-10","otherList":[{"attachName":"婚纱.txt","attachUrl":"http://bgtest1.baogang.info:9081/NMfiledownloadservlet?fileguid=d6886f3a-62c4-4e91-a797-c83564eed0b3"}],"isVideo":"1","labelList":[{"labelid":"b7484d30-3d22-45d7-a077-5bca10d48d02","labelname":"产业规划"}],"docId":"de5b2a3b-e199-4363-b930-c593d3b804bf","videoUrl":null,"channelTitle":"爱心子栏目","showType":"","title":"爱心其他文章11","groupTitle":"爱心","groupId":"783f88f1-9bb7-44bd-8429-24b1b9298302","recommend":"0","category":null,"channelId":"61900ad0-eced-4e9d-9232-e5ccbf14421a"}],"result":"success"},
     * "status":"1","msg":"查询成功"}
     */

    private String iszt;//是否是专题 0是1否
    private String pubDate;
    private String isVideo;//是否是视频 0是1否
    private String docId;
    private String videoUrl;
    private String groupTitle;
    private String groupId;
    private String recommend;//是否是推荐 0是1否
    private String category;//是否置顶 0是1否
    private String title;
    private String showType;//小图 3、大图1、多图2、无图0
    private String channelTitle;
    private String channelId;
    private String channelPic;
    private String docLink;
    private List<PicInfo> picList;//图片集合
    private List<LabelInfo> labelList;//标签集合
    private List<PicInfo> otherList;//附件集合

    private String content;//内容
    private int readCount;//阅读数
    private int commentCount;//评论数
    private int loveCount;//点赞数
    private int isCollect;//是否已收藏1是0否
    private int isLove;//是否已点赞1是0否
    private String author;//作者
    private int status;//是否已读 1是0否

    public String getChannelPic() {
        return channelPic;
    }

    public void setChannelPic(String channelPic) {
        this.channelPic = channelPic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<PicInfo> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<PicInfo> otherList) {
        this.otherList = otherList;
    }

    public String getIszt() {
        return iszt;
    }

    public void setIszt(String iszt) {
        this.iszt = iszt;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(String isVideo) {
        this.isVideo = isVideo;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowType() {
        return TextUtils.isEmpty(showType) ? "" : showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public List<PicInfo> getPicList() {
        return picList;
    }

    public void setPicList(List<PicInfo> picList) {
        this.picList = picList;
    }

    public List<LabelInfo> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<LabelInfo> labelList) {
        this.labelList = labelList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isVideo() {
        return "1".equals(isVideo);
    }

    public boolean isSpecial() {
        return "1".equals(iszt);
    }

    public boolean isTop() {
        return "1".equals(category);
    }

    public boolean isSmallPic() {
        return "3".equals(showType);
    }

    public boolean isBigPic() {
        return "1".equals(showType);
    }

    public boolean isMorePic() {
        return "2".equals(showType);
    }

    public boolean isNoPic() {
        return TextUtils.isEmpty(showType) || "0".equals(showType);
    }


    /**
     * 是否外部链接
     *
     * @return
     */
    public boolean isDocLink() {
        return !TextUtils.isEmpty(docLink);
    }

    /**
     * 获取封面
     *
     * @return
     */
    public String getIcon(int index) {
        if(isSpecial())return channelPic;
        if (picList != null && picList.size() > index) {
            return picList.get(index).getAttachUrl();
        }
        return null;
    }

    /**
     * 获取标签
     *
     * @param index
     * @return
     */
    public LabelInfo getlabel(int index) {
        if (labelList != null && labelList.size() > index) {
            return labelList.get(index);
        }
        return null;
    }

    /**
     * 是否已收藏
     *
     * @return
     */
    public boolean isCollected() {
        return 1 == isCollect;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public int getIsLove() {
        return isLove;
    }

    public void setIsLove(int isLove) {
        this.isLove = isLove;
    }

    /**
     * 是否已点赞
     *
     * @return
     */
    public boolean isGooded() {
        return 1 == isLove;
    }

    public void good(boolean flag) {
        isLove = flag ? 1 : 0;
    }

    public void collect(boolean flag) {
        isCollect = flag ? 1 : 0;
    }

    public boolean isReaded() {
        return status == 1;
    }

    public void setReaded() {
        status = 1;
    }

}
