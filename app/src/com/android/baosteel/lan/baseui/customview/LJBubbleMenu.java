package com.android.baosteel.lan.baseui.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baosight.lan.R;


/**
 * @author yulong.cui
 * @Description: 气泡菜单
 * Create DateTime: 2016/4/17
 */
public class LJBubbleMenu extends PopupWindow implements View.OnClickListener {
    private String[] menus = new String[0];
    private Context context;
    private int width;
    private int height;
    private boolean isTopBar;//箭头是否朝上
    private View currentView;//当前参照的view，即长按的view

    private int offsetX;//x的偏移值
    private int offsetY;//y的偏移值

    private int offsetBarX;//箭头的偏移

    private DisplayMetrics screenMetrice;

    private OnMenuItemClickListener listener;

    private boolean isOffset;//是否进行位置偏移

    public LJBubbleMenu(Context context, String[] menus, View currentView, OnMenuItemClickListener listener) {
        this(context, menus, currentView, listener, true);
    }

    public LJBubbleMenu(Context context, String[] menus, View currentView, OnMenuItemClickListener listener, boolean isOffset) {
        super(context);
        if (menus != null) {
            this.menus = menus;
        }
        this.context = context;
        this.listener = listener;
        this.isOffset = isOffset;
        screenMetrice = context.getResources().getDisplayMetrics();
        width = this.menus.length * (int) (70 * screenMetrice.density);
        width = Math.min(width, screenMetrice.widthPixels);
        height = (int) (30 * context.getResources().getDisplayMetrics().density);
        this.currentView = currentView;
        initLocation();
        View view = initView();
        setContentView(view);
        setWidth(width);
        setHeight(height * 2);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.popupAnimation);
    }

    /**
     * 初始化布局
     *
     * @return
     */
    private View initView() {
        LinearLayout playout = new LinearLayout(context);
        //设置宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playout.setLayoutParams(params);
        //方向
        playout.setOrientation(LinearLayout.VERTICAL);
        //填充子view
        if (isTopBar) {
            playout.addView(initBarView());
            playout.addView(initMenuView());
        } else {
            playout.addView(initMenuView());
            playout.addView(initBarView());
        }
        return playout;
    }

    /**
     * 初始化菜单
     *
     * @return
     */
    private View initMenuView() {
        LinearLayout menuView = new LinearLayout(context);
        //设置宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        menuView.setLayoutParams(params);

        menuView.setBackgroundResource(R.drawable.view_bubble_menu_bg);
        //填充item
        LinearLayout.LayoutParams txParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        txParam.weight = 1;
        for (int i = 0; i < menus.length; i++) {
            TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            textView.setText(menus[i]);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(txParam);
            textView.setTag(i);
            textView.setOnClickListener(this);
            menuView.addView(textView);
        }
        //设置间隔,3.0以下版本，可以通过View来间隔，现在暂时不支持
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menuView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            menuView.setDividerDrawable(getDivider());
        }

        return menuView;
    }

    /**
     * 初始化箭头
     *
     * @return
     */
    private View initBarView() {
        ImageView barView = new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = offsetBarX;
        barView.setLayoutParams(lp);
        barView.setImageResource(isTopBar ? R.drawable.view_bubble_menu_bar_top : R.drawable.view_bubble_menu_bar_bottom);
        return barView;
    }

    /**
     * 生成间隔
     *
     * @return
     */
    private Drawable getDivider() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(1, -1);
        drawable.setColor(Color.parseColor("#efefef"));
        return drawable;
    }

    public void show() {
        this.showAtLocation(currentView, isTopBar ? Gravity.BOTTOM : Gravity.TOP, offsetX, offsetY);
    }

    /**
     * bar 的方向是否朝上
     *
     * @return
     */
    private boolean isTopBar(int y) {

        //顶部空间小于菜单的2倍高度，菜单在下，否则在上
        return y < 5 * height / 2;
    }

    /**
     * 计算弹出位置
     */
    private void initLocation() {
        int screenW = screenMetrice.widthPixels;
        int screenH = screenMetrice.heightPixels;

        int[] location = new int[2];
        currentView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        int x = location[0];
        int y = location[1];

        //弹框居中，不偏移
        if (!isOffset) {
            offsetBarX = width / 2;
            offsetX = 0;
            offsetY = y - height;
            isTopBar = false;
            return;
        }
        isTopBar = isTopBar(y);
        int viewWidth = currentView.getWidth();
        //计算y轴的偏移
        if (isTopBar) {
            offsetY = screenH - y - currentView.getHeight() - 5 * height / 2;
        } else {
            offsetY = y - 3 * height / 2;
        }

        //边界处理
        int tempOffset = (screenW - width) / 2;
        //太宽了就不偏移
        if (tempOffset > 20) {
            //理论偏移值
            offsetX = (x + viewWidth / 2) - screenW / 2;
            if (tempOffset < Math.abs(offsetX)) {
                offsetX = tempOffset * Math.abs(offsetX) / offsetX;
            }
        }

        //计算箭头的偏移
        //临时认为箭头宽度为16dp
        int barWidth = (int) (16 * context.getResources().getDisplayMetrics().density);
        offsetBarX = viewWidth / 2 + x - offsetX - screenW / 2 + width / 2 - barWidth / 2;
        //临界值处理
        if (offsetBarX > width - barWidth) {
            offsetBarX -= barWidth;
        }
        if (offsetBarX < barWidth) {
            offsetBarX += barWidth;
        }

    }

    @Override
    public void onClick(View v) {
        int position = Integer.valueOf(v.getTag().toString());
        if (listener != null)
            listener.onMenuItemClick(position, menus[position]);
        dismiss();
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int postion, String btnContent);
    }


}
