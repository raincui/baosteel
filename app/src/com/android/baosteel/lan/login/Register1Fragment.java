package com.android.baosteel.lan.login;


import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.util.AppUtil;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.baosight.lan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment with a Google +1 button.
 * Use the {@link Register1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register1Fragment extends BaseFragment {

    private RegisterActivity activity;

    private View rootView;
    private CheckBox box_agree;
    private View view_protocol;

    private String phoneNum;
    private TextView txt_code;
    private EditText edit_code;

    public Register1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Register1Fragment.
     */
    public static Register1Fragment newInstance() {
        Register1Fragment fragment = new Register1Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (RegisterActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register1, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initView() {
        super.initView();
        box_agree = findView(rootView,R.id.box_agree);
        txt_code = findView(rootView,R.id.txt_code);
        edit_code = findView(rootView,R.id.edit_code);
        view_protocol = findView(rootView,R.id.lly_protocol);
        view_protocol.setVisibility(activity.isFromRegister()?View.VISIBLE:View.GONE);
    }

    public boolean isAgreeProtocol(){
        return activity.isFromRegister()&&box_agree.isChecked();
    }

    public void checkPhoneCode(){
        String code = edit_code.getText().toString();
        if(TextUtils.isEmpty(code)){
            showToast("请输入验证码");
            return;
        }
        Map<String,Object> param = new HashMap<>();
        param.put("userPhone",phoneNum);
        param.put("validCode",code);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userCheckCode,param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if(getActivity().isFinishing())return;
                if (!isAdded()) return;
                if (flag) {
                  activity.onNext();
                }
            }
        });
    }

    public void onPhoneCode(){
        EditText edit_phone = findView(rootView,R.id.edit_phone);
        phoneNum = edit_phone.getText().toString();
        if(!AppUtil.isPhoneNum(phoneNum)){
            showToast("请输入正确手机号");
            return;
        }
        List<String> param = new ArrayList<>();
        param.add(phoneNum);
        NetApi.call(NetApi.getJsonParam(activity.isFromRegister()?ProtocolUrl.userRegisterCode:ProtocolUrl.userPasswordCode, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if(getActivity().isFinishing())return;
                if (!isAdded()) return;
                if (flag) {
                    txt_code.setClickable(false);
                    int count = 60;
                    txt_code.setText(count+"");
                    countdown(--count);
                }
            }
        });
    }

    private void countdown(final int count){
        new Thread(){
            @Override
            public void run() {
                super.run();
                mHandler.sendMessageDelayed(mHandler.obtainMessage(1000,count,0),1000);
            }
        }.start();
    }

    @Override
    protected void message(Message msg) {
        super.message(msg);
        if(msg.what == 1000){
            if(msg.arg1==-1){
                txt_code.setText("重新获取");
                txt_code.setClickable(true);
                return;
            }
            txt_code.setText(msg.arg1+"");
            countdown(--msg.arg1);
        }
    }

    public void putParam(Map<String,Object> param){
        param.put("userPhone",phoneNum);
        param.put("loginName",phoneNum);
        param.put("validCode",edit_code.getText().toString());
    }
}
