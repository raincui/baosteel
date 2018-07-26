package com.android.baosteel.lan.baseui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;


/**
 * Created by czl on 2016/5/3.
 */
public class BaseFragment extends Fragment {
    private BroadcastReceiver broadcastReceiver = null;

    private boolean isCancel;

    protected LocalBroadcastManager localBroadcastManager;
    protected Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (!isCancel && isAdded()) {
                message(msg);
            }
            return false;
        }
    });

    protected void cancel() {
        isCancel = true;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        registerBroadCast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadCast();
    }


    /**
     * 描述：初始化界面Title
     * 返回类型：void
     */
    protected void initTitle() {

    }

    /**
     * 描述：初始化界面控件
     * 返回类型：void
     */
    protected void initView() {

    }

    /**
     * 描述：获取业务数据
     * 返回类型：void
     */
    protected void initData() {

    }

    /**
     * 描述：初始化控件监听
     * 返回类型：void
     */
    protected void initListener() {

    }

    protected void message(Message msg) {

    }

    protected void showToast(final String text) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
            }
        });

    }
    protected void showToast(int text) {
        showToast(getString(text));
    }


    private void registerBroadCast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter mIntentFilter = createIntentFilter();
        if (mIntentFilter != null && mIntentFilter.countActions() > 0) {
            broadcastReceiver = new BaseBroadcast();
            localBroadcastManager.registerReceiver(broadcastReceiver, mIntentFilter);
        }
    }

    private void unRegisterBroadCast() {
        if (broadcastReceiver != null && localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
        }
    }

    protected IntentFilter createIntentFilter() {
        return null;
    }

    protected void onReceive(Context context, Intent intent) {

    }

    private class BaseBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseFragment.this.onReceive(context, intent);
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    protected <T extends View>T findView(View view,int res){
       return  (T)view.findViewById(res);
    }

    public void refresh(){

    }

}
