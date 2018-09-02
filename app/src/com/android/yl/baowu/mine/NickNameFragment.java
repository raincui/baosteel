package com.android.yl.baowu.mine;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.UserInfo;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment with a Google +1 button.
 * Use the {@link NickNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NickNameFragment extends BaseFragment {

    private View rootView;

    private EditText edit_nick;

    public NickNameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Register1Fragment.
     */
    public static NickNameFragment newInstance() {
        NickNameFragment fragment = new NickNameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nick_name, container, false);
        initView();
        initListener();
        return rootView;
    }

    @Override
    protected void initListener() {
        super.initListener();
        View btn_commit = findView(rootView, R.id.btn_commit);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nickName = edit_nick.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    showToast("请输入昵称");
                    return;
                }
                Map<String, Object> param = new HashMap<>();
                param.put("userName", nickName);
                NetApi.call(NetApi.getJsonParam(ProtocolUrl.userSetting, param), new BusinessCallback(getContext()) {
                    @Override
                    public void subCallback(boolean flag, String json) {
                        if (getActivity().isFinishing()) return;
                        if (!isAdded()) return;
                        if (!flag) {
                            return;
                        }
                        showToast("昵称已修改");
                        UserInfo info = SaveDataGlobal.getUserInfo();
                        info.setUserName(nickName);
                        getActivity().finish();
                    }
                });

            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        edit_nick = findView(rootView, R.id.edit_nick);
    }


}
