package com.android.baosteel.lan.basebusiness.http;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @Title: OkRequestHelper
 */
public class OkRequestHelper {
    private final static String TAG = OkRequestHelper.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static X509TrustManager trustAllManager;

    //----------------------- okHttpClient ---------------------------
    private static OkHttpClient client = null;

    private static OkHttpClient uploadClient = null;
    /**
     * 2G 超时60秒
     * 3G 超时40秒
     * 4G&WIFI 超时20秒
     */
    private static int TimeOutUnit = 60;
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.tag() instanceof OkRequestHelper && originalRequest.tag() != null && ((OkRequestHelper)
                    originalRequest.tag()).saveCache) {
                // 暂时关闭cache
                // OkRequestHelper requestHelper = (OkRequestHelper) originalRequest.tag();
                // Response response1 = OKCacheHelper.readCache(chain.request());
                // if (response1 != null) {
                //    if (((OkRequestHelper) originalRequest.tag()).cacheCallback != null) {
                //        ((OkRequestHelper) originalRequest.tag()).cacheCallback.onCacheData(requestHelper.tag, response1.body
                // ().string());
                //    }
                // }
            }
            Response originalResponse = chain.proceed(chain.request());
            if (originalRequest.tag() instanceof OkRequestHelper && originalRequest.tag() != null && ((OkRequestHelper)
                    originalRequest.tag()).saveCache) {
                // 暂时关闭cache
                // originalResponse = OKCacheHelper.saveCache(originalResponse, chain.request(), true);
            } else {
                // 暂时关闭cache
                // originalResponse = OKCacheHelper.saveCache(originalResponse, chain.request(), false);
            }
            if (DEBUG) {
                Log.e("OkRequestHelper", originalResponse.body().string());
            }
            return originalResponse;
        }
    };


    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert
                .CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert
                .CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }

    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static final Interceptor LoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            if (mNetState == NetState.NET_NO) {
                throw new ConnectException("ENETUNREACH");
            }

            Request request = chain.request();

            long t1 = System.nanoTime();
            if (request.tag() instanceof OkRequestHelper && request.tag() != null) {
                OkRequestHelper requestHelper = (OkRequestHelper) request.tag();
                if (DEBUG) {
                    Log.e(TAG, requestHelper.desc + ":------>" + requestHelper.getUrl() + "?" + requestHelper.getFormateParams());
                }
            }


            Response response = null;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }


            long t2 = System.nanoTime();
            if (DEBUG) {
                Log.e(TAG, "请求耗时:------>" + (((int) (t2 - t1)) / 1000000) + "ms");
            }
            return response;
        }
    };
    //----------------------- 网络状态 -------------------------------

    /**
     * 枚举网络状态
     * NET_NO：没有网络
     * NET_2G:2g网络
     * NET_3G：3g网络
     * NET_4G：4g网络
     * NET_WIFI：wifi
     * NET_UNKNOWN：未知网络
     */
    public enum NetState {
        NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

    private static ConnectionChangeReceiver myReceiver;
    private static NetState mNetState = NetState.NET_4G;
    //--------------------------  基本配置 ----------------------------------
    private static final String CHARSET_NAME = "UTF-8";
    public static final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
    //----------------------- 请求描述 打日志 -------------------------------
    private String desc = "";
    //----------------------- post&get 基础请求参数 --------------------
    private Map<String, String> params = new HashMap<String, String>();
    private String host = "";
    private String path = "";
    private String url = "";
    private int tag;
    private HttpCallback httpCallback;
    // 下载文件的回调
    private DownloadCallback downloadCallback = null;
    // 上传文件的回调
    private UploadCallback uploadCallback = null;
    private Call call;
    //------------------------ 日志设置 -------------------------
    private boolean needLog = false;
    //------------------------ 缓存设置 -------------------------
    private boolean saveCache = false;
    //------------------------ 下载文件 -------------------------
    //文件下载路径
    private String destPath;
    //文件是否覆盖 默认不覆盖
    private boolean overWrite = false;

    //----------------------- 上传文件 ---------------------------
    private List<String> files = new ArrayList<String>();

    //----------------------- 请求头 ------------------------------
    private Map<String, String> header = new HashMap<String, String>();
    //----------------------- 默认的请求头 post&get ------------------------
    private Map<String, String> defaultHeader = new HashMap<String, String>() {
        {
            put("charset", CHARSET_NAME);
            put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        }
    };

    //----------------------- 初始化 应用启动时调用 --------------------------------------
    public static void initRequestHelper(Application application) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        application.registerReceiver(myReceiver, filter);

        resetConfig();
    }


    //------------------------ 外部调用 --------------------------


    /**
     * 取消网络请求
     */
    public void cancel() {
        call.cancel();
    }

    public void url(String url) {
        if (url != null) this.url = url;
    }


    public String getDesc() {
        return desc;
    }

    public void hostAndPath(String host, String path) {
        if (host != null) {
            this.host = host;
        }
        if (path != null) {
            this.path = path;
        }
    }

    /**
     * 设置请求的参数
     *
     * @param params
     */
    public void setParams(Map<String, String> params) {
        if (params != null) {
            this.params = params;
        }
    }

    /**
     * 设置网络请求的描述信息，用于打印log
     *
     * @param desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 设置请求的标识
     *
     * @param tag
     */
    public void tag(int tag) {
        this.tag = tag;
    }

    /**
     * 设置请求的header ，基础的header不再需要添加
     * 这里用于设置带Cookie的请求
     */
    public void setRequestHeader(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                this.header.put(h.getKey(), h.getValue());
            }
        }
    }

    /**
     * 下载文件时设置文件的保存地址
     */
    public void setFileDestPath(boolean overWrite, String destPath) {
        this.overWrite = overWrite;
        if (destPath != null) {
            this.destPath = destPath;
        }
    }

    /**
     * 请求回调
     *
     * @param httpCallback
     */
    public void setHttpCallback(HttpCallback httpCallback) {
        this.httpCallback = httpCallback;
    }


    /**
     * 上传进度的回调
     */
    public void setUploadProgressCallback(UploadCallback downloadCallback) {
        this.uploadCallback = downloadCallback;
    }

    /**
     * 下载进度的回调
     */
    public void setDownloadProgressCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }


    /**
     * 发起 get请求 异步
     */
    public void getEnqueue() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "网络请求";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, StatusCode.ERROR_PARAM, new HttpException(StatusCode.ERROR_PARAM, "HttpGet must set " +
                        "url!"));
            }
            return;
        }
        if (!urlBuilder.toString().contains("?")) {
            urlBuilder.append("?");
        }
        if (!urlBuilder.toString().endsWith("?") && !urlBuilder.toString().endsWith("&")) {
            urlBuilder.append("&");
        }
        urlBuilder.append(getFormateParams());
        String urlStr = urlBuilder.toString();
        Request.Builder builder = new Request.Builder().url(urlStr);
        for (Map.Entry<String, String> entry : defaultHeader.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        //ok3 的回调
        Callback okCallback = new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                //处理异步请求的错误
                if (httpCallback != null) {
                    httpCallback.onFail(tag, parseStatusCode(e), new HttpException(parseStatusCode(e), e.getMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (httpCallback != null) {
                    httpCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(response.body().string()));
                }
            }
        };
        //请求发出
        enqueue(builder, okCallback, client);
    }

    /**
     * 推荐使用异步请求  public void getEnqueue()throws Exception
     * 异常时，返回HttpException的JsonString
     *
     * @return
     */
    @Deprecated
    public String get() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "网络请求";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, StatusCode.ERROR_PARAM, new HttpException("HttpGet must set url!"));
            }
            return "HttpGet must set url!";
        }
        if (!urlBuilder.toString().contains("?")) {
            urlBuilder.append("?");
        }
        if (!urlBuilder.toString().endsWith("?") && !urlBuilder.toString().endsWith("&")) {
            urlBuilder.append("&");
        }
        urlBuilder.append(getFormateParams());
        String urlStr = urlBuilder.toString();
        Request.Builder builder = new Request.Builder().url(urlStr);
        for (Map.Entry<String, String> entry : defaultHeader.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        String ret = "";
        try {
            Response response = execute(builder);
            if (response.isSuccessful()) {
                ret = response.body().string();
                if (httpCallback != null) {
                    httpCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(ret));
                }
                return replaceJson(ret);
            } else {
                if (httpCallback != null) {
                    StatusCode stateCode = StatusCode.OK;
                    if (response.code() > 500) {
                        stateCode = StatusCode.ERROR_SERVER;
                    } else if (response.code() > 400) {
                        stateCode = StatusCode.ERROR_URL;
                    }
                    httpCallback.onFail(tag, stateCode, new HttpException(response.code(), ret));
                }
                return new HttpException(response.code(), ret).toJsonString();
            }
        } catch (Exception e) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, parseStatusCode(e), new HttpException(parseStatusCode(e), ret));
            }
            return new HttpException(parseStatusCode(e), e.getMessage()).toJsonString();
        }
    }

    /**
     * 发起post请求 异步
     *
     * @throws Exception
     */
    public void postEnqueue() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "post网络请求";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, StatusCode.ERROR_PARAM, new HttpException(StatusCode.ERROR_PARAM, "HttpPost must set " +
                        "url!"));
            }
            return;
        }
        String urlStr = urlBuilder.toString();
        Request.Builder builder = new Request.Builder().url(urlStr);
        for (Map.Entry<String, String> entry : defaultHeader.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        builder.post(RequestBody.create(FORM_URLENCODED, getFormateParams()));
        Callback okCallback = new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                if (httpCallback != null) {
                    httpCallback.onFail(tag, parseStatusCode(e), new HttpException(parseStatusCode(e), e.getMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //数据回调
                if (httpCallback != null) {
                    httpCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(response.body().string()));
                }
            }
        };
        enqueue(builder, okCallback, client);
    }

    /**
     * 推荐使用异步请求 public void postEnqueue() throws Exception
     * 异常时，返回HttpException的JsonString
     *
     * @throws Exception
     */
    @Deprecated
    public String post() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "post同步网络请求";
        }
        String ret = "";
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, StatusCode.ERROR_PARAM, new HttpException(StatusCode.ERROR_PARAM, "HttpPost must set " +
                        "url!"));
            }
            return "HttpPost must set url!";
        }

        String urlStr = urlBuilder.toString();
        Request.Builder builder = new Request.Builder().url(urlStr);
        for (Map.Entry<String, String> entry : defaultHeader.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        builder.post(RequestBody.create(FORM_URLENCODED, getFormateParams()));
        try {
            Response response = execute(builder);
            if (response.isSuccessful()) {
                ret = response.body().string();
                if (httpCallback != null) {
                    httpCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(ret));
                }
                return replaceJson(ret);
            } else {
                if (httpCallback != null) {
                    StatusCode stateCode = StatusCode.OK;
                    if (response.code() > 500) {
                        stateCode = StatusCode.ERROR_SERVER;
                    } else if (response.code() > 400) {
                        stateCode = StatusCode.ERROR_URL;
                    }
                    httpCallback.onFail(tag, stateCode, new HttpException(response.code(), ret));
                }
                return new HttpException(response.code(), ret).toJsonString();
            }
        } catch (Exception e) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, parseStatusCode(e), new HttpException(parseStatusCode(e), e.getMessage()));
            }
            return new HttpException(parseStatusCode(e), e.getMessage()).toJsonString();
        }
    }

    /**
     * postEnQueueGzip
     */
    public void postEnQueueGzip() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "post上传压缩文件网络请求";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (httpCallback != null) {
                httpCallback.onFail(tag, StatusCode.ERROR_PARAM, new HttpException("PostEnQueueGzip must set url!"));
            }
            return;
        }
        String urlStr = urlBuilder.toString();
        Request.Builder builder = new Request.Builder().url(urlStr);
        for (Map.Entry<String, String> entry : defaultHeader.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        builder.addHeader("Content-Encoding", "gzip");
        String formatParams = formatParamsCommon();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        byte[] data = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(formatParams.getBytes("UTF-8"));
            gzip.flush();
            gzip.finish();
            gzip.close();
            data = out.toByteArray();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        builder.post(RequestBody.create(FORM_URLENCODED, data));
        //ok3 的回调
        Callback okCallback = new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                if (httpCallback != null) {
                    httpCallback.onFail(tag, parseStatusCode(e), new HttpException(parseStatusCode(e), e.getMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //数据回调
                if (httpCallback != null) {
                    httpCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(response.body().string()));
                }
            }
        };
        enqueue(builder, okCallback, client);
    }


    /**
     * 带进度的下载文件
     */
    public void downloadFile() {
        if (TextUtils.isEmpty(desc)) {
            desc = "下载文件";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (downloadCallback != null) {
                downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PARAM, "GetFileProgress url can't be null!"));
            }
            return;
        }
        if (!urlBuilder.toString().contains("?")) {
            urlBuilder.append("?");
        }
        if (!urlBuilder.toString().endsWith("?") && !urlBuilder.toString().endsWith("&")) {
            urlBuilder.append("&");
        }
        urlBuilder.append(getFormateParams());
        String urlStr = urlBuilder.toString();
        if (TextUtils.isEmpty(destPath)) {
            if (downloadCallback != null) {
                downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PARAM, "GetFileProgress: destPath can't be " +
                        "null!"));
            }
            return;
        }
        final File destFile = new File(destPath);
        if (!destFile.getParentFile().exists()) {
            boolean ret = destFile.getParentFile().mkdirs();
            if (!ret && downloadCallback != null) {
                downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PERMISSION_DENIED, "GetFileProgress: destPath " +
                        "permission denied!"));
            }
            return;
        }
        if (destFile.exists()) {
            if (overWrite) {
                destFile.delete();
            } else {
                if (downloadCallback != null) {
                    downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PARAM, "GetFileProgress: file already " +
                            "exist!"));
                }
                return;
            }
        }
        Request.Builder builder = new Request.Builder().url(urlStr);
        OkHttpClient.Builder builder1 = client.newBuilder();
        builder1.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder().body(new OKProgressHelper.ProgressResponseBody(tag, originalResponse.body
                        (), downloadCallback)).build();
            }
        });
        Callback okCallback = new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                if (downloadCallback != null) {
                    downloadCallback.onFail(tag, new HttpException(parseStatusCode(e), e.getMessage()));
                }
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] buffer = new byte[1024 * 8];
                    try {
                        boolean result = destFile.createNewFile();
                        if (!result) {
                            if (downloadCallback != null) {
                                downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PERMISSION_DENIED,
                                        "GetFileProgress destPath permission denied!"));
                            }
                            return;
                        }
                    } catch (Exception e) {
                        if (downloadCallback != null) {
                            downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PERMISSION_DENIED, "GetFileProgress" +
                                    " destPath permission denied!"));
                        }
                        return;
                    }

                    try {
                        BufferedInputStream reader = new BufferedInputStream(response.body().byteStream());
                        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(destFile));
                        int n = 0;
                        while ((n = reader.read(buffer, 0, buffer.length)) != -1) {
                            writer.write(buffer, 0, n);
                        }
                        writer.flush();
                        if (downloadCallback != null) {
                            downloadCallback.onSucess(tag, destFile);
                        }
                    } catch (Exception e) {
                        if (downloadCallback != null) {
                            downloadCallback.onFail(tag, new HttpException(parseStatusCode(e), e.getMessage()));
                        }
                    }
                } else {
                    if (downloadCallback != null) {
                        downloadCallback.onFail(tag, new HttpException(response.code(), response.body().string()));
                    }
                }
            }
        };
        Request request = builder.tag(this).build();
        call = builder1.build().newCall(request);
        call.enqueue(okCallback);
    }


    /**
     * 设置要上传的文件绝对路径
     *
     * @param files 由每个文件的绝对路径组成
     */
    public void setFiles(String[] files) {
        if (files != null) {
            this.files.addAll(Arrays.asList(files));
        }
    }

    /**
     * 带进度的上传文件
     */
    public void uploadFiles() {
        needLog = true;
        if (TextUtils.isEmpty(desc)) {
            desc = "上传文件";
        }
        StringBuilder urlBuilder = new StringBuilder(getUrl());
        if (urlBuilder.length() == 0) {
            if (uploadCallback != null) {
                uploadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PARAM, "uploadFileProgress url can't be null!"));
            }
            return;
        }
        String urlStr = urlBuilder.toString();
        if (this.files == null) {
            if (downloadCallback != null) {
                downloadCallback.onFail(tag, new HttpException(StatusCode.ERROR_PARAM, "uploadFileProgress files can't be " +
                        "null!"));
            }
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody
                        .create(null, entry.getValue()));
            }
        }
        String fileStr = "";
        for (String file : files) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + file + "\"; filename=\"" + file + "\""),
                    RequestBody.create(MediaType.parse("application/octet-stream"), new File(file)));
            fileStr += file;
        }
        Request.Builder requestBuilder = new Request.Builder().url(urlStr).post(buildRequestBody(tag, builder.build(),
                uploadCallback));
        final Callback okCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("networklib---upload", e == null ? "upload" : e.toString());
                if (uploadCallback != null) {
                    uploadCallback.onFail(tag, new HttpException(parseStatusCode(e), e.getMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (uploadCallback != null) {
                    uploadCallback.onData(tag, 200 == response.code(), response.code(), replaceJson(response.body().string()));
                }
            }
        };
        enqueue(requestBuilder, okCallback, uploadClient);
    }

    /**
     * 服务端返回的数据可能不符合json格式，需要客户端做统一处理
     *
     * @param data
     * @return
     */
    private String replaceJson(String data) {
        String json = data;
        if (json != null && json.endsWith(";")) {
            json = json.substring(0, json.length() - 1);
        }
        if (json != null && data.startsWith("null")) {
            json = json.replaceFirst("null", "");
        }
        if (json != null && data.startsWith("callback")) {
            json = json.replaceFirst("callback", "");
        }
        if (json != null && data.startsWith("jsonp")) {
            json = json.replaceFirst("jsonp", "");
        }
        if (json != null && json.startsWith("(") && json.endsWith(")")) {
            // 去除服务端返回“（ + json + ）”类型的非法数据
            json = json.replaceFirst("\\(", "");
            json = json.substring(0, json.length() - 1);
        }
        return json;
    }


    /**
     * 异步请求
     *
     * @param callback
     */
    private void enqueue(Request.Builder reqBuilder, Callback callback, OkHttpClient client) {
        Request request = reqBuilder.tag(this).build();
        call = client.newCall(request);
        call.enqueue(callback);
    }


    private Response execute(Request.Builder reqBuilder) throws IOException {
        Request request = reqBuilder.tag(this).build();
        call = client.newCall(request);
        return call.execute();
    }


    //----------------------- 私有调用 --------------------------

    private static void resetConfig() {
        trustAllManager = new TrustAllManager();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //信任所有HTTPS官方验证
//        builder.sslSocketFactory(createSSLSocketFactory(),trustAllManager);
//        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        builder.connectTimeout(TimeOutUnit, TimeUnit.SECONDS);
        builder.readTimeout(TimeOutUnit, TimeUnit.SECONDS);
        builder.writeTimeout(TimeOutUnit, TimeUnit.SECONDS);
        // 暂时屏蔽cookie缓存功能，后期app需要cookie缓存时，再加回来（目前重复调用时发生系列化存储问题 java.io.InvalidClassException）
        // builder.cookieJar(CookieStore.getInstance());
        builder.retryOnConnectionFailure(true);
//        builder.interceptors().add(LoggingInterceptor);
//        builder.interceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
//        builder.networkInterceptors().add(retry_interceptor);
        client = builder.build();
        if (uploadClient == null) {
            uploadClient = client.newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.MINUTES)
                    .writeTimeout(60, TimeUnit.MINUTES).build();
        }
    }

    /**
     * 网络状态变化时修改配置
     *
     * @param newState
     */
    private static void changeNetState(NetState newState) {
        if (newState != mNetState) {
            mNetState = newState;
            switch (mNetState) {

                case NET_NO:
                    break;
                case NET_2G:
                    TimeOutUnit = 60;
                    resetConfig();
                    break;
                case NET_3G:
                    TimeOutUnit = 40;
                    resetConfig();
                    break;
                case NET_4G:
                    TimeOutUnit = 20;
                    resetConfig();
                    break;
                case NET_WIFI:
                    TimeOutUnit = 20;
                    resetConfig();
                    break;
                case NET_UNKNOWN:
                    break;
            }
        }

    }


    /**
     * 从exception 获取返回状态
     *
     * @param e
     * @return
     */
    private StatusCode parseStatusCode(Exception e) {
        if (e instanceof ConnectException) {
            return StatusCode.ERROR_CONNECTION;
        } else if (e instanceof BindException) {
            return StatusCode.ERROR_BIND;
        } else if (e instanceof SocketTimeoutException) {
            return StatusCode.ERROR_SOCKET_TIME_OUT;
        } else {
            return StatusCode.ERROR_OTHER;
        }
    }

