package com.android.yl.baowu.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.SearchInfo;
import com.android.yl.baowu.basebusiness.util.JsonDataParser;
import com.android.yl.baowu.baseui.DocLinkActivity;
import com.android.yl.baowu.baseui.customview.LJRefreshLayout;
import com.android.yl.baowu.baseui.customview.LJRefreshListView;
import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;
import com.android.yl.baowu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    List<SearchInfo> list = JsonDataParser.j2SearchInfo(data);
                    view_refresh.setLoadingMoreEnabled(list.size() >= ProtocolUrl.pageSize);
                    findViewById(R.id.txt_tip).setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
                    mAdapter.replaceAll(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    private void loadData(int page, BusinessCallback callback) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("keyword", mKeyword);
        map.put("condition", subParam);
        map.put("pageNo", page);
        map.put("pageSize", ProtocolUrl.pageSize);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.searchNews, map), callback);

    }

    private void loadMore() {
        loadData(++mCurrentPage, new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    List<SearchInfo> list = JsonDataParser.j2SearchInfo(data);
                    mAdapter.addAll(list);
                    view_refresh.setLoadingMoreEnabled(list.size() >= ProtocolUrl.pageSize);
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
