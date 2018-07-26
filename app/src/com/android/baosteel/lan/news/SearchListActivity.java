package com.android.baosteel.lan.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.SearchInfo;
import com.android.baosteel.lan.baseui.DocLinkActivity;
import com.android.baosteel.lan.baseui.customview.LJRefreshLayout;
import com.android.baosteel.lan.baseui.customview.LJRefreshListView;
import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.baseui.ui.SimpleBaseAdapter;
import com.baosight.lan.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: SearchListActivity
 * @Description: 搜索资讯列表
 * Create DateTime: 2017/3/21
 */
public class SearchListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;

    private String mKeyword;
    private SearchAdapter mAdapter;
    private int mCurrentPage;

    @Override
    protected void initTitle() {
        super.initTitle();
        mKeyword = getIntent().getStringExtra("keyword");
        TextView title = findView(R.id.tv_title);
        title.setText(mKeyword);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        view_refresh = findView(R.id.view_refresh);
        list_refresh = findView(R.id.list_refresh);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mAdapter == null) {
            mAdapter = new SearchAdapter(this, null);
            list_refresh.setAdapter(mAdapter);
        }

        view_refresh.post(new Runnable() {
            @Override
            public void run() {
                view_refresh.setRefreshing(true);
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        view_refresh.setOnRefreshListener(new LJRefreshLayout.OnViewRefreshListener() {
            @Override
            public void onRefresh(boolean isLoadingMore) {
                if (isLoadingMore) {
                    loadMore();
                    return;
                }
                loadData();
            }
        });
        list_refresh.setOnItemClickListener(this);
    }

    private void loadData() {
        mCurrentPage = 0;
        loadData(++mCurrentPage, new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                view_refresh.setRefreshing(false);
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<SearchInfo>>() {
                        }.getType();
                        List<SearchInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        view_refresh.setLoadingMoreEnabled(list.size() >= 10);
                        findViewById(R.id.txt_tip).setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
                        mAdapter.replaceAll(list);
                        return;
                    }
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    private void loadData(int page, BusinessCallback callback) {
        try {
            String keyword = URLEncoder.encode(mKeyword, "utf-8");
            StringBuilder sb = new StringBuilder(ProtocolUrl.searchNews);
            sb.append("/").append(keyword).append("/").append(page);
            NetApi.call(NetApi.getJsonParam(sb.toString()), callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadMore() {
        loadData(++mCurrentPage, new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<SearchInfo>>() {
                        }.getType();
                        List<SearchInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        mAdapter.addAll(list);
                        view_refresh.setLoadingMoreEnabled(list.size() >= 10);
                        return;
                    }
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_search_list);
        initView();
        initListener();
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchInfo info = mAdapter.getItem(position);
        if (info.isDocLink()) {
            Intent intent = new Intent(SearchListActivity.this, DocLinkActivity.class);
            intent.putExtra("title", info.getTitle());
            intent.putExtra("docLink", info.getDocLink());
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(SearchListActivity.this, NewsDetailActivity.class);
        intent.putExtra("docId", info.getDocId());
        startActivity(intent);
    }

    class SearchAdapter extends SimpleBaseAdapter<SearchInfo> {

        public SearchAdapter(Context context, List<SearchInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_search_list;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            TextView title = holder.getView(R.id.txt_title);
            title.setText(getItem(position).getTitle());
            return convertView;
        }
    }

}
