package com.android.baosteel.lan.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.news.comment.LookFragment;
import com.android.baosteel.lan.news.comment.MyAnswerFragment;
import com.android.baosteel.lan.news.comment.MyCollectFragment;
import com.android.baosteel.lan.news.comment.MyCommentFragment;
import com.baosight.lan.R;

/**
 * @author yulong.cui
 *         Create DateTime: 2017/3/6
 */
public class MineNewsActivity extends BaseActivity {
    private TextView txt_title;
    public final static int FRAGMENT_MINE_COLLECT = 1000;
    public final static int FRAGMENT_MINE_TALK = 1001;
    public final static int FRAGMENT_MINE_ANSWER = 1002;
    public final static int FRAGMENT_MINE_LOOK = 1003;

    @Override
    protected void initTitle() {
        super.initTitle();
        txt_title = findView(R.id.tv_title);
        String title = getIntent().getStringExtra("title");
        txt_title.setText(title);
    }

    public static void start(Context context, String title, int fragment) {
        Intent intent = new Intent(context, MineNewsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("fragment", fragment);
        context.startActivity(intent);
    }

    public static void start(Fragment context, String title, int fragment, int resultId) {
        Intent intent = new Intent(context.getContext(), MineNewsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("fragment", fragment);
        context.startActivityForResult(intent, resultId);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
    }

    @Override
    protected void initData() {
        super.initData();
        int fragmentId = getIntent().getIntExtra("fragment", FRAGMENT_MINE_COLLECT);
        BaseFragment fragment;
        switch (fragmentId) {
            case FRAGMENT_MINE_ANSWER:
                fragment = new MyAnswerFragment();
                break;
            case FRAGMENT_MINE_COLLECT:
                fragment = new MyCollectFragment();
                break;
            case FRAGMENT_MINE_TALK:
                fragment = new MyCommentFragment();
                break;
            case FRAGMENT_MINE_LOOK:
                fragment = new LookFragment();
                break;
            default:
                fragment = new MyCollectFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commitAllowingStateLoss();

    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_common);
        initView();
        initListener();
        initData();
    }
}
