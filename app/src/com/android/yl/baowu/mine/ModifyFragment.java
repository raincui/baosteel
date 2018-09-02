package com.android.yl.baowu.mine;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.UserInfo;
import com.android.yl.baowu.basebusiness.util.Base64Util;
import com.android.yl.baowu.basebusiness.util.BitmapAction;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.R;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyulong on 2018/8/21.
 */

public class ModifyFragment extends TakePhotoFragment {
    private View viewMain;
    private ImageView img_head;


    private CropOptions cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
    private File picFile;

    public static ModifyFragment newInstance() {
        ModifyFragment fragment = new ModifyFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_modify, null);
        else {
            ViewGroup parent = (ViewGroup) viewMain.getParent();
            if (parent != null) parent.removeView(viewMain);
        }
        return viewMain;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picFile = new File(getActivity().getFilesDir().getAbsolutePath(), "baosteel");
        if (!picFile.exists()) picFile.mkdir();
        initView();
        initListener();
    }

    protected void initView() {
        img_head = (ImageView) viewMain.findViewById(R.id.img_head);
    }

    protected void initListener() {
        View view = viewMain.findViewById(R.id.rly_take);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new ModifyPicDialog(getContext(), new ModifyPicDialog.onMenuListener() {
                    @Override
                    public void onTake() {
                        File file = new File(picFile, "image.jpg");
                        if (!file.exists()) try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getTakePhoto().onPickFromCaptureWithCrop(Uri.fromFile(file), cropOptions);
                    }

                    @Override
                    public void onSelect() {
                        File file = new File(picFile, "image.jpg");
                        if (file.exists())
                            file.delete();
                        getTakePhoto().onPickFromGalleryWithCrop(Uri.fromFile(file), cropOptions);
                    }
                });
                dialog.show();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setAttributes(lp);
            }
        });
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String path = result.getImage().getOriginalPath();
//        img_head.setImageURI(Uri.parse(path));

        Bitmap bitmap = BitmapAction.getBitmapFromFile(path);
        final String picStr = bitmap2StrByBase64(bitmap);
        Map<String, Object> param = new HashMap<>();
        param.put("userPic", picStr);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userSetting, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (getActivity().isFinishing()) return;
                if (!isAdded()) return;
                if (!flag) {
                    return;
                }
                Toast.makeText(getContext(), "头像已修改", Toast.LENGTH_SHORT).show();
                UserInfo info = SaveDataGlobal.getUserInfo();
                info.setUserPic(picStr);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64Util.encodeToString(bytes, Base64Util.DEFAULT);
    }


}
