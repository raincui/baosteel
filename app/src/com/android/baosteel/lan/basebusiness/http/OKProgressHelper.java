package com.android.baosteel.lan.basebusiness.http;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * @author cai.pengrui on 2016/5/2.
 * @Title: OKProgressHelper
 * Create DateTime: 2016/5/2<br>
 */
public class OKProgressHelper {


    public static class ProgressResponseBody extends ResponseBody {
        //实际的待包装响应体
        private final ResponseBody responseBody;
        //下载进度回调接口
        private final DownloadCallback downloadCallback;
        private final int tag;
        //包装完成的BufferedSource
        private BufferedSource bufferedSource;

        /**
         * 构造函数，赋值
         *
         * @param tag              请求标记
         * @param responseBody     待包装的响应体
         * @param progressListener 回调接口
         */
        public ProgressResponseBody(int tag, ResponseBody responseBody, DownloadCallback progressListener) {
            this.tag = tag;
            this.responseBody = responseBody;
            this.downloadCallback = progressListener;
        }


        /**
         * 重写调用实际的响应体的contentType
         *
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         *
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        /**
         * 重写进行包装source
         *
         * @return BufferedSource
         * @throws IOException 异常
         */
        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                //包装
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        /**
         * 读取，回调进度接口
         *
         * @param source Source
         * @return Source
         */
        private Source source(Source source) {

            return new ForwardingSource(source) {
                //当前读取字节数
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    //回调，如果contentLength()不知道长度，会返回-1
                    if (downloadCallback != null) {
                        downloadCallback.onProgress(tag, totalBytesRead, responseBody.contentLength());
                    }
                    return bytesRead;
                }
            };
        }
    }

    public static class ProgressRequestBody extends RequestBody {
        //实际的待包装请求体
        private final RequestBody requestBody;
        private final UploadCallback uploadCallback;
        private final int tag;
        private BufferedSink bufferedSink;

        public ProgressRequestBody(int tag, RequestBody requestBody, UploadCallback progressListener) {
            this.tag = tag;
            this.requestBody = requestBody;
            this.uploadCallback = progressListener;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();

        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * 写入，回调进度接口
         *
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    if (uploadCallback != null) {
                        uploadCallback.onProgress(tag, bytesWritten, contentLength);
                    }
                }
            };
        }
    }


    public interface ProgressCallback {
        /**
         * 进度更新
         *
         * @param currentBytes  传输进度
         * @param contentLength 总长
         * @param done          是否完成
         */
        void onProgress(long currentBytes, long contentLength, boolean done);

        /**
         * 返回数据
         *
         * @param data
         */
        void onResponse(int tag, StatusCode code, String data);

    }
}