//    /**
//     * 构建网络错误信息
//     *
//     * @param e
//     * @return
//     */
//    private String buildHttpErrMsg(Exception e) {
//        JSONObject jsonObject = new JSONObject();
//        JsonUtil.putJson(jsonObject, "result", false);
//        JsonUtil.putJson(jsonObject, "count", "");
//        JsonUtil.putJson(jsonObject, "data", "");
//        if (e instanceof ConnectException) {
//            if (e.getMessage().contains("ENETUNREACH")) {
//                JsonUtil.putJson(jsonObject, "msg", "请检查您的网络");
//            } else {
//                JsonUtil.putJson(jsonObject, "msg", "连接失败");
//            }
//        } else if (e instanceof BindException) {
//            JsonUtil.putJson(jsonObject, "msg", "请求失败");
//        } else if (e instanceof IOException) {
//            try {
//                try {
//                    Log.e("e.getMessage", e.getMessage() == null ? "xx1" : e.getMessage());
//                    int code = Integer.parseInt(e.getMessage().substring(e.getMessage().lastIndexOf(" ") + 1));
//                    if (code >= 500) {
//                        JsonUtil.putJson(jsonObject, "msg", "服务器错误");
//                    } else if (code > 400) {
//                        JsonUtil.putJson(jsonObject, "msg", "请求地址错误");
//                    } else {
//                        JsonUtil.putJson(jsonObject, "msg", "请求失败");
//                    }
//                } catch (NumberFormatException ee) {
//                    if (e.getMessage().contains("Canceled")) {
//                        JsonUtil.putJson(jsonObject, "msg", "请求已取消");
//                    } else {
//                        JsonUtil.putJson(jsonObject, "msg", "请求失败");
//                    }
//                }
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                JsonUtil.putJson(jsonObject, "msg", "请求失败");
//            }
//
//
//        } else {
//            JsonUtil.putJson(jsonObject, "msg", "请求失败");
//        }
//        return jsonObject.toString();
//    }


    private String formatParamsCommon() {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue()));
            }
        }
        return sb.toString();
    }

    /**
     * 构造url
     *
     * @return
     */
    private String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (stringBuilder.length() == 0) {
            stringBuilder.append(host);
            stringBuilder.append(path);
        }
        return stringBuilder.toString();
    }

    /**
     * 构造请求参数 urlEncode
     *
     * @return
     */
    private String getFormateParams() {
        StringBuilder sb = new StringBuilder("");
        if (params != null) {
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(URLEncoder.encode(String.valueOf(entry.getKey()), CHARSET_NAME) + "=" + URLEncoder.encode
                            (!TextUtils.isEmpty(entry.getValue()) ? entry.getValue() : "", CHARSET_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void printLog(String data) {
        if (needLog) {
            Log.e("OkRequestHelper", getDesc() + ":------>" + data);
        }
    }


    //---------------------- 监听网络状态变化 ---------------------
    public static class ConnectionChangeReceiver extends BroadcastReceiver {
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            NetState stateCode = NetState.NET_NO;
            if (info == null || !info.isAvailable()) {
                //改变背景或者 处理网络的全局变量 网络不可用

            } else {

                switch (info.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        stateCode = NetState.NET_WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        switch (info.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                            case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                            case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                stateCode = NetState.NET_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                stateCode = NetState.NET_3G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                stateCode = NetState.NET_4G;
                                break;
                            default:
                                stateCode = NetState.NET_UNKNOWN;
                        }
                        break;
                    default:
                        stateCode = NetState.NET_UNKNOWN;
                }
            }
            changeNetState(stateCode);
        }
    }

    private static RequestBody buildRequestBody(int tag, RequestBody requestBody, UploadCallback progressListener) {
        return new OKProgressHelper.ProgressRequestBody(tag, requestBody, progressListener);
    }

}