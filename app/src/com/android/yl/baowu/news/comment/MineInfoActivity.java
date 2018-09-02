package com.android.yl.baowu.news.comment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news.comment
 * @Title: MineInfoActivity
 * @Description: 我相关信息
 * Create DateTime: 2017/4/13
 */
public class MineInfoActivity extends BaseActivity {

    private ViewPager viewPager;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_mineinfo);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        radioGroup = findView(R.id.rg_menu);
        viewPager = findView(R.id.steel_pager);
    }

    @Override
    protected void initData() {
        super.initData();
        List<Fragment> list = new ArrayList<>();
        list.add(new LookFragment());
        list.add(new MyCommentFragment());
        list.add(new MyCollectFragment());
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), list));
    }

    @Override
    protected void initListener() {
        super.initListener();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_1) {
                    viewPager.setCurrentItem(0, true);
                } else if (i == R.id.rb_2) {
                    viewPager.setCurrentItem(1, true);
                } else if (i == R.id.rb_3) {
                    viewPager.setCurrentItem(2, true);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position == 0 ? R.id.rb_1 : (position == 1 ? R.id.rb_2 : R.id.rb_3));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> list = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            if (list != null && !list.isEmpty())
                this.list.addAll(list);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
