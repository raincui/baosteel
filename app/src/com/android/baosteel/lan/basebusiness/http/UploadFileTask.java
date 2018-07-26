package com.android.baosteel.lan.basebusiness.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.baosteel.lan.basebusiness.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFileTask extends AsyncTask<String, String, String> {
    public boolean isget = false;
    private String end = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private Handler handler = null;
    private String path;
    private String fileName;
    private int imgType;

    public UploadFileTask(Handler handler, String fileName, int imgType) {
        this.handler = handler;
        this.fileName = fileName;
        this.imgType = imgType;
    }


    @Override
    protected String doInBackground(String... params) {
        int res = 0;
        String jsonStr = null;
        // 判读参数是否满足需求
        if (params != null && params.length > 1) {
            String imgurl = params[0];
            String photoPath = params[1];
            path = photoPath;

            File photoFile = new File(photoPath);
            try {
                URL url = new URL(imgurl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                /* 允许Input、Output，不使用Cache */
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                /* 设置传送的method=POST */
                con.setRequestMethod("POST");
                /* setRequestProperty */
                con.setRequestProperty("Charset", "UTF-8");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                /* 设置DataOutputStream */
                DataOutputStream ds = new DataOutputStream(con.getOutputStream());
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition:form-data; " + "name=\""+fileName+"\";filename=\"" + photoFile.getName() + "\""
                        + end);
                ds.writeBytes(end);
                /* 取得文件的FileInputStream */
                FileInputStream fStream = new FileInputStream(photoPath);
                /* 设置每次写入1024bytes */
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                /* 从文件读取数据至缓冲区 */
                while ((length = fStream.read(buffer)) != -1) {
                    /* 将资料写入DataOutputStream中 */
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                /* close streams */
                fStream.close();
                ds.flush();

                res = con.getResponseCode();

                if (res == 200) {
                    InputStream in = con.getInputStream();
                    // int ch;
                    // while ((ch = in.read()) != -1) {
                    // b.append((char) ch);
                    // }
                    // in.close();
                    jsonStr = new String(readInputStream(in), "UTF-8");
                }
            } catch (Exception e) {
                LogUtil.e("ssy",e.getMessage());
                uploadError();
            }

            if (res == 200) {
                uploadSuccess(jsonStr,imgType);
            } else {
                uploadError();
            }
        } else {
            uploadError();
        }
        return "";
    }

    // 上传失败
    private void uploadError() {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("info", "error");
        message.setData(bundle);
        handler.sendMessage(message);
    }

    // 上传成功
    private void uploadSuccess(String json, int imgType) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("info", "success");
        bundle.putString("rsp", json);
        bundle.putInt("imgType",imgType);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        String error = values[0];
    }

    private Bitmap resize_img(Bitmap bitmap, float pc) {
        Matrix matrix = new Matrix();
        matrix.postScale(pc, pc);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        System.gc();
        return resizeBmp;
    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
        // ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();// 网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }

}
