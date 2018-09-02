package com.android.yl.baowu.news;

import android.content.Context;
import android.view.View;

import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;

import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: MyAdapter
 * @Description: 新闻列表adapter
 * Create DateTime: 2017/3/7
 */
public class NewsAdapter extends SimpleBaseAdapter {
    public NewsAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    public int getItemResource(int position) {
        return 0;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        return null;
    }
}
