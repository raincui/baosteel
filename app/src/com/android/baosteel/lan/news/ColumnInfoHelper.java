package com.android.baosteel.lan.news;

import com.android.baosteel.lan.basebusiness.entity.ColumnInfo;
import com.android.baosteel.lan.basebusiness.util.SharedPrefAction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: ColumnInfoHelper
 * @Description: 栏目数据缓存处理
 * Create DateTime: 2017/3/13
 */
public class ColumnInfoHelper {


    private ColumnInfoHelper() {
        localData = new ArrayList<>();
    }

    static class Instance {
        static ColumnInfoHelper api = new ColumnInfoHelper();
    }

    public static ColumnInfoHelper getInstance() {
        return ColumnInfoHelper.Instance.api;
    }


    private List<ColumnInfo> editableData;
    private List<ColumnInfo> localData;

    /**
     * 缓存栏目组数据
     *
     * @param info
     */
    public void cacheData(JSONObject info) {
        localData.clear();
        localData.add(new ColumnInfo("推荐", -1));
        JSONArray ja = info.optJSONArray("data");
        Type type = new TypeToken<List<ColumnInfo>>() {
        }.getType();
        editableData = new Gson().fromJson(ja.toString(), type);
        //去除强制订阅的数据及排序
        int size = editableData.size();
        for (int i = size - 1; i >= 0; i--) {
            ColumnInfo cf = editableData.get(i);
            if (cf.isQZLook()) {
                localData.add(1, cf);
                editableData.remove(i);
                continue;
            }
            cf.setOrderNo(SharedPrefAction.getInt("sort_" + cf.getGroupId(), 0));
        }
        Collections.sort(editableData);
    }

    /**
     * 返回已订阅栏目组和推荐
     *
     * @return
     */
    public List<ColumnInfo> getTitleColumns() {
        List<ColumnInfo> list = new ArrayList<>();
        list.addAll(localData);
        for (ColumnInfo info : editableData) {
            if (info.isLook()) {
                list.add(info);
            }
        }
        return list;
    }

    /**
     * 返回可编辑栏目组数据
     *
     * @return
     */
    public List<ColumnInfo> getEditableColumns() {
        return editableData;
    }

    /**
     * 保存栏目组顺序
     */
    public void saveSortColumns() {
        if (editableData == null || editableData.isEmpty()) return;
        for (int i = 0; i < editableData.size(); i++) {
            SharedPrefAction.putInt("sort_" + editableData.get(i).getGroupId(), i);
        }
    }

    /**
     * 栏目组排序
     *
     * @param from
     * @param to
     */
    public void changeSortColumns(int from, int to) {
        if (editableData == null) return;
        ColumnInfo info = editableData.remove(from);
        editableData.add(to, info);
    }



}
