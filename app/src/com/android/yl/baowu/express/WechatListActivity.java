package com.android.yl.baowu.express;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.WeChatInfo;
import com.android.yl.baowu.baseui.DocLinkActivity;
import com.android.yl.baowu.baseui.customview.LJRefreshLayout;
import com.android.yl.baowu.baseui.customview.LJRefreshListView;
import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;
import com.android.yl.baowu.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Description: 公众号文章列表
 * Create DateTime: 2017/3/21
 */
public class WechatListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;

    private String mChannelId;
    private SearchAdapter mAdapter;
    private int mCurrentPage;

    @Override
    protected void initTitle() {
        super.initTitle();
        mChannelId = getIntent().getStringExtra("id");
        String titleStr = getIntent().getStringExtra("title");
        TextView title = findView(R.id.tv_title);
        title.setText(titleStr);
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
                        Type type = new TypeToken<List<WeChatInfo>>() {
                        }.getType();
                        List<WeChatInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        view_refresh.setLoadingMoreEnabled(list.size() >= 10);
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
        StringBuilder sb = new StringBuilder(ProtocolUrl.getWechatList);
        sb.append("/").append(page).append("/").append(mChannelId);
        NetApi.call(NetApi.getJsonParam(sb.toString()), callback);
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
                        Type type = new TypeToken<List<WeChatInfo>>() {
                        }.getType();
                        List<WeChatInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
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
        WeChatInfo info = mAdapter.getItem(position);
        //标记已读
        info.setReaded();
        mAdapter.notifyDataSetChanged();
        markReaded(info.getRecordId());
        setResult(Activity.RESULT_OK);

        Intent intent = new Intent(WechatListActivity.this, DocLinkActivity.class);
        intent.putExtra("title", info.getTitle());
        intent.putExtra("docLink", info.getHref());
        startActivity(intent);
        return;
    }

    /**
     * 标记已读
     *
     * @param docId
     */
    private void markReaded(String docId) {
        StringBuilder url = new StringBuilder(ProtocolUrl.markDocReaded);
        url.append("/").append(docId);
        NetApi.call(NetApi.getJsonParam(url.toString()), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean success, String json) {

            }
        });
    }




    class SearchAdapter extends SimpleBaseAdapter<WeChatInfo> {

        public SearchAdapter(Context context, List<WeChatInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_search_list;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            WeChatInfo info = getItem(position);
            TextView title = holder.getView(R.id.txt_title);
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, info.isReaded() ? 0 : R.drawable.icon_point, 0);
            title.setText(info.getTitle());
            TextView time = holder.getView(R.id.txt_time);
            time.setText(info.getCreateTime());
            return convertView;
        }
    }

}
