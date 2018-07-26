package com.android.baosteel.lan.express;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.WeChatInfo;
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
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class WeiJuZhenFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private View viewMain;
    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;

    private GongzhAdapter mAdapter;
    private boolean isFirstInit;//兼容第一个碎片加载系统bug

    private int mCurrentPage;

    public static WeiJuZhenFragment newInstance() {
        return new WeiJuZhenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstInit = true;
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
        if (isFirstInit) {
            isFirstInit = false;
            initData();
        }
    }

    private void loadData(final boolean isMore) {
        if (!isMore)
            mCurrentPage = 0;
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getWeiJuZhen + "/" + (++mCurrentPage)), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!isAdded()) return;
                view_refresh.setRefreshing(false);
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        Type type = new TypeToken<List<WeChatInfo>>() {
                        }.getType();
                        List<WeChatInfo> list = new Gson().fromJson(data.optJSONArray("data").toString(), type);
                        if (isMore) mAdapter.addAll(list);
                        else {
                            mAdapter.replaceAll(list);
                        }
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
    protected void initView() {
        super.initView();
        view_refresh = findView(viewMain, R.id.view_refresh);
        list_refresh = findView(viewMain, R.id.list_refresh);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mAdapter == null) {
            mAdapter = new GongzhAdapter(getContext(), null);
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
        list_refresh.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        WeChatInfo info = mAdapter.getItem(i);
        Intent intent = new Intent(getActivity(), WechatListActivity.class);
        intent.putExtra("id", info.getChannelId());
        intent.putExtra("title", info.getTitle());
        startActivityForResult(intent,1000);
    }

    class GongzhAdapter extends SimpleBaseAdapter<WeChatInfo> {

        public GongzhAdapter(Context context, List<WeChatInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_weijuzhen;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            WeChatInfo info = getItem(position);
            TextView title = holder.getView(R.id.txt_title);
            title.setText(info.getTitle());
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, info.getNotReadCount() > 0 ? R.drawable.icon_point : 0, 0);

            TextView content = holder.getView(R.id.txt_content);
            content.setText(info.getDescription());

            SimpleDraweeView image = holder.getView(R.id.img_icon);
            getCircleImageView(image);
            image.setImageURI(info.getChannelPic());
            return convertView;
        }
    }

    private void getCircleImageView(SimpleDraweeView imageView) {
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (view_refresh != null) {
            view_refresh.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
    }
}
