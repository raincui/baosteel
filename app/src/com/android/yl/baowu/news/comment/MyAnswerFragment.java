package com.android.yl.baowu.news.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.MyCommentInfo;
import com.android.yl.baowu.basebusiness.util.JsonDataParser;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.customview.LJRefreshLayout;
import com.android.yl.baowu.baseui.customview.LJRefreshListView;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;
import com.android.yl.baowu.moduleApi.LearningApi;
import com.android.yl.baowu.news.NewsDetailActivity;
import com.android.yl.baowu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class MyAnswerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private View viewMain;
    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;

    private MyAdapter mAdapter;
    private boolean isFirstInit = true;//是否第一次加载
    private int mCurrentPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_weijuzhen, null);
        else {
            ViewGroup parent = (ViewGroup) viewMain.getParent();
            if (parent != null) parent.removeView(viewMain);
        }
        return viewMain;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void initView() {
        super.initView();
        view_refresh = findView(viewMain, R.id.view_refresh);
        list_refresh = findView(viewMain, R.id.list_refresh);
    }

    @Override
    protected void initData() {
        super.initData();
        if (!isFirstInit) return;
        isFirstInit = false;
        if (mAdapter == null) {
            mAdapter = new MyAdapter(getContext(), null);
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
                if (!isLoadingMore) {
                    LearningApi.getInstance().notifyRefresh(null);
                }
                loadData(isLoadingMore);
            }
        });
        list_refresh.setOnItemClickListener(this);
    }

    private void loadData(final boolean isMore) {
        if (!isMore) mCurrentPage = 0;
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> subParam = new HashMap<>();
        param.put("pageNo", ++mCurrentPage);
        param.put("pageSize", ProtocolUrl.pageSize);
        subParam.put("userId", SaveDataGlobal.getUserId());
        subParam.put("type", 1);
        param.put("condition", subParam);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getMyComments, param), new BusinessCallback(getContext()) {
            public void subCallback(boolean flag, String json) {
                if (!isAdded()) return;
                view_refresh.setRefreshing(false);
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    List<MyCommentInfo> list = JsonDataParser.j2MyCommentInfo(data);
                    if (isMore) mAdapter.addAll(list);
                    else {
                        mAdapter.replaceAll(list);
                    }
                    view_refresh.setLoadingMoreEnabled(list.size() >= ProtocolUrl.pageSize);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }


            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MyCommentInfo info = mAdapter.getItem(i);
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("docId", info.getDocId());
        startActivity(intent);

    }

    class MyAdapter extends SimpleBaseAdapter<MyCommentInfo> {

        public MyAdapter(Context context, List<MyCommentInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_my_comment;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            MyCommentInfo info = getItem(position);
            TextView txt_comment = holder.getView(R.id.txt_comment);
            txt_comment.setText(info.getRemarkContent());

            TextView txt_title = holder.getView(R.id.txt_title);
            txt_title.setText(info.getDocTitle());
            TextView txt_time = holder.getView(R.id.txt_time);
            txt_time.setText(info.getRemarkDate());
            TextView btn_label = holder.getView(R.id.btn_label);
            btn_label.setText(info.getGroupName());
            return convertView;
        }
    }


}
