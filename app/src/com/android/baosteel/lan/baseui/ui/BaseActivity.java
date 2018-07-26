package com.android.baosteel.lan.baseui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.baosteel.lan.basebusiness.util.InputUtil;


/**
 */
public class BaseActivity extends FragmentActivity {

    private BroadcastReceiver broadcastReceiver = null;

    private boolean isCancel;

    protected LocalBroadcastManager localBroadcastManager;

    protected Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (!isCancel && !isFinishing()) {
                message(msg);
            }
            return false;
        }
    });

    protected void cancel() {
        isCancel = true;
    }


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(saveInstanceState);
        registerBroadCast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadCast();
    }

    protected <T> T findView(int id) {
        return (T) this.findViewById(id);
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

    // 显示消息框
    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(int res) {
        showToast(getString(res));
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        try {
            res.updateConfiguration(config, res.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private void registerBroadCast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(BaseActivity.this);
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
            BaseActivity.this.onReceive(context, intent);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                InputUtil.hideSoftInput(v.getWindowToken(), BaseActivity.this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public void onBack(View view) {
        finish();
    }
}
