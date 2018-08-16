package com.android.baosteel.lan.basebusiness.http;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Map;


/**
 * Created by Administrator on 2015/8/1.
 */
public class OKHttpClientUtil {

    private static Context context;

    private static boolean isHandle401;

    public static void init(Context context, boolean isHandle401) {
        OKHttpClientUtil.context = context;

        OKHttpClientUtil.isHandle401 = isHandle401;
    }


    /**
     * 异步post埋点请求
     *
     * @param url
     * @param tag
     * @param okHttpAsyncCallback
     * @param nameValuePairs
     */
    public static void postEnQueueGzip(String url, final int tag, final OkHttpAsyncCallback okHttpAsyncCallback, Map<String, Object> nameValuePairs) {
        RequestBuilder.newBuilder().url(url).tag(tag).param(formatParams(nameValuePairs)).desc("异步post埋点请求").httpCallback(new HttpCallback() {
            @Override
            public void onData(int tag, boolean isSuccess, int httpCode, String data) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onData(tag, data);
                }
            }

            @Override
            public void onFail(int tag, StatusCode statusCode, HttpException e) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onFailed(tag, e);
                }
            }
        }).build().postEnQueueGzip();
    }


    /**
     * OKHttp post方法
     *
     * @param url
     * @param nameValuePairs
     * @return
     * @throws Exception
     */
    public static String postHttpClientRequest(String url, Map<String, Object> nameValuePairs) throws Exception {
        String data = RequestBuilder.newBuilder().url(url).param(formatParams(nameValuePairs)).desc("请求链接").build().post();
//        handle401(data);
        return data;
    }


    /**
     * 异步post
     *
     * @param url
     * @param tag
     * @param okHttpAsyncCallback
     * @param nameValuePairs
     */
    public static void postEnQueue(String url, final int tag, final OkHttpAsyncCallback okHttpAsyncCallback, Map<String, Object> nameValuePairs,Map<String, String> heads) {
        RequestBuilder.newBuilder().desc("请求链接").url(url).param(formatParams(nameValuePairs)).tag(tag).header(heads).httpCallback(new HttpCallback() {
            @Override
            public void onData(int tag, boolean isSuccess, int httpCode, String data) {
//                handle401(data);
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onData(tag, data);
                }
            }

            @Override
            public void onFail(int tag, StatusCode statusCode, HttpException e) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onFailed(tag, e);
                }
            }
        }).build().postEnqueue();


    }

    /**
     * 同步get
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    public static String getHttpClientRequest(String url, Map<String, Object> nameValuePairs) {
        String data = RequestBuilder.newBuilder().url(url).param(formatParams(nameValuePairs)).desc("请求连接").build().get();
//        handle401(data);
        return data;
    }


    /**
     * 异步get
     *
     * @param url
     * @param tag
     * @param okHttpAsyncCallback
     * @param nameValuePairs
     */
    public static void getEnQueue(String url, final int tag, final OkHttpAsyncCallback okHttpAsyncCallback, Map<String, Object> nameValuePairs,Map<String, String> heads) {
        RequestBuilder.newBuilder().url(url).tag(tag).param(formatParams(nameValuePairs)).header(heads).desc("请求连接").httpCallback(new HttpCallback() {
            @Override
            public void onData(int tag, boolean isSuccess, int httpCode, String data) {
//                handle401(data);
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onData(tag, data);
                }
            }

            @Override
            public void onFail(int tag, StatusCode statusCode, HttpException e) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onFailed(tag, e);
                }
            }
        }).build().getEnqueue();

    }


    /**
     * 同步下载文件
     *
     * @param url
     * @param destPath  目标路径
     * @param overWrite 是否覆盖
     * @throws IOException
     */
    public static void getFile(String url, String destPath, boolean overWrite) throws IOException {
        RequestBuilder.newBuilder().url(url).fileDestPath(overWrite, destPath).desc("下载文件").build().downloadFile();
    }



    public static void postUserCertificateEnQueue(final int tag, String url, String[] files,
                                                  final OkHttpAsyncCallback okHttpAsyncCallback) {
        RequestBuilder.newBuilder().url(url).tag(tag).files(files).uploadProgressCallback(new UploadCallback() {
            @Override
            public void onProgress(int tag, long currentBytes, long totalBytes) {
                Log.e("14500",""+currentBytes+"---: "+totalBytes);
            }

            @Override
            public void onData(int tag, boolean isSuccess, int httpCode, String data) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onData(httpCode, data);
                }
            }

            @Override
            public void onFail(int tag, HttpException e) {
                if (okHttpAsyncCallback != null) {
                    okHttpAsyncCallback.onFailed(tag, e);
                }
            }
        }).build().uploadFiles();
    }


    /**
     * 对请求参数进行URL编码
     *
     * @param _params
     * @return
     */
    private static Map<String, Object> formatParams(Map<String, Object> _params) {
        Map<String, Object> tempParams = _params;
        return tempParams;
    }

    /**
     * 异步get
     * 只给活动详情接口,H5调用 这个方法用的(Banner打点，无需基础打点字段以及指纹)
     *
     * @param url
     * @param nameValuePairs
     */
    public static void getActivitiesRequestEqueue(String url, Map<String, Object> nameValuePairs) {
        RequestBuilder.newBuilder().url(url).param(nameValuePairs).desc("请求连接").build().getEnqueue();
    }

    public static void handle401(String data) {
        if (!isHandle401) {
            return;
        }
//        try {
//            JSONObject json = JsonUtil.convertJsonObj(data);
//            if (json != null) {
//                String code1 = json.optString("code");
//                String msg = json.optString("msg");
//                String result = json.optString("result");
//                if ("401".equals(code1) || ("error".equals(result) && msg != null && msg.contains("请登录"))) {
//                    Intent intent = new Intent("android.intent.action.driver.start.login");
//                    context.sendBroadcast(intent);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
