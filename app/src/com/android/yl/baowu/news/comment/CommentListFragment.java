package com.android.yl.baowu.news.comment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.CommentInfo;
import com.android.yl.baowu.basebusiness.util.JsonDataParser;
import com.android.yl.baowu.baseui.customview.LJRefreshLayout;
import com.android.yl.baowu.baseui.customview.LJRefreshListView;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;
import com.android.yl.baowu.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> subParam = new HashMap<>();
        param.put("pageNo", ++mCurrentPage);
        param.put("pageSize", ProtocolUrl.pageSize);
        subParam.put("contentId", docId);
        subParam.put("type", type);//0最新，1最热
        param.put("condition", subParam);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getCommentList, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!isAdded()) return;
                view_refresh.setRefreshing(false);
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    List<CommentInfo> list = JsonDataParser.j2CommentInfo(data);
                    if (isMore) {
                        mAdapter.addAll(list);
                    } else {
                        mAdapter.replaceAll(list);
                    }
                    view_refresh.setLoadingMoreEnabled(list.size() == ProtocolUrl.pageSize);
                    return;
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


        Map<String, Object> param = new HashMap<>();
        param.put("commentId", info.getRemarkId());
        param.put("type", info.isGood() ? 0 : 1);
        info.setIsLove(info.isGood() ? 0 : 1);
        mAdapter.notifyDataSetChanged();
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.goCommentGood, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!isAdded()) return;
                if (!flag) {
                    info.setIsLove(info.isGood() ? 0 : 1);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                info.setLoveCount(info.isGood() ? info.getLoveCount() + 1 : info.getLoveCount() - 1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void refresh() {
        super.refresh();
        initData();
    }
}
