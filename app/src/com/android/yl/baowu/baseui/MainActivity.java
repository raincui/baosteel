package com.android.yl.baowu.baseui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.android.yl.baowu.basebusiness.entity.UserInfo;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.mine.MineFragment;
import com.android.yl.baowu.mine.MineNewsActivity;
import com.android.yl.baowu.moduleApi.NewsApi;
import com.android.yl.baowu.news.NewsFragment;
import com.android.yl.baowu.news.SearchActivity;
import com.android.yl.baowu.R;

public class MainActivity extends BaseActivity {

    NewsFragment newsFragment;
    MineFragment mineFragment;

    public static final int request_code_modify = 1000;

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
        MineNewsActivity.start(this,"修改个人资料",MineNewsActivity.FRAGMENT_MINE_MODIFY, request_code_modify);
    }
    public void onMineCollect(View view){
        MineNewsActivity.start(this,"我的收藏",MineNewsActivity.FRAGMENT_MINE_COLLECT);
    }
    public void onMineTalk(View view){
        MineNewsActivity.start(this,"我的话题",MineNewsActivity.FRAGMENT_MINE_TALK);
    }
    public void onMineAnswer(View view){
        MineNewsActivity.start(this,"我的回复",MineNewsActivity.FRAGMENT_MINE_ANSWER);
    }
    public void onSetting(View view){
        MineNewsActivity.start(this,"设置",MineNewsActivity.FRAGMENT_MINE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= Activity.RESULT_OK)return;
        switch(requestCode){
            case request_code_modify:
                UserInfo userInfo= SaveDataGlobal.getUserInfo();
                if(userInfo == null){
                    RadioGroup radioGroup = findView(R.id.rg_menu);
                    radioGroup.check(R.id.rb_1);
                    onNews(null);

                }else{
                    if (mineFragment!=null)
                    mineFragment.refresh();
                }
                break;
        }
    }
}
