package com.android.baosteel.lan.basebusiness.entity;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.entity
 * @Title: PicInfo
 * @Description: 图片信息
 * Create DateTime: 2017/3/14
 */
public class PicInfo extends Info{
    //"attachName":"QQ图片20170223103821.png","attachUrl":"http://bgtest1.baogang.info:9081/NMfiledownloadservlet?fileguid=6c5fabb2-9f72-4764-ad3d-0f71ff6aa915
    private String attachName;
    private String attachUrl;

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }
}
