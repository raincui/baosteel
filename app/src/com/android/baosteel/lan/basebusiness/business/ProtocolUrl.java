package com.android.baosteel.lan.basebusiness.business;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.business
 * @Title: ProtocolUrl
 * @Description: 协议url
 * Create DateTime: 2017/3/7
 */
public class ProtocolUrl {

//    private static String base = "http://10.70.61.8:9082/rest/";
    private static String base = "";
    /**
     * 获取全部栏目
     */
    public static String getColumn = base + "appgroup/getChannelGroupList";
    /**
     * 改变订阅状态
     */
    public static String changeLook = base + "appgroup/manageGroup";

    /**
     * 获取资讯列表
     */
    public static String getNews = base + "appnews/getList";
    /**
     * 获取我的评论列表
     */
    public static String getMyComments = base + "remark/getMyRemarkList";
    /**
     * 获取我的收藏列表
     */
    public static String getMyCollects = base + "remark/getMyCollectList";
    /**
     * 获取文章详情
     */
    public static String getNewsDetail = base + "appnews/getNewsInfo";
    /**
     * 点赞
     */
    public static String goGood = base + "remark/toDocLove";
    /**
     * 改变收藏状态
     */
    public static String changeCollect = base + "remark/toCollect";
    /**
     * 获取电子报类型列表
     */
    public static String getNewspaperTypeList = base + "appgroup/getChannelList";
    /**
     * 获取报纸列表
     */
    public static String getNewspaperList = base + "appnews/getList/3/1";
    /**
     * 搜索
     */
    public static String searchNews = base + "appnews/getSpecialList";
    /**
     * 获取评论列表
     */
    public static String getCommentList = base + "remark/getRemarkList";
    /**
     * 评论点赞
     */
    public static String goCommentGood = base + "remark/toRemarkLove";
    /**
     * 评论
     */
    public static String goComment = base + "remark/addRemark";
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
