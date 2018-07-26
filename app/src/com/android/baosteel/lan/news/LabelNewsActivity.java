package com.android.baosteel.lan.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.baosight.lan.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: LookActivity
 * @Description: 标签文章列表
 * Create DateTime: 2017/3/6
 */
public class LabelNewsActivity extends BaseActivity {
    private TextView txt_title;


    @Override
    protected void initTitle() {
        super.initTitle();
        txt_title = findView(R.id.tv_title);
        txt_title.setText("标签");
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
    }

    @Override
    protected void initData() {
        super.initData();
        final NewsListFragment nFragment = NewsListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("type", "1");
        bundle.putString("groupId", getIntent().getStringExtra("labelId"));
        nFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, nFragment).commitAllowingStateLoss();
        txt_title.post(new Runnable() {
            @Override
            public void run() {
                View view = View.inflate(LabelNewsActivity.this, R.layout.view_label_head, null);
                nFragment.setHeadView(view);
                ((TextView) view.findViewById(R.id.txt_name)).setText(getIntent().getStringExtra("title"));
            }
        });

    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_label_news);
        initView();
        initListener();
        initData();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }
}
