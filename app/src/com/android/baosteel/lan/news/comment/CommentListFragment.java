package com.android.baosteel.lan.news.comment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.CommentInfo;
import com.android.baosteel.lan.baseui.customview.LJRefreshLayout;
import com.android.baosteel.lan.baseui.customview.LJRefreshListView;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.baseui.ui.SimpleBaseAdapter;
import com.baosight.lan.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class CommentListFragment extends BaseFragment {


    private View viewMain;
    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;
    private MyAdapter mAdapter;


    private String type = "0";//请求类型0最新，1最热
    private String docId;

    private int mCurrentPage;

    public static CommentListFragment newInstance() {
        return new CommentListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type", "0");
            docId = bundle.getString("docId", "");
        }
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
                loadData(isLoadingMore);
            }
        });
    }

    private void loadData(final boolean isMore) {
        if (!isMore)
            mCurrentPage = 0;
        List<String> param = new ArrayList<>();
        param.add(docId);
        param.add(type);
        param.add(String.valueOf(++mCurrentPage));
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getCommentList, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                if (!isAdded()) return;
                view_refresh.setRefreshing(false);
                if(!flag)return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<CommentInfo>>() {
                        }.getType();
                        List<CommentInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        if (isMore) {
                            mAdapter.addAll(list);
                        } else {
                            mAdapter.replaceAll(list);
                        }
                        view_refresh.setLoadingMoreEnabled(list.size() == 10);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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


    class MyAdapter extends SimpleBaseAdapter<CommentInfo> implements View.OnClickListener {

        public MyAdapter(Context context, List<CommentInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_comment;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            CommentInfo info = getItem(position);
            SimpleDraweeView img = holder.getView(R.id.img_head);
            //getCircleImageView(img);
            TextView name = holder.getView(R.id.txt_name);
            name.setText(info.getUserName());
            TextView time = holder.getView(R.id.txt_time);
            time.setText(info.getRemarkDate());
            TextView content = holder.getView(R.id.txt_content);
            content.setText(info.getRemarkContent());
            TextView goodCount = holder.getView(R.id.txt_good_count);
            goodCount.setText(String.valueOf(info.getLoveCount()));
            goodCount.setVisibility(info.getLoveCount() > 0 ? View.VISIBLE : View.GONE);
            goodCount.setTextColor(getResources().getColor(info.isGood() ? R.color.txtblue : R.color.txt2a2a2a));
            ImageView btn_good = holder.getView(R.id.btn_good);
            btn_good.setImageResource(info.isGood() ? R.drawable.yizan : R.drawable.zan);
            btn_good.setTag(position);
            btn_good.setOnClickListener(this);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            goGood(mAdapter.getItem(position));
        }
        private void getCircleImageView(SimpleDraweeView imageView) {
            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        }
    }

    /**
     * 点赞
     *
     * @param info
     */
    private void goGood(final CommentInfo info) {
        List<String> list = new ArrayList<>();
        list.add(docId);
        list.add(info.getRemarkId());
        list.add(info.isGood()?"1":"0");
        info.setIsLove(info.isGood()?1:0);
        mAdapter.notifyDataSetChanged();
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.goCommentGood, list), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                if (!isAdded()) return;
                if(!flag)return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        info.setLoveCount(info.isGood()?info.getLoveCount() + 1:info.getLoveCount() - 1);
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                    info.setIsLove(info.isGood()?1:0);
                    mAdapter.notifyDataSetChanged();
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    @Override
    public void refresh() {
        super.refresh();
        initData();
    }
}
