package com.android.yl.baowu.news;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.util.AppUtil;
import com.android.yl.baowu.basebusiness.util.SharedPrefAction;
import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.baseui.ui.SimpleBaseAdapter;
import com.android.yl.baowu.R;

import java.util.List;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: LookActivity
 * @Description: 搜索
 * Create DateTime: 2017/3/6
 */
public class SearchActivity extends BaseActivity {

    private ListView list_search;
    private EditText edit_search;

    private SearchAdapter mAdapter;

    private static String KEY_SEARCH = "key_search";//搜索记录本地存储key

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        list_search = findView(R.id.list_search);
        edit_search = findView(R.id.edit_search);
    }

    @Override
    protected void initData() {
        super.initData();
        List<String> list = SharedPrefAction.getKeywords(KEY_SEARCH);
        mAdapter = new SearchAdapter(this, list);
        list_search.setAdapter(mAdapter);
    }


    @Override
    protected void initListener() {
        super.initListener();
        list_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mAdapter.getCount()) {
                    //点击footView

                } else {
                    String key = mAdapter.getItem(position);
                    search(key);
                }
            }
        });
        edit_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP&&keyCode == KeyEvent.KEYCODE_ENTER) {
                    String keyword = edit_search.getText().toString().trim();
                    SharedPrefAction.putKeyword(KEY_SEARCH, keyword);
                    mAdapter.add(keyword);
                    search(keyword);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 去搜索
     *
     * @param key
     */
    private void search(String key) {
        if (TextUtils.isEmpty(key)|| AppUtil.clickFilter()) return;
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Intent intent = new Intent(this, SearchListActivity.class);
        intent.putExtra("keyword", key);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_search);
        initView();
        initListener();
        initData();
    }

    class SearchAdapter extends SimpleBaseAdapter<String> implements View.OnClickListener {

        public SearchAdapter(Context context, List<String> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_keyword_list;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            String key = getItem(position);
            TextView name = holder.getView(R.id.txt_name);
            name.setText(key);
            View view = holder.getView(R.id.btn_clean);
            view.setTag(position);
            view.setOnClickListener(this);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            SharedPrefAction.cleanKeyWord(KEY_SEARCH, false, getItem(position));
            remove(position);
        }
    }

}
