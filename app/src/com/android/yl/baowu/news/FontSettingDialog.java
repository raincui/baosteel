package com.android.yl.baowu.news;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.android.yl.baowu.R;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class FontSettingDialog extends Dialog {

    private SeekBar seekBar;
    private RadioGroup rg;
    private onSettingListener listener;
    private int currentSize;
    private int currentLight;

    private static int font1 = 90;
    private static int font2 = 110;
    private static int font3 = 130;
    private static int font4 = 150;
    private static int font5 = 170;


    public FontSettingDialog(Context context,int currentSize,int currentLight,onSettingListener listener) {
        super(context, R.style.LJAlertDialogStyle);
        this.listener = listener;
        this.currentSize = currentSize;
        this.currentLight = currentLight;
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_setting);
        seekBar = (SeekBar) findViewById(R.id.seek_bar_sun);
        seekBar.setProgress(currentLight);
        rg = (RadioGroup) findViewById(R.id.rg_fonts);
        rg.check(getCheckedId());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                currentSize = i==R.id.rb1?font1:
                        i==R.id.rb2?font2:
                                i == R.id.rb3?font3:
                                        i == R.id.rb4?font4:font5;
                listener.onSetting(currentSize,currentLight);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!b)return;
                currentLight = i;
                listener.onSetting(currentSize,currentLight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.onSettingEnd(currentLight);
            }
        });
    }


    private int getCheckedId(){
        if(currentSize<=font1)return R.id.rb1;
        if(currentSize<=font2)return R.id.rb2;
        if(currentSize<=font3)return R.id.rb3;
        if(currentSize<=font4)return R.id.rb4;
        if(currentSize<=font5)return R.id.rb5;
        return R.id.rb5;
    }

    interface onSettingListener{
        void onSetting(int txtSize,int light);
        void onSettingEnd(int light);
    }

}
