package com.android.baosteel.lan.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.baosteel.lan.basebusiness.util.SaveDataGlobal;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.baosight.lan.R;

/**
 * Created by cuiyulong on 2018/8/21.
 */

public class ModifyFragment extends BaseFragment {
    private View viewMain;

    public static ModifyFragment newInstance() {
        ModifyFragment fragment = new ModifyFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_modify, null);
        else {
            ViewGroup parent = (ViewGroup) viewMain.getParent();
            if (parent != null) parent.removeView(viewMain);
        }
        return viewMain;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();

    }

    @Override
    protected void initView() {
        super.initView();

    }

    @Override
    protected void initListener() {
        super.initListener();

    }
}
