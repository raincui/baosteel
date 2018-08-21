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

public class FontFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener{
    private View viewMain;


    private static int font1 = 90;
    private static int font2 = 110;
    private static int font3 = 130;
    private static int font4 = 150;
    private static int font5 = 170;

    private CheckBox font_small,font_middle,font_big,font_large,font_great;

    private CompoundButton currentBox;

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_font, null);
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
        font_big = findView(viewMain,R.id.font_big);
        font_small = findView(viewMain,R.id.font_small);
        font_middle = findView(viewMain,R.id.font_middle);
        font_large = findView(viewMain,R.id.font_large);
        font_great = findView(viewMain,R.id.font_great);
    }

    @Override
    protected void initListener() {
        super.initListener();
        font_big.setOnCheckedChangeListener(this);
        font_small.setOnCheckedChangeListener(this);
        font_middle.setOnCheckedChangeListener(this);
        font_large.setOnCheckedChangeListener(this);
        font_great.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        int fontSize = SaveDataGlobal.getInt("fontSize", font2);
        currentBox = findView(viewMain,getCheckedId(fontSize));
        currentBox.setChecked(true);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isChecked||currentBox==buttonView)return;
        currentBox.setChecked(false);
        currentBox = buttonView;
        int fontSize = font2;
        switch (buttonView.getId()){
            case R.id.font_small:
                fontSize = font1;
                break;
            case R.id.font_middle:
                fontSize = font2;
                break;
            case R.id.font_big:
                fontSize = font3;
                break;
            case R.id.font_large:
                fontSize = font4;
                break;
            case R.id.font_great:
                fontSize = font5;
                break;
        }
        SaveDataGlobal.putInt("fontSize", fontSize);
    }
    private int getCheckedId(int currentSize){
        if(currentSize<=font1)return R.id.font_small;
        if(currentSize<=font2)return R.id.font_middle;
        if(currentSize<=font3)return R.id.font_big;
        if(currentSize<=font4)return R.id.font_large;
        if(currentSize<=font5)return R.id.font_great;
        return R.id.font_middle;
    }
}
