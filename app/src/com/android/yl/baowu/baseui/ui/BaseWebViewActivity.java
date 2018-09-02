package com.android.yl.baowu.baseui.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.yl.baowu.basebusiness.util.AppUtil;
import com.android.yl.baowu.basebusiness.util.LogUtil;
import com.android.yl.baowu.baseui.DocLinkActivity;
import com.android.yl.baowu.baseui.customview.LJWebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 */
public class BaseWebViewActivity extends BaseActivity {

    protected LJWebView webView;
    private int color;

    protected List<String> picList = new ArrayList<>();//图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.color = Color.parseColor("#00000000");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (webView != null) {
                webView.onPause();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (webView != null) {
                webView.onResume();
            }
        }
    }

    public void setWebView(String url, int webViewResourceId) {
        webView = (LJWebView) findViewById(webViewResourceId);
        initWebView(url);
    }

    public void setWebView(String url, int webViewResourceId, int color) {
        this.webView = (LJWebView) this.findViewById(webViewResourceId);
        this.color = color;
        this.initWebView(url);
    }

    public void initWebView(final LJWebView webView) {
        {
            if (webView != null) {
                AppUtil.disableHardwareRenderInLayer(webView);
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
                webView.requestFocus();
                WebSettings webSettings = webView.getSettings();
                webSettings.setDefaultTextEncodingName("UTF-8");
                //允许JS操作
                webSettings.setJavaScriptEnabled(true);
                //支持通过js打开新的窗口
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                //支持H5缓存策略
                webSettings.setAppCacheEnabled(true);
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                //支持访问本地文件
                webSettings.setAllowFileAccess(true);
                //允许缩放
                webSettings.setBuiltInZoomControls(true);
                webSettings.setSupportZoom(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                //获得数据库操作权限
                webSettings.setDatabaseEnabled(true);
                String dir = getApplicationContext().getDir("database", Activity.MODE_PRIVATE).getPath();
                webSettings.setDatabasePath(dir);
                // 使用localStorage则必须打开
                webSettings.setDomStorageEnabled(true);
                webSettings.setGeolocationEnabled(true);


                //阻止被系统浏览器接管发起的web请求
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (TextUtils.isEmpty(url)) return true;
                        if (url.equalsIgnoreCase("http://www.baosteel.com/group/player/jwplayer.js")) {
                            url = "file:///android_asset/jwplayer.js";
                            webView.loadUrl(url);
                        } else if (url.contains("$attach$")) {
                            Intent intent = new Intent(BaseWebViewActivity.this, DocLinkActivity.class);
                            intent.putExtra("isAttachment", true);
                            intent.putExtra("docLink", url.substring(url.indexOf("$attach$")+8));
                            startActivity(intent);
                        }else{
                            webView.loadUrl(url);
                            LogUtil.e("----: "+url);
                        }
                        return true;
                    }

                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                        WebResourceResponse response = null;
                        if (url.equalsIgnoreCase("file:///android_asset/jwplayer.js")) {
                            try {
                                InputStream localCopy = getAssets().open("jwplayer.js");
                                response = new WebResourceResponse("text/javascript", "UTF-8", localCopy);
                                return response;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return super.shouldInterceptRequest(view, url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        addImageClickListener(view);
                        if (webView != null) {
                            webView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (webView != null) {
                                        webView.dismissProgress();
                                    }

                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (webView != null) {
                            webView.showProgress();
                            webView.setProgressColor(color);
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        onPageLoadError();
                    }
                });
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                        // 构建一个Builder来显示网页中的对话框
                        AlertDialog.Builder builder = new AlertDialog.Builder(BaseWebViewActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage(message);
                        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                        builder.setCancelable(false);
                        builder.create();
                        builder.show();
                        return true;
                    }

                    @Override
                    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BaseWebViewActivity.this);
                        builder.setTitle("confirm");
                        builder.setMessage(message);
                        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });
                        builder.setCancelable(false);
                        builder.create();
                        builder.show();
                        return true;
                    }

                    @Override
                    // 设置网页加载的进度条
                    public void onProgressChanged(WebView view, int newProgress) {
                        webView.setProgress(newProgress);
                        super.onProgressChanged(view, newProgress);
                    }

                    // 设置应用程序的标题title
                    public void onReceivedTitle(WebView view, String title) {
                        BaseWebViewActivity.this.setTitle(title);
                        super.onReceivedTitle(view, title);
                    }

                    @Override
                    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long
                            estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
                        quotaUpdater.updateQuota(estimatedSize * 2);
                    }

                    @Override
                    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                        callback.invoke(origin, true, false);
                        super.onGeolocationPermissionsShowPrompt(origin, callback);
                    }

                    @Override
                    public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater
                            quotaUpdater) {
                        quotaUpdater.updateQuota(spaceNeeded * 2);
                    }
                });

                webView.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                        }
                        return false;
                    }
                });

            }

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String url) {
        initWebView(webView);
        if (webView != null) {
            webView.loadUrl(url);

        }

    }

    public void goBack(View view) {
        if (webView!=null&&webView.canGoBack()) {
            webView.goBack();
        } else onBack(null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Error: WebView.destroy() called while still attached
        if (webView != null) {
            ViewGroup viewGroup = (ViewGroup) webView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(webView);
            }
            webView.clearCache(true);
            webView.stopLoading();
            webView.removeAllViews();
            //3.0以上手机,提前结束上下文环境，缩放按钮来不及消失crash问题
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setVisibility(View.GONE);
            try {
                webView.destroy();
            } catch (Exception e) {
            }
            webView = null;

        }
    }

    // 调用方法
    protected void doMethon(final String methon) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.loadUrl("javascript:" + methon);
                } catch (Exception e) {
                }
            }
        }, 1000);
    }

    /**
     * 调用方法
     *
     * @param methon 方法名及参数
     */
    protected void doMethonNow(final String methon) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.loadUrl("javascript:" + methon);
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 无网络或错误处理 回调
     */
    public void onPageLoadError() {

    }

    /**
     * 设置webView里图片点击监听
     * @param webView
     */
    private void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++) " +
                "{"
                + " objs[i].onclick=function() " +
                " { "
                + "  window.imagelistener.openImage(this.src); " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                " } " +
                "}" +
                "})()");
    }
}
