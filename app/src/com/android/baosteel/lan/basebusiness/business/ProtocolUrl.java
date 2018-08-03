package com.android.baosteel.lan.basebusiness.business;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.business
 * @Title: ProtocolUrl
 * @Description: 协议url
 * Create DateTime: 2017/3/7
 */
public class ProtocolUrl {

    private static String base = "http://139.224.234.251:8080/v1/api/";
//    private static String base = "";
    /**
     * 获取全部栏目
     */
    public static String getColumn = base + "node/group/list";
    /**
     * 改变订阅状态
     */
    public static String changeLook = base + "node/group/subscribe";

    /**
     * 获取资讯列表
     */
    public static String getNews = base + "content/listByNodeGroup";
    /**
     * 获取我的评论列表
     */
    public static String getMyComments = base + "comment/listByUser";
    /**
     * 获取我的收藏列表
     */
    public static String getMyCollects = base + "content/llistByStored";
    /**
     * 获取文章详情
     */
    public static String getNewsDetail = base + "content/detail";
    /**
     * 点赞
     */
    public static String goGood = base + "content/digg";
    /**
     * 改变收藏状态
     */
    public static String changeCollect = base + "content/store";
    /**
     * 获取电子报类型列表
     */
    public static String getNewspaperTypeList = base + "node/listByGroup";
    /**
     * 获取报纸列表
     */
    public static String getNewspaperList = base + "content/listByNode";
    /**
     * 搜索
     */
    public static String searchNews = base + "content/search";
    /**
     * 获取评论列表
     */
    public static String getCommentList = base + "comment/list";
    /**
     * 评论点赞
     */
    public static String goCommentGood = base + "comment/digg";
    /**
     * 评论
     */
    public static String goComment = base + "comment/add";
    /**
     * 获取微矩阵列表
     */
    public static String getWeiJuZhen = base + "appgroup/getWeChat";

    /**
     * 获取公众号文章列表
     */
    public static String getWechatList = base + "appgroup/getWeChatList";

    /**
     * 标识文章已读
     */
    public static String markDocReaded = base + "appgroup/addReadStatus";
}
