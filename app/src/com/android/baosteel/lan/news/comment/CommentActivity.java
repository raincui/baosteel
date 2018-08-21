package com.android.baosteel.lan.news.comment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.baosight.lan.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: CommentActivity
 * @Description: 评论列表
 * Create DateTime: 2017/3/21
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    BaseFragment hotFragment;
    BaseFragment newFragment;
    private RadioGroup rg_menu;
    private String docId;
    private View btn_talk;

    private View rly_edit;
    private View btn_edit_cancel;
    private View btn_edit_send;
    private EditText edit_input;

    private BaseFragment currentFragment;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        rg_menu = findView(R.id.rg_menu);
        btn_talk = findView(R.id.btn_talk);
        rly_edit = findView(R.id.rly_edit_talk);
        btn_edit_cancel = findView(R.id.btn_cancel_edit);
        btn_edit_send = findView(R.id.btn_commit_edit);
        edit_input = findView(R.id.edit_talk);
    }

    @Override
    protected void initData() {
        super.initData();
        docId = getIntent().getStringExtra("docId");
        goHotFragment();
    }

    @Override
    protected void initListener() {
        super.initListener();
        rg_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_1) {
                    goHotFragment();
                } else {
                    goNewFragment();
                }
            }
        });
        btn_talk.setOnClickListener(this);
        btn_edit_cancel.setOnClickListener(this);
        btn_edit_send.setOnClickListener(this);

    }

    private void goTalk(String comment) {
        if (TextUtils.isEmpty(comment)) {
            showToast("请输入评论内容");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("contentId", docId);
        map.put("content", comment);
        map.put("referenceId ", 0);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.goComment, map), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (!flag) return;
                if (isFinishing()) return;
                currentFragment.refresh();
                rly_edit.setVisibility(View.GONE);
                showToast("评论成功");
            }
        });


    }


    /**
     * 最新
     */
    public void goNewFragment() {
        if (newFragment == null) {
            newFragment = CommentListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("type", "0");
            bundle.putString("docId", docId);
            newFragment.setArguments(bundle);
            if (hotFragment != null)
                getSupportFragmentManager().beginTransaction().hide(hotFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, newFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().hide(hotFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().show(newFragment).commitAllowingStateLoss();
        }
        currentFragment = newFragment;
    }

    /**
     * 最热
     */
    public void goHotFragment() {
        if (hotFragment == null) {
            hotFragment = CommentListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("type", "1");
            bundle.putString("docId", docId);
            hotFragment.setArguments(bundle);
            if (newFragment != null)
                getSupportFragmentManager().beginTransaction().hide(newFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, hotFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().hide(newFragment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().show(hotFragment).commitAllowingStateLoss();
        }
        currentFragment = hotFragment;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_talk) {
            //写评论
            rly_edit.setVisibility(View.VISIBLE);
            edit_input.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(edit_input, 0);
        } else if (id == R.id.btn_cancel_edit) {
            rly_edit.setVisibility(View.GONE);
        } else if (id == R.id.btn_commit_edit) {
            String talk = edit_input.getText().toString();
            if (TextUtils.isEmpty(talk)) {
                showToast("请输入评论");
                return;
            }
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(CommentActivity.this.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            goTalk(talk);
        }
    }
}
