package com.android.baosteel.lan.baseui.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.android.baosteel.lan.basebusiness.util.BeanUtils;


/**
 * Created by czl on 2016/11/14.
 */

public class LJWebView extends WebView {

    ProgressBar proressbar;

    public LJWebView(Context context) {
        super(context);
        initProgress(context);
    }

    public LJWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgress(context);
    }

    private void initProgress(Context context) {
        proressbar = new ProgressBar(context);
        BeanUtils.setFieldValue(proressbar, "mOnlyIndeterminate", new Boolean(false));
        proressbar.setIndeterminate(false);
        proressbar.setIndeterminateDrawable(getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));
        proressbar.setBackgroundColor(Color.WHITE);
        ClipDrawable progressDrawable = new ClipDrawable(new ColorDrawable(Color.parseColor("#16D96D")), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        proressbar.setProgressDrawable(progressDrawable);
        proressbar.setProgress(0);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()), 0, 0);
        addView(proressbar, layoutParams);
    }

    public void setProgressBackGroundColor(int color) {
        if (proressbar != null) {
            proressbar.setBackgroundColor(color);
        }

    }

    public void setProgressColor(int color) {
        if (proressbar != null) {
            ClipDrawable progressDrawable = new ClipDrawable(new ColorDrawable(color), Gravity.LEFT, ClipDrawable.HORIZONTAL);
            proressbar.setProgressDrawable(progressDrawable);
        }
    }

    public void setProgress(int progress) {
        if (proressbar != null) {
            proressbar.setProgress(progress);
        }
    }

    public void showProgress() {
        if (proressbar != null) {
            proressbar.setVisibility(View.VISIBLE);
        }

    }

    public void dismissProgress() {
        if (proressbar != null) {
            proressbar.setVisibility(View.GONE);
        }
    }


}
