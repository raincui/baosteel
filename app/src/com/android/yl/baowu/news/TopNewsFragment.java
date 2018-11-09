package com.android.yl.baowu.news;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.yl.baowu.R;
import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.NewsInfo;
import com.android.yl.baowu.basebusiness.util.JsonDataParser;
import com.android.yl.baowu.baseui.DocLinkActivity;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopNewsFragment extends BaseFragment {

    private View rootView;
    private ConvenientBanner banner_top;
    private List<NewsInfo> newsInfos;


    public TopNewsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TopNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopNewsFragment newInstance() {
        TopNewsFragment fragment = new TopNewsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_news, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        super.initView();
        banner_top = findView(rootView, R.id.banner_top);
    }


    class ImageViewHolder implements Holder<NewsInfo> {
        private View itemView;

        @Override
        public View createView(Context context) {
            itemView = View.inflate(context, R.layout.item_news_banner, null);
            return itemView;
        }

        @Override
        public void UpdateUI(Context context, int position, NewsInfo data) {
            TextView title = findView(itemView, R.id.txt_title);
            title.setText(data.getTitle());
            SimpleDraweeView icon = findView(itemView, R.id.img_icon);
            icon.setImageURI(data.getIcon(0));
        }
    }

    @Override
    protected void initData() {
        super.initData();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("isColor", 1);
        map.put("condition", subParam);
        map.put("pageNo", 1);
        map.put("pageSize", 1000);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getColoredNews, map), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean success, String json) {
                if(!isAdded())return;
                if (!success) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    JSONArray ja = data.optJSONArray("data");
                    newsInfos = JsonDataParser.j2NewsInfos(ja);
                    banner_top.setPages(new CBViewHolderCreator<ImageViewHolder>() {
                        @Override
                        public ImageViewHolder createHolder() {
                            return new ImageViewHolder();
                        }
                    }, newsInfos);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        banner_top.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                NewsInfo info = newsInfos.get(position);
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
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        banner_top.stopTurning();
    }

    @Override
    public void onResume() {
        super.onResume();
        banner_top.startTurning(2000);
    }
}
