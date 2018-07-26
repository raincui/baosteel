package com.android.baosteel.lan.news;

import android.os.Bundle;

import com.android.baosteel.lan.basebusiness.entity.ColumnInfo;
import com.android.baosteel.lan.baseui.ui.BaseFragment;


public class MFragmentManager {

    static class Instance {
        static MFragmentManager m = new MFragmentManager();
    }

    public static MFragmentManager getInstance() {
        return Instance.m;
    }

    public BaseFragment newFragment(ColumnInfo info) {
        //-1 推荐，0普通，1报纸
        BaseFragment fragment;
        if (info.getType() ==1) {
            fragment = NewspaperFragment.newInstance();
        } else {
            fragment = NewsListFragment.newInstance();
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", info.getType() == -1?"6":"0");
        bundle.putString("groupId", info.getGroupId());
        fragment.setArguments(bundle);
        return fragment;
    }


}
