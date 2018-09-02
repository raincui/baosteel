package com.android.yl.baowu.news;

import android.os.Bundle;

import com.android.yl.baowu.basebusiness.entity.ColumnInfo;
import com.android.yl.baowu.baseui.ui.BaseFragment;


public class MFragmentManager {

    static class Instance {
        static MFragmentManager m = new MFragmentManager();
    }

    public static MFragmentManager getInstance() {
        return Instance.m;
    }

    public BaseFragment newFragment(ColumnInfo info) {
        //1 推荐，0普通，2报纸
        BaseFragment fragment;
        if (info.getType() ==2) {
            fragment = NewspaperFragment.newInstance();
        } else {
            fragment = NewsListFragment.newInstance();
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", info.getType() == 1?"6":"0");
        bundle.putString("groupId", info.getGroupId());
        fragment.setArguments(bundle);
        return fragment;
    }


}
