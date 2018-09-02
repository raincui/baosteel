package com.android.yl.baowu.mine;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.yl.baowu.basebusiness.entity.UserInfo;
import com.android.yl.baowu.basebusiness.util.Base64Util;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.login.LoginActivity;
import com.android.yl.baowu.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {

    private View rootView;
    private SimpleDraweeView img_head;

    public MineFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MineFragment.
     */
    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
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

        rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)initView();
    }

    @Override
    protected void initView() {
        super.initView();
        if(getActivity().isFinishing())return;
        img_head = findView(rootView,R.id.img_head);
        UserInfo userInfo = SaveDataGlobal.getUserInfo();
        if(userInfo == null){
            showToast("请先登陆");
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }
        if(!TextUtils.isEmpty(userInfo.getUserPic())){
            img_head.setImageURI(bitmap2uri(getContext(),base642Bitmap(userInfo.getUserPic())));
        }

        TextView txt_name = findView(rootView,R.id.txt_name);
        txt_name.setText(userInfo.getUserName());

        TextView txt_phone = findView(rootView,R.id.txt_phone);
        txt_phone.setText(getString(R.string.mine_phone,userInfo.getUserPhone()));
    }

    @Override
    public void refresh() {
        super.refresh();
        initView();
    }

    public Bitmap base642Bitmap(String base64) {
        byte[] bytes = Base64Util.decode(base64, Base64Util.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
    }

    public static Uri bitmap2uri(Context c, Bitmap b) {
        File path = new File(c.getCacheDir() + File.separator + System.currentTimeMillis() + ".jpg");
        try {
            OutputStream os = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return Uri.fromFile(path);
        } catch (Exception ignored) {
        }
        return null;
    }
}
