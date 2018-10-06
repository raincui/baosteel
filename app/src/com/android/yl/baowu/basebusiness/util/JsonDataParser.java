package com.android.yl.baowu.basebusiness.util;

import com.android.yl.baowu.basebusiness.entity.ColumnInfo;
import com.android.yl.baowu.basebusiness.entity.CommentInfo;
import com.android.yl.baowu.basebusiness.entity.LabelInfo;
import com.android.yl.baowu.basebusiness.entity.MyCollectInfo;
import com.android.yl.baowu.basebusiness.entity.MyCommentInfo;
import com.android.yl.baowu.basebusiness.entity.NewsInfo;
import com.android.yl.baowu.basebusiness.entity.PicInfo;
import com.android.yl.baowu.basebusiness.entity.SearchInfo;
import com.android.yl.baowu.basebusiness.entity.SpecialInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuiyulong on 2018/8/1.
 */

public class JsonDataParser {

    public static List<ColumnInfo> j2ColumnInfos(JSONObject data) {
        List<ColumnInfo> list = new ArrayList<>();
        JSONArray ja = data.optJSONArray("data");
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                ColumnInfo info = new ColumnInfo();
                info.setId(jo.optString("id"));
                info.setDescription(jo.optString("description"));
                info.setGroupId(jo.optString("id"));
                info.setGroupName(jo.optString("nodeGroupName"));
                info.setOrderNo(jo.optInt("taxis"));
                info.setType(jo.optInt("groupType"));
                info.setIsDy(jo.optString("isSub"));
                info.setQzDy(jo.optString("isForce"));
                list.add(info);
            }
        }
        return list;
    }


    public static NewsInfo j2NewsInfo(JSONObject jo){
        NewsInfo info = new NewsInfo();
        info.setId(jo.optString("id"));
        info.setTitle(jo.optString("title"));

        JSONArray groups = jo.optJSONArray("nodeGroups");
        if(groups!=null&&groups.length()>0){
            JSONObject group = groups.optJSONObject(0);
            info.setGroupId(group.optString("id"));
            info.setGroupTitle(group.optString("nodeGroupName"));
        }

        JSONArray videos = jo.optJSONArray("videoUrl");
        if(videos!=null&&videos.length()>0){
            String video = groups.optString(0);
            info.setVideoUrl(video);
        }

        info.setAuthor(jo.optString("author"));
        info.setCategory(jo.optString("isTop"));
        info.setCommentCount(jo.optInt("commonts"));
        info.setContent(jo.optString("content"));
        info.setDocId(jo.optString("id"));
        info.setDocLink(jo.optString("linkUrl"));
        info.setIsCollect(jo.optInt("isKeep"));
        info.setIsLove(jo.optInt("isDigg"));
        info.setLoveCount(jo.optInt("diggs"));
        info.setPubDate(jo.optString("addDate"));
        info.setReadCount(jo.optInt("hits"));
        info.setRecommend(jo.optString("isRecommend"));

        JSONArray pics = jo.optJSONArray("imageUrl");
        List<PicInfo> picList = new ArrayList<>();
        if(pics!=null&&pics.length()>0){
            for(int l =0;l<pics.length();l++){
                String file = pics.optString(l);
                PicInfo labelInfo = new PicInfo();
                labelInfo.setAttachName(file);
                labelInfo.setAttachUrl(file);
                picList.add(labelInfo);
            }
        }
        info.setPicList(picList);


        JSONArray files = jo.optJSONArray("fileUrl");
        List<PicInfo> fileList = new ArrayList<>();
        if(files!=null&&files.length()>0){
            for(int k =0;k<files.length();k++){
                String file = files.optString(k);
                PicInfo labelInfo = new PicInfo();
                labelInfo.setAttachName(file);
                labelInfo.setAttachUrl(file);
                fileList.add(labelInfo);
            }
        }
        info.setOtherList(fileList);

        int appType = jo.optInt("appType");
        info.setIsVideo(appType==4?"1":"0");
        info.setShowType(appType==3?"2":(appType==2?"1":(appType==1?"3":"0")));

        JSONArray labels = jo.optJSONArray("contentGroups");
        List<LabelInfo> labelInfos = new ArrayList<>();
        if(labels!=null&&labels.length()>0){
            for(int j =0;j<labels.length();j++){
                JSONObject label = labels.optJSONObject(j);
                LabelInfo labelInfo = new LabelInfo();
                labelInfo.setLabelid(label.optString("id"));
                labelInfo.setLabelname(label.optString("groupName"));
                labelInfos.add(labelInfo);
            }
        }
        info.setLabelList(labelInfos);


        JSONObject channel = jo.optJSONObject("node");
        if(channel!=null){
            info.setChannelId(channel.optString("nodeId"));
            info.setChannelTitle(channel.optString("nodeName"));
            int nodeType = channel.optInt("nodeType");
            info.setIszt(nodeType==1?"1":"0");

        }
        return info;
    }


    public static List<NewsInfo> j2NewsInfos(JSONArray ja) {
        List<NewsInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                NewsInfo info = j2NewsInfo(jo);
                list.add(info);
            }
        }
        return list;
    }

    public static List<SpecialInfo> j2SpecialInfo(JSONObject data){
        JSONArray ja = data.optJSONArray("data");
        List<SpecialInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                SpecialInfo info = new SpecialInfo();
                info.setTitle(jo.optString("nodeName"));
                info.setChannelId(jo.optString("nodeId"));
                list.add(info);
            }
        }
        return list;
    }

    public static List<SearchInfo> j2SearchInfo(JSONObject data){
        JSONArray ja = data.optJSONArray("data");
        List<SearchInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                SearchInfo info = new SearchInfo();
                info.setTitle(jo.optString("title"));
                info.setDocId(jo.optString("id"));
                info.setDocLink(jo.optString("linkUrl"));
                list.add(info);
            }
        }
        return list;
    }
    public static List<MyCommentInfo> j2MyCommentInfo(JSONObject data){
        JSONArray ja = data.optJSONArray("data");
        List<MyCommentInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                MyCommentInfo info = new MyCommentInfo();
                info.setId(jo.optString("commentId"));
                info.setDocId(jo.optString("contentId"));
                info.setDocTitle(jo.optString("title"));
                info.setGroupName(jo.optString("groupName"));
                info.setRemarkContent(jo.optString("content"));
                info.setRemarkDate(jo.optString("addDate"));
                info.setRemarkId(jo.optString("commentId"));
                list.add(info);
            }
        }
        return list;
    }
    public static List<MyCollectInfo> j2MyCollectInfo(JSONObject data){
        JSONArray ja = data.optJSONArray("data");
        List<MyCollectInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                MyCollectInfo info = new MyCollectInfo();
                info.setId(jo.optString("contentId"));
                info.setDocId(jo.optString("contentId"));
                info.setDocTitle(jo.optString("title"));
                info.setCollectDate(jo.optString("addDate"));

                JSONArray groups = jo.optJSONArray("nodeGroups");
                if(groups!=null&&groups.length()>0){
                    JSONObject group = groups.optJSONObject(0);
                    info.setGroupName(group.optString("nodeGroupName"));
                }

                list.add(info);
            }
        }
        return list;
    }
    public static List<CommentInfo> j2CommentInfo(JSONObject data){
        JSONArray ja = data.optJSONArray("data");
        List<CommentInfo> list = new ArrayList<>();
        if (ja != null) {
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                CommentInfo info = new CommentInfo();
                info.setId(jo.optString("id"));
                info.setRemarkId(jo.optString("id"));
                info.setRemarkDate(jo.optString("addDate"));
                info.setRemarkContent(jo.optString("content"));
                info.setIsLove(jo.optInt("isDigg"));
                info.setLoveCount(jo.optInt("diggs"));
                info.setUserName(jo.optString("userName"));
                list.add(info);
            }
        }
        return list;
    }

}
