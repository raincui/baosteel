package com.android.yl.baowu.mine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.android.yl.baowu.R;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class ModifyPicDialog extends Dialog {

    private onMenuListener listener;

    public ModifyPicDialog(Context context, onMenuListener listener) {
        super(context, R.style.LJAlertDialogStyle);
        this.listener = listener;
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_modify_pic);
        findViewById(R.id.btn_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onTake();
                cancel();
            }
        });
        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onSelect();
                cancel();
            }
        });


    }


    public interface onMenuListener {
        void onTake();

        void onSelect();
    }


}
