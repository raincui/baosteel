package com.android.baosteel.lan.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.NewsInfo;
import com.android.baosteel.lan.basebusiness.entity.SpecialInfo;
import com.android.baosteel.lan.baseui.DocLinkActivity;
import com.android.baosteel.lan.baseui.customview.LJRefreshLayout;
import com.android.baosteel.lan.baseui.customview.LJRefreshListView;
import com.android.baosteel.lan.baseui.customview.StackFlow;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.baseui.ui.SimpleBaseAdapter;
import com.baosight.lan.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class NewspaperFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private View viewMain;
    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;
    private MyAdapter mAdapter;
    private ImageAdapter mImageAdapter;
    private TextView txt_title;
    private TextView txt_time;

    private View lly_paper;
    private StackFlow stackFlow;//图片滚动view


    private String guid;

    private boolean isFirstFragment;//兼容第一个碎片加载系统bug
    private boolean isFirstInit;//是否是第一次加载

    public static NewspaperFragment newInstance() {
        return new NewspaperFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstInit = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            guid = bundle.getString("groupId", "null");
        }
    }

    @Override
    protected void initView() {
        super.initView();
        txt_title = findView(viewMain, R.id.txt_title);
        view_refresh = findView(viewMain, R.id.view_refresh);
        list_refresh = findView(viewMain, R.id.list_refresh);
        lly_paper = findView(viewMain, R.id.lly_paper);
        stackFlow = findView(viewMain, R.id.flow_paper);
        txt_time = findView(viewMain, R.id.txt_time);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (view_refresh == null) {
                isFirstFragment = true;
                return;
            }
            initData();
        }
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
        stackFlow.setOnItemClickListener(this);
        stackFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NewsInfo info = mImageAdapter.getItem(position);
                txt_time.setText(info.getPubDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                txt_time.setText("");
            }
        });
        txt_title.setOnClickListener(this);
    }

    private void loadMore() {
    }

    private void loadData() {

        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getNewspaperTypeList + "/" + guid), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                view_refresh.setRefreshing(false);
                if(!flag)return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<SpecialInfo>>() {
                        }.getType();
                        List<SpecialInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        if (list == null || list.isEmpty()) {
                            txt_title.setVisibility(View.GONE);
                        } else {
                            txt_title.setVisibility(View.VISIBLE);
                            txt_title.setText(list.get(0).getTitle());
                            mAdapter.replaceAll(list);
                            loadChannelList(list.get(0));
                        }
                        return;
                    }
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });


    }

    /**
     * 获取专题下列表
     *
     * @param info
     */
    private void loadChannelList(SpecialInfo info) {
        if (info == null) return;
        if (mImageAdapter == null) {
            mImageAdapter = new ImageAdapter(getContext(), null);
            stackFlow.setAdapter(mImageAdapter);
        } else {
            txt_time.setText("");
            mImageAdapter.clear();
        }
        txt_title.setText(info.getTitle());
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getNewspaperList + "/" + info.getChannelId()), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                if(!flag)return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<NewsInfo>>() {
                        }.getType();
                        List<NewsInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        mImageAdapter.replaceAll(list);
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
            viewMain = inflater.inflate(R.layout.fragment_paper, null);
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
        if (isFirstFragment) {
            isFirstFragment = false;
            initData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == stackFlow) {
            NewsInfo info = mImageAdapter.getItem(i);
            if (info.isDocLink()) {
                Intent intent = new Intent(getContext(), DocLinkActivity.class);
                intent.putExtra("title", info.getTitle());
                intent.putExtra("docLink", info.getDocLink());
                startActivity(intent);
                return;
            }

            Intent intent = new Intent(getContext(), NewsDetailActivity.class);
            intent.putExtra("docId", info.getDocId());
            startActivity(intent);
        } else if (adapterView == list_refresh) {
            SpecialInfo info = mAdapter.getItem(i);
            loadPaper(info);
            loadChannelList(info);
            onClick(txt_title);
        }
    }

    @Override
    public void onClick(View v) {
        if (lly_paper.getVisibility() == View.VISIBLE) {
            lly_paper.setVisibility(View.GONE);
            view_refresh.setVisibility(View.VISIBLE);
            txt_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xiangshang, 0);
        } else {
            view_refresh.setVisibility(View.GONE);
            lly_paper.setVisibility(View.VISIBLE);
            txt_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xiangxia, 0);
        }
    }

    class MyAdapter extends SimpleBaseAdapter<SpecialInfo> {

        public MyAdapter(Context context, List<SpecialInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_special_list;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            SpecialInfo info = getItem(position);
            TextView name = holder.getView(R.id.txt_name);
            name.setText(info.getTitle());
            return convertView;
        }
    }

    /**
     * 加载专题下列表
     *
     * @param info
     */
    private void loadPaper(SpecialInfo info) {
        txt_title.setText(info.getTitle());
    }

    /**
     * 报纸列表adapter
     */
    class ImageAdapter extends SimpleBaseAdapter<NewsInfo> {

        public ImageAdapter(Context context, List<NewsInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_paper_list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsInfo info = getItem(position);
            if (null == convertView) {
                convertView = View.inflate(getContext(), R.layout.item_paper_list, null);
                convertView.setLayoutParams(new StackFlow.LayoutParams(dpTopx(200), dpTopx(320)));
            }
            SimpleDraweeView imageView = (SimpleDraweeView) convertView;
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            imageView.setImageURI(info.getIcon(0));
            return convertView;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            NewsInfo info = getItem(position);
            SimpleDraweeView imageView = holder.getView(R.id.img_pic);
            imageView.setImageURI(info.getIcon(0));


            return convertView;
        }
    }

    private int dpTopx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5);
    }
}
