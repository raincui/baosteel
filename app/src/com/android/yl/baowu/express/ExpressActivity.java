package com.android.yl.baowu.express;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.news.NewsListFragment;
import com.android.yl.baowu.R;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.express
 * @Title: ExpressActivity
 * @Description: 快递矩阵主页
 * Create DateTime: 2017/2/27
 */
public class ExpressActivity extends BaseActivity {

    private RadioGroup rg_menu;
    private WeiJuZhenFragment wFragment;
    private NewsListFragment nFragment;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_express);
        initView();
        initListener();
        initData();

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        TextView title = findView(R.id.tv_title);
        title.setText("快递");
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        rg_menu = findView(R.id.rg_menu);
    }

    @Override
    protected void initData() {
        super.initData();
        toNews();
    }

    @Override
    protected void initListener() {
        super.initListener();
        rg_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_1) {
                    toNews();
                } else {
                    toWeiJuZhen();
                }
            }
        });
    }

    /*
    显示消息列表
     */
    private void toNews() {
        if (nFragment == null) {
            nFragment = NewsListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("type", "4");
            nFragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, nFragment).commitAllowingStateLoss();
    }

    /*
   显示公众号列表
    */
    private void toWeiJuZhen() {
        if (wFragment == null) {
            wFragment = WeiJuZhenFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, wFragment).commitAllowingStateLoss();
    }

}
