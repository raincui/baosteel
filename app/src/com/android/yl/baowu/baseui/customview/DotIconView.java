package com.android.yl.baowu.baseui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.yl.baowu.R;

public class DotIconView extends RelativeLayout {

    private int msgCount;
    private TextView txt_dot;
    private ImageView img_icon;

    public DotIconView(Context context) {
        this(context, null);
    }

    public DotIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_dot_icon, this, true);
        txt_dot = (TextView) rl.findViewById(R.id.txt_dot);
        img_icon = (ImageView) rl.findViewById(R.id.img_icon);
    }

    public void setMessageCount(int count) {
        msgCount = count;
        if (count == 0) {
            txt_dot.setVisibility(View.GONE);
        } else {
            txt_dot.setVisibility(View.VISIBLE);
                txt_dot.setText(count + "");
        }
    }

    public void setIcon(int iconRes) {
        img_icon.setImageResource(iconRes);
    }

    public void addMsg() {
        addMsg(1);
    }
    public void addMsg(int count) {
        setMessageCount(msgCount + count);
    }
}