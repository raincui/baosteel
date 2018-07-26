package com.android.baosteel.lan.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.LabelInfo;
import com.android.baosteel.lan.basebusiness.entity.NewsInfo;
import com.android.baosteel.lan.basebusiness.entity.PicInfo;
import com.android.baosteel.lan.basebusiness.util.AppUtil;
import com.android.baosteel.lan.basebusiness.util.SharedPrefAction;
import com.android.baosteel.lan.baseui.DocLinkActivity;
import com.android.baosteel.lan.baseui.customview.LJRefreshLayout;
import com.android.baosteel.lan.baseui.customview.LJRefreshListView;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.baseui.ui.SimpleBaseAdapter;
import com.android.baosteel.lan.moduleApi.LearningApi;
import com.baosight.lan.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class NewsListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private View viewMain;
    private LJRefreshLayout view_refresh;
    private LJRefreshListView list_refresh;
    private MyAdapter mAdapter;

    private boolean isRecommend;//是否是推荐
    private boolean isPush;//是否是推送
    private boolean isLearning;//是否是学习
    private boolean isSpecail;//是否是专题
    private boolean isLabel;//是否是标签

    private String type;//请求类型0栏目组，1标签，2专题，3，电子报，4推送，5学习，6推荐type
    private String guid;//栏目组id

    private boolean isFirstFragment;//兼容第一个碎片加载系统bug
    private boolean isFirstInit;//是否第一次加载

    private int mCurrentPage;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstInit = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type", "0");
            isRecommend = "6".equals(type);
            isPush = "4".equals(type);
            isLearning = "5".equals(type);
            isSpecail = "2".equals(type);
            isLabel = "1".equals(type);
            guid = bundle.getString("groupId", "null");
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
                if (!isLoadingMore && isLearning) {
                    LearningApi.getInstance().notifyRefresh(null);
                }
                loadData(isLoadingMore);
            }
        });
        list_refresh.setOnItemClickListener(this);
    }

    private void loadData(final boolean isMore) {
        if (!isMore) mCurrentPage = 0;
        List<String> param = new ArrayList<>();
        param.add(type);
        param.add(String.valueOf(++mCurrentPage));
        param.add(guid);

        StringBuilder sb = new StringBuilder(ProtocolUrl.getNews);
        sb.append("_").append(type).append("_").append(guid);
        final String key = sb.toString();
        //加载缓存
        if (!isMore && mAdapter.getCount() == 0) {
            String cacheData = SharedPrefAction.getString(key, null);
            if (cacheData != null) {
                Type type = new TypeToken<List<NewsInfo>>() {
                }.getType();
                List<NewsInfo> list = new Gson().fromJson(cacheData, type);
                mAdapter.replaceAll(list);
            }
        }

        NetApi.call(NetApi.getJsonParam(ProtocolUrl.getNews, param), new BusinessCallback(getContext()) {
            public void subCallback(boolean flag, String json) {
                if (!isAdded()) return;
                view_refresh.setRefreshing(false);
                if (!flag) return;
                /**
                 * {"data":{"data":
                 * [
                 * {"iszt":"1","picList":[],"pubDate":"2017-03-13","otherList":[],"isVideo":"1",
                 * "labelList":[{"labelid":"b7484d30-3d22-45d7-a077-5bca10d48d02","labelname":"产业规划"}],
                 * "docId":"d243312c-0e08-44dc-a01e-186f3d8dab62","videoUrl":null,
                 * "channelTitle":"爱心子栏目","showType":"0","title":"产业规划文章",
                 * "groupTitle":"爱心","groupId":"783f88f1-9bb7-44bd-8429-24b1b9298302",
                 * "recommend":"1","category":null,"channelId":"61900ad0-eced-4e9d-9232-e5ccbf14421a"},
                 */
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        String dataStr = data.optJSONArray("data").toString();
                        Type type = new TypeToken<List<NewsInfo>>() {
                        }.getType();
                        List<NewsInfo> list = new Gson().fromJson(dataStr, type);
                        if (isMore) mAdapter.addAll(list);
                        else {
                            //缓存信息
                            SharedPrefAction.putString(key, dataStr);
                            mAdapter.replaceAll(list);
                            if (isLabel) {
                                int labelCount = data.optInt("labelCount");
                                ((TextView) list_refresh.getChildAt(0).findViewById(R.id.txt_count)).setText(labelCount + "篇文章");
                            }
                        }
                        view_refresh.setLoadingMoreEnabled(list.size() >= 10);
                        return;
                    }
                    String tip = data.optJSONObject("data").optString("errorMsg");
                    if ("{}".equals(tip)) tip = "网络请求超时";
                    showToast(tip);
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
        if (isLabel || isSpecail || isLearning || isPush || isFirstFragment) {
            isFirstFragment = false;
            initData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (AppUtil.clickFilter()) return;
        i -= list_refresh.getHeaderViewsCount();
        if (i < 0) return;
        NewsInfo info = mAdapter.getItem(i);
        if (info == null) return;
        if (!isSpecail && info.isSpecial()) {
            Intent intent = new Intent(getContext(), SpecialNewsActivity.class);
            intent.putExtra("picUrl", info.getIcon(0));
            intent.putExtra("title", info.getChannelTitle());
            intent.putExtra("groupId", info.getChannelId());
            startActivity(intent);
            return;
        }
        //标记已读
        info.setReaded();
        mAdapter.notifyDataSetChanged();
//        markReaded(info.getDocId());

        if (info.isDocLink()) {
            Intent intent = new Intent(getContext(), DocLinkActivity.class);
            intent.putExtra("title", info.getTitle());
            intent.putExtra("docLink", info.getDocLink());
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("docId", info.getDocId());
        intent.putExtra("noMenu", isLearning | isPush);
        intent.putExtra("fromLearning",isLearning);
        startActivity(intent);


    }

    /**
     * 标记已读
     *
     * @param docId
     */
    private void markReaded(String docId) {
        StringBuilder url = new StringBuilder(ProtocolUrl.markDocReaded);
        url.append("/").append(docId);
        NetApi.call(NetApi.getJsonParam(url.toString()), new BusinessCallback(getActivity()) {
            @Override
            public void subCallback(boolean success, String json) {

            }
        });
    }


    class MyAdapter extends SimpleBaseAdapter<NewsInfo> implements View.OnClickListener {

        public MyAdapter(Context context, List<NewsInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            NewsInfo info = getItem(position);
            //学习和推送特殊处理
            if (isLearning | isPush) {
                List<PicInfo> list = info.getPicList();
                if (list == null || list.isEmpty()) return R.layout.item_news_nopic;//无图
                return R.layout.item_news_smallpic;//小图
            }

            boolean isSpecialItem = !isLabel && !isSpecail && info.isSpecial();//普通列表的专题条目
            if (info.isTop()) return R.layout.item_news_toppic;//置顶
            if (isSpecialItem) return R.layout.item_news_bigpic;//专题
            if (info.isVideo()) return R.layout.item_news_video;//视频
            if (info.isBigPic()) return R.layout.item_news_bigpic;//大图
            if (info.isSmallPic()) return R.layout.item_news_smallpic;//小图
            if (info.isMorePic()) return R.layout.item_news_morepic;//多图
            if (info.isNoPic()) return R.layout.item_news_nopic;//无图
            return R.layout.item_news_nopic;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsInfo info = getItem(position);
            ViewHolder holder;
            if (convertView != null && isEqualsType((NewsInfo) convertView.getTag(R.id.tagId), info)) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, getItemResource(position), null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setTag(R.id.tagId, info);
            }
            return getItemView(position, convertView, holder);
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            NewsInfo info = getItem(position);
            TextView title = holder.getView(R.id.txt_title);

            boolean isSpecialItem = !isLabel && !isSpecail && info.isSpecial();//普通列表的专题条目
            if (isSpecialItem) {
                title.setText(Html.fromHtml("<img src='" + R.drawable.label_special + "'  />", imageGetter, null));
                title.append("  " + info.getChannelTitle());
            } else {
                title.setText(info.getTitle());
            }
            TextView describe = holder.getView(R.id.txt_describe);
            if (describe != null) {
                describe.setVisibility(isLearning ? View.VISIBLE : View.GONE);
                describe.setText(info.getChannelTitle());
            }


            TextView time = holder.getView(R.id.txt_time);
            if (time != null)
                time.setText(formatTime(info.getPubDate()));
            SimpleDraweeView icon = holder.getView(R.id.img_icon);
            if (icon != null) {
                icon.setImageURI(info.getIcon(0));
            }
            SimpleDraweeView icon1 = holder.getView(R.id.img_icon1);
            if (icon1 != null) {
                icon1.setImageURI(info.getIcon(1));
            }
            SimpleDraweeView icon2 = holder.getView(R.id.img_icon2);
            if (icon2 != null) {
                icon2.setImageURI(info.getIcon(2));
            }

            TextView label = holder.getView(R.id.btn_label);
            if (label != null) {
                LabelInfo labelInfo = info.getlabel(0);
                label.setTag(isRecommend ? info.getGroupId() : labelInfo);
                label.setOnClickListener(this);
                label.setTextColor(isLabel || isRecommend ? Color.WHITE : getResources().getColor(R.color.txtblue));
                label.setBackgroundResource(isLabel || isRecommend ? R.color.txtblue : R.drawable.shape_stroke_blue);
                label.setVisibility((isRecommend || labelInfo != null) && !isSpecialItem ? View.VISIBLE : View.GONE);
                label.setText(info.isTop() ? "置顶" : (isLabel || isRecommend ? info.getGroupTitle() : (labelInfo != null ? labelInfo.getLabelname() : "")));
            }
            TextView label1 = holder.getView(R.id.btn_label1);
            if (label1 != null) {
                LabelInfo labelInfo1 = info.getlabel(1);
                label1.setTag(labelInfo1);
                label1.setOnClickListener(this);
                label1.setVisibility(isSpecialItem || isLabel || isRecommend || labelInfo1 == null ? View.GONE : View.VISIBLE);
                label1.setText(labelInfo1 != null ? labelInfo1.getLabelname() : "");
            }

            if (isPush && !info.isReaded())
                time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_point,0, 0, 0);
            else
                time.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            return convertView;
        }

        /**
         * 格式化时间
         */
        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        private String formatTime(String time) {
            Calendar today = Calendar.getInstance();
            Calendar cuTime = Calendar.getInstance();
            try {
                Date date = sdf.parse(time);
                cuTime.setTime(date);
                if (today.get(Calendar.DAY_OF_YEAR) == cuTime.get(Calendar.DAY_OF_YEAR) && today.get(Calendar.YEAR) == cuTime.get(Calendar.YEAR))
                    return "今天";
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return time;
        }

        /**
         * 是否是同一种布局
         *
         * @param info1
         * @param info2
         * @return
         */
        private boolean isEqualsType(NewsInfo info1, NewsInfo info2) {
            if (info1 == null || info2 == null) return false;
            if (info1.isTop() ^ info2.isTop()) return false;
            if (info1.isVideo() ^ info2.isVideo()) return false;
            return info1.isTop() && info2.isTop() ||
                    info1.isVideo() && info2.isVideo() ||
                    info1.getShowType().equals(info2.getShowType());

        }

        @Override
        public void onClick(View v) {
            if (AppUtil.clickFilter()) return;
            if (isLabel) return;
            if (isRecommend) {
                scrollTo((String) v.getTag());
            } else {
                LabelInfo info = (LabelInfo) v.getTag();
                Intent intent = new Intent(getContext(), LabelNewsActivity.class);
                intent.putExtra("title", info.getLabelname());
                intent.putExtra("count", "46512");
                intent.putExtra("labelId", info.getLabelid());
                startActivity(intent);
            }
        }
    }

    Html.ImageGetter imageGetter = new Html.ImageGetter() {

        @Override
        public Drawable getDrawable(String source) {
            int id = Integer.parseInt(source);
            Drawable drawable = getResources().getDrawable(id);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        }
    };

    public void scrollTo(String groupId) {
        ((NewsFragment) getParentFragment()).scrollTo(groupId);
    }

    public void setHeadView(View view) {
        list_refresh.addHeaderView(view, null, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (view_refresh != null) {
            view_refresh.setRefreshing(false);
        }
    }
}
