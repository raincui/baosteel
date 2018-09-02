package com.android.yl.baowu.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.DocLinkActivity;
import com.android.yl.baowu.baseui.ui.BaseActivity;
import com.android.yl.baowu.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    Register1Fragment register1Fragment;
    Register2Fragment register2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initTitle();
        initView();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        TextView txt_title = findView(R.id.tv_title);
        txt_title.setText(getIntent().getStringExtra("title"));
    }

    @Override
    protected void initView() {
        super.initView();
        register1Fragment = Register1Fragment.newInstance(isFromRegister(), false);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, register1Fragment).commitAllowingStateLoss();
    }

    @Override
    public void onNext(Map<String, Object> param) {
        getSupportFragmentManager().beginTransaction().hide(register1Fragment).commitAllowingStateLoss();
        if (register2Fragment == null) {
            register2Fragment = Register2Fragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, register2Fragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(register2Fragment).commitAllowingStateLoss();

        }
    }

    public void onCommit(View view) {
        if (!register2Fragment.isValidPassword()) return;
        Map<String, Object> param = new HashMap<>();
        register1Fragment.putParam(param);
        register2Fragment.putParam(param);
        NetApi.call(NetApi.getJsonParam(isFromRegister() ? ProtocolUrl.userRegister : ProtocolUrl.userPasswordReset, param), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (flag) {
                    showToast(isFromRegister() ? "注册成功" : "密码已重置");
                    SaveDataGlobal.remove("loginName");
                    SaveDataGlobal.remove("loginPwd");
                    finish();
                }
            }
        });
    }

    public void onProtocol(View view) {
        Intent intent = new Intent(this, DocLinkActivity.class);
        intent.putExtra("docLink", "file:///android_asset/protocol.html");
        startActivity(intent);
    }

    public boolean isFromRegister() {
        return getIntent().getBooleanExtra("isRegister", false);
    }
}
