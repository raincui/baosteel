package com.android.yl.baowu.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.R;

/**
 * Created by cuiyulong on 2018/8/21.
 */

public class SettingFragment extends BaseFragment {
    private View viewMain;
    private CheckBox box_push;

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_setting, null);
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
        box_push = findView(viewMain, R.id.box_push);
        boolean isPush = SaveDataGlobal.getBoolean("push_ctrl", false);
        box_push.setChecked(isPush);
    }

    @Override
    protected void initListener() {
        super.initListener();
        box_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveDataGlobal.putBoolean("push_ctrl", isChecked);
            }
        });
    }
}
