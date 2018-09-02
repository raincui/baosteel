package com.android.yl.baowu.baseui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.yl.baowu.basebusiness.util.AppUtil;
import com.android.yl.baowu.baseui.customview.LJWebView;


/**
 */
public abstract class BaseWebViewFragment extends BaseFragment {

    protected LJWebView mWebView;
    private int color;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        this.color = Color.parseColor("#16D96D");
    }

    //    @Override
//    public void onPause() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            if (mWebView != null) {
//                mWebView.onPause();
//            }
//        }
//        super.onPause();
//    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebView(String url, final int color) {
        this.color = color;
        if (mWebView != null) {
            AppUtil.disableHardwareRenderInLayer(mWebView);
            mWebView.requestFocus();
            WebSettings webSettings = mWebView.getSettings();
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
            //获得数据库操作权限
            webSettings.setDatabaseEnabled(true);
            String dir = getActivity().getApplicationContext().getDir("database", Activity.MODE_PRIVATE).getPath();
            webSettings.setDatabasePath(dir);
            // 使用localStorage则必须打开
            webSettings.setDomStorageEnabled(true);
            webSettings.setGeolocationEnabled(true);
//            if (Build.VERSION.SDK_INT >= 21) {
//                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            }
            //阻止被系统浏览器接管发起的web请求
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (mWebView != null) {
                        mWebView.showProgress();
                        mWebView.setProgressColor(color);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mWebView != null) {
                        mWebView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mWebView != null) {
                                    mWebView.dismissProgress();
                                }

                            }
                        }, 1000);
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    onPageLoadError();
                }
            });
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                    // 构建一个Builder来显示网页中的对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    mWebView.setProgress(newProgress);
                    super.onProgressChanged(view, newProgress);
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

            mWebView.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                            mWebView.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });

            mWebView.loadUrl(url);

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Error: WebView.destroy() called while still attached
        if (mWebView != null) {
            ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mWebView);
            }
            mWebView.removeAllViews();
            //3.0以上手机,提前结束上下文环境，缩放按钮来不及消失crash问题
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.setVisibility(View.GONE);
            long timeout = ViewConfiguration.getZoomControlsTimeout();
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mWebView.destroy();
                        mWebView = null;
                    } catch (Exception ex) {
                    }
                }
            }, timeout);
        }
    }

    /**
     * 无网络或错误处理 回调
     */
    public void onPageLoadError() {

    }
}
