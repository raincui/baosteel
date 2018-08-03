package com.android.baosteel.lan.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.ColumnInfo;
import com.android.baosteel.lan.basebusiness.util.SharedPrefAction;
import com.android.baosteel.lan.baseui.customview.PagerSlidingTabStrip;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.news.comment.MineInfoActivity;
import com.baosight.lan.R;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yulong.cui
 * @Title: NewsFragment
 * Create DateTime: 2017/2/27
 */
public class NewsFragment extends BaseFragment implements View.OnClickListener {

    private View viewMain;
    private PagerSlidingTabStrip news_tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private List<ColumnInfo> mList;//所有栏目

    private boolean isFirst;//是不是第一次启动

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        isFirst = true;
        Fresco.initialize(getActivity().getApplicationContext());
        SharedPrefAction.open(getActivity().getApplicationContext(), "baosteel_spf");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMain = inflater.inflate(R.layout.fragment_news, null);
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        news_tabs = (PagerSlidingTabStrip) viewMain.findViewById(R.id.steel_tabs);
        pager = (ViewPager) viewMain.findViewById(R.id.steel_pager);
        pager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initListener() {
        super.initListener();
        findView(viewMain, R.id.btn_search).setOnClickListener(this);
        findView(viewMain, R.id.btn_look).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if (!isFirst) return;
        isFirst = false;
        loadColumnData();
    }

    /**
     * 加载所有栏目
     */
    public void loadColumnData() {
        String url = ProtocolUrl.getColumn;
        NetApi.call(NetApi.getJsonParam(url,false), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                if (!isAdded()) return;
                if(!flag)return;
                try {
                    JSONObject info = new JSONObject(json);
                        ColumnInfoHelper.getInstance().cacheData(info);
                        adapter = new MyPagerAdapter(getChildFragmentManager(), ColumnInfoHelper.getInstance().getTitleColumns());
                        pager.setAdapter(adapter);
                        news_tabs.setViewPager(pager);
                        return;
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_search) {
            startActivity(new Intent(getContext(), SearchActivity.class));
        } else if (v.getId() == R.id.btn_look) {
            startActivityForResult(new Intent(getContext(), MineInfoActivity.class), 1000);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private List<ColumnInfo> list = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm, List<ColumnInfo> list) {
            super(fm);
            if (list == null) return;
            this.list.clear();
            this.list.addAll(list);
        }

        public ColumnInfo getItemObject(int position) {
            return list.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).getGroupName();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            getChildFragmentManager().beginTransaction().remove((Fragment) object).commitAllowingStateLoss();

        }

        @Override
        public Fragment getItem(int position) {
            return MFragmentManager.getInstance().newFragment(list.get(position));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == 1000) {
            adapter = new MyPagerAdapter(getChildFragmentManager(), ColumnInfoHelper.getInstance().getTitleColumns());
            pager.setAdapter(adapter);
            news_tabs.setViewPager(pager);
        }
    }

    public void scrollTo(String columnId) {
        for (int positon = 0; positon < adapter.getCount(); positon++) {
            ColumnInfo info = adapter.getItemObject(positon);
            if (columnId.equals(info.getGroupId())) {
                pager.setCurrentItem(positon);
                break;
            }
        }
    }

}
