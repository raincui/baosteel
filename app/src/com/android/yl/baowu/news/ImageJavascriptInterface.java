package com.android.yl.baowu.news;

import android.content.Context;
import android.content.Intent;

public class ImageJavascriptInterface {
    private Context context;
    private String[] imageUrls;

    public ImageJavascriptInterface(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent();
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("curImageUrl", img);
        intent.setClass(context, PhotoBrowserActivity.class);
        context.startActivity(intent);
    }
}