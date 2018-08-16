package com.android.baosteel.lan.baseui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.mine.MineFragment;
import com.android.baosteel.lan.moduleApi.NewsApi;
import com.android.baosteel.lan.news.NewsFragment;
import com.android.baosteel.lan.news.SearchActivity;
import com.baosight.lan.R;

public class MainActivity extends BaseActivity {

    NewsFragment newsFragment;
    MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onNews(null);
    }

    public void onNews(View view) {
        findViewById(R.id.rly_title).setVisibility(View.VISIBLE);
        if (mineFragment != null)
            getSupportFragmentManager().beginTransaction().hide(mineFragment).commitAllowingStateLoss();
        if (newsFragment == null) {
            newsFragment = NewsApi.getInstance().getFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, newsFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(newsFragment).commitAllowingStateLoss();
            newsFragment.scrollTo("17");
        }
    }

    public void onVideo(View view) {
        onNews(view);
        newsFragment.scrollTo("25");
    }

    public void onPaper(View view) {
        onNews(view);
        newsFragment.scrollTo("18");
    }

    public void onMine(View view) {
        findViewById(R.id.rly_title).setVisibility(View.GONE);
        if (newsFragment != null)
            getSupportFragmentManager().beginTransaction().hide(newsFragment).commitAllowingStateLoss();
        if (mineFragment == null) {
            mineFragment = MineFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, mineFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(mineFragment).commitAllowingStateLoss();
        }
    }

    public void onSearch(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onMineInfo(View view){

    }
    public void onMineCollect(View view){

    }
    public void onMineTalk(View view){

    }
    public void onMineAnswer(View view){

    }
    public void onSetting(View view){

    }


}
