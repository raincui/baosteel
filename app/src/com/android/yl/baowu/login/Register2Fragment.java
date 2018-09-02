package com.android.yl.baowu.login;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.yl.baowu.basebusiness.util.AppUtil;
import com.android.yl.baowu.basebusiness.util.MD5;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.R;

import java.util.Map;

/**
 * A fragment with a Google +1 button.
 * Use the {@link Register2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register2Fragment extends BaseFragment {

    private View rootView;

    private EditText edit_password1;
    private EditText edit_password2;

    public Register2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Register1Fragment.
     */
    public static Register2Fragment newInstance() {
        Register2Fragment fragment = new Register2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register2, container, false);
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
        edit_password1 = findView(rootView,R.id.edit_password1);
        edit_password2 = findView(rootView,R.id.edit_password2);
    }

   public boolean isValidPassword(){
       String password1 = edit_password1.getText().toString();
       String password2 = edit_password2.getText().toString();

       if(!AppUtil.isPasswd(password1)){
           showToast("密码只能是6-12位数字或字母");
           return false;
       }

       if(!password1.equals(password2)){
           showToast("两次输入的密码不一致");
           return false;
       }
       return true;
   }

   public void putParam(Map<String,Object> param){
       String password = edit_password2.getText().toString();
       param.put("loginPwd", MD5.getMD5(password));
       param.put("newLoginPwd", MD5.getMD5(password));
   }


}
