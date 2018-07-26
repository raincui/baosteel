package com.android.baosteel.lan.baseui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.moduleApi.ExpressApi;
import com.android.baosteel.lan.moduleApi.LearningApi;
import com.android.baosteel.lan.moduleApi.NewsApi;
import com.baosight.lan.R;

public class MainActivity extends BaseActivity {

    Fragment newsFragment;
    Fragment learnFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetApi.init(this);
        onNews(null);
    }

    public void onNews(View view) {
        if (newsFragment == null) {
            newsFragment = NewsApi.getInstance().getFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, newsFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(newsFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().hide(learnFragment).commitAllowingStateLoss();
        }
    }

    public void onLearning(View view) {
        if (learnFragment == null) {
            learnFragment = LearningApi.getInstance().getFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, learnFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(learnFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().hide(newsFragment).commitAllowingStateLoss();
        }
    }

    public void onExpress(View view) {
        ExpressApi.getInstance().openExpressActivity(this);
    }

}
