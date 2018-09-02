package com.android.yl.baowu.basebusiness.http;



import java.util.Map;

/**
 * 网络请求入口
 *
 * @author ruisong.li 2016/6/13
 * @Title: RequestBuilder
 * Create DateTime: 2016/6/13
 */
public class RequestBuilder {

    private OkRequestHelper requestHelper = null;

    private RequestBuilder() {
        requestHelper = new OkRequestHelper();
    }

    public static RequestBuilder newBuilder() {
        return new RequestBuilder();
    }

    /**
     * 设置请求的url
     *
     * @param url
     * @return
     */
    public RequestBuilder url(String url) {
        requestHelper.url(url);
        return this;
    }

    /**
     * 设置请求的url
     *
     * @param host
     * @param path
     * @return
     */
    public RequestBuilder hostAndPath(String host, String path) {
        requestHelper.hostAndPath(host, path);
        return this;
    }

    /**
     * 设置请求参数,post为请求体，get为查询参数
     *
     * @param param
     * @return
     */
    public RequestBuilder param(Map<String, Object> param) {
        requestHelper.setParams(param);
        return this;
    }


    /**
     * 请求的描述，打日志用
     *
     * @param desc
     * @return
     */
    public RequestBuilder desc(String desc) {
        requestHelper.setDesc(desc);
        return this;
    }

    /**
     * 设置请求的头
     *
     * @return
     */
    public RequestBuilder header(Map<String, String> headers) {
        requestHelper.setRequestHeader(headers);
        return this;
    }

    /**
     * 设置下载文件的目标路径
     *
     * @param overWrite    是否覆盖原有文件
     * @param fileDestPath
     * @return
     */
    public RequestBuilder fileDestPath(boolean overWrite, String fileDestPath) {
        requestHelper.setFileDestPath(overWrite, fileDestPath);
        return this;
    }

    /**
     * 设置需要上传的文件路径
     *
     * @param files
     * @return
     */
    public RequestBuilder files(String[] files) {
        requestHelper.setFiles(files);
        return this;
    }

    /**
     * 上传下载时的进度回调
     *
     * @return
     */
    public RequestBuilder uploadProgressCallback(UploadCallback callback) {
        requestHelper.setUploadProgressCallback(callback);
        return this;
    }

    /**
     * 下载时的进度回调
     *
     * @return
     */
    public RequestBuilder downloadProgressCallback(DownloadCallback callback) {
        requestHelper.setDownloadProgressCallback(callback);
        return this;
    }

    /**
     * 普通网络请求的回调（非上传或下载）
     *
     * @param callback
     * @return
     */
    public RequestBuilder httpCallback(HttpCallback callback) {
        requestHelper.setHttpCallback(callback);
        return this;
    }

    /**
     * 设置回调标识
     *
     * @return
     */
    public RequestBuilder tag(int tag) {
        requestHelper.tag(tag);
        return this;
    }


    public OkRequestHelper build() {
        return requestHelper;
    }
}
