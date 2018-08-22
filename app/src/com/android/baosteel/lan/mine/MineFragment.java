package com.android.baosteel.lan.mine;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.entity.UserInfo;
import com.android.baosteel.lan.basebusiness.util.SaveDataGlobal;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.login.LoginActivity;
import com.baosight.lan.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {

    private View rootView;


    public MineFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MineFragment.
     */
    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)initView();
    }

    @Override
    protected void initView() {
        super.initView();
        if(getActivity().isFinishing())return;
        UserInfo userInfo = SaveDataGlobal.getUserInfo();
        if(userInfo == null){
            showToast("请先登陆");
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }
        TextView txt_name = findView(rootView,R.id.txt_name);
        txt_name.setText(userInfo.getUserName());

        TextView txt_phone = findView(rootView,R.id.txt_phone);
        txt_phone.setText(getString(R.string.mine_phone,userInfo.getUserPhone()));
    }

    @Override
    public void refresh() {
        super.refresh();
        initView();
    }
}
