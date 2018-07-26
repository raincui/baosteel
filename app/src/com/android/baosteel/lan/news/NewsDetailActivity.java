package com.android.baosteel.lan.news;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.LabelInfo;
import com.android.baosteel.lan.basebusiness.entity.NewsInfo;
import com.android.baosteel.lan.basebusiness.entity.PicInfo;
import com.android.baosteel.lan.basebusiness.util.LogUtil;
import com.android.baosteel.lan.basebusiness.util.SharedPrefAction;
import com.android.baosteel.lan.baseui.customview.DotIconView;
import com.android.baosteel.lan.baseui.ui.BaseWebViewActivity;
import com.android.baosteel.lan.news.comment.CommentActivity;
import com.baosight.lan.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yulong.cui
 * @Title: DocLinkActivity
 * @Description: 资讯详情
 * Create DateTime: 2017/3/14
 */
public class NewsDetailActivity extends BaseWebViewActivity implements View.OnClickListener {

    private TextView txt_title;
    private TextView txt_time;
    private TextView txt_readcount;
    private LinearLayout lly_labels;
    private LinearLayout lly_labels1;
    private View btn_edit;
    private ImageView btn_love;
    private ImageView btn_more;
    private DotIconView btn_good;
    private DotIconView btn_talk;

    private View rly_edit;
    private View btn_edit_cancel;
    private View btn_edit_send;
    private EditText edit_input;


    private String docId;
    private NewsInfo mInfo;

    private int fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_detail);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        txt_title = findView(R.id.txt_title);
        txt_time = findView(R.id.txt_time);
        btn_edit = findView(R.id.btn_edit);
        btn_love = findView(R.id.btn_love);
        btn_more = findView(R.id.btn_menu);
        btn_good = findView(R.id.btn_good);
        btn_talk = findView(R.id.btn_talk);
        btn_talk.setIcon(R.drawable.dapinlun);
        txt_readcount = findView(R.id.txt_count);
        webView = findView(R.id.web);
        lly_labels = findView(R.id.lly_labels);
        lly_labels1 = findView(R.id.lly_labels1);
        initWebView(webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setProgressBackGroundColor(Color.TRANSPARENT);

        rly_edit = findView(R.id.rly_edit_talk);
        btn_edit_cancel = findView(R.id.btn_cancel_edit);
        btn_edit_send = findView(R.id.btn_commit_edit);
        edit_input = findView(R.id.edit_talk);
        boolean noMenu = getIntent().getBooleanExtra("noMenu", false);
        findViewById(R.id.rly_menu).setVisibility(noMenu ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btn_edit.setOnClickListener(this);
        btn_love.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        btn_good.setOnClickListener(this);
        btn_talk.setOnClickListener(this);
        btn_edit_cancel.setOnClickListener(this);
        btn_edit_send.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        //字体和亮度
        fontSize = SharedPrefAction.getInt("fontSize", 110);
        webView.getSettings().setTextZoom(fontSize);

        docId = getIntent().getStringExtra("docId");
        String url = ProtocolUrl.getNewsDetail + "/" + docId;
        NetApi.call(NetApi.getJsonParam(url), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        JSONArray ja = data.optJSONArray("data");
                        if (ja != null && ja.length() > 0) {
                            mInfo = new Gson().fromJson(ja.getJSONObject(0).toString(), NewsInfo.class);
                            adapterData();
                            return;
                        }
                    }
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    private void adapterData() {
        if (mInfo == null) return;
        txt_time.setText(mInfo.getPubDate());
        txt_title.setText(mInfo.getTitle());
        boolean isFromLearning = getIntent().getBooleanExtra("fromLearning",false);
        txt_readcount.setVisibility(isFromLearning?View.VISIBLE:View.GONE);
        txt_readcount.setText("阅读" + mInfo.getReadCount());
        String htmlStr = mInfo.getContent();
        htmlStr = htmlStr.replaceAll("http://www.baosteel.com/group/player/jwplayer.js", "file:///android_asset/jwplayer.js");
        String[] imageUrls = initImageUrls(htmlStr);
        htmlStr = getHtmlData(appendAttach(htmlStr));
        webView.loadDataWithBaseURL("file:///android_asset/jwplayer.js", htmlStr, "text/html", "utf-8", null);
        if (imageUrls != null)
            webView.addJavascriptInterface(new ImageJavascriptInterface(this, imageUrls), "imagelistener");

        btn_love.setImageResource(mInfo.isCollected() ? R.drawable.shoucangdianji : R.drawable.shoucang);
        btn_good.setIcon(mInfo.isGooded() ? R.drawable.yizan : R.drawable.zan);
        btn_good.setMessageCount(mInfo.getLoveCount());
        btn_talk.setMessageCount(mInfo.getCommentCount());
        adapterLabels(mInfo.getLabelList());

    }

    private String appendAttach(String htmlStr) {
        List<PicInfo> attachs = mInfo.getOtherList();
        if (attachs == null || attachs.isEmpty()) return htmlStr;
        StringBuilder sb = new StringBuilder();
        sb.append(htmlStr).append("<p><ul><p>附件</p>");
        /**<p>
         * <ul>
         * <p>附件</p>
         * <li><a  href = 'xxxxxxx'>链接1</a></li>
         * <li><a  href = 'xxxxxxx'>链接2</a></li>
         * <li><a  href = 'xxxxxxx'>链接3</a></li>
         * </ul>
         * </p>
         */
        String head = "$attach$";
        for (PicInfo info : attachs) {
            if (TextUtils.isEmpty(info.getAttachUrl())) continue;
            sb.append("<li><a href='").append(head).append(info.getAttachUrl()).append("&name=").append(info.getAttachName()).append("'>");
            sb.append(info.getAttachName()).append("</a></li>");
        }
        sb.append("</ul></p>");
        return sb.toString();
    }


    /**
     * 为html字符串拼接head
     *
     * @param bodyHTML
     * @return
     */
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}p{color:#666666;line-height:150%}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    /**
     * 初始化标签
     *
     * @param list
     */
    private void adapterLabels(List<LabelInfo> list) {
        if (list == null || list.isEmpty()) return;
        lly_labels.removeAllViews();
        lly_labels1.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.leftMargin = dpTopx(10);
        for (LabelInfo info : list) {
            TextView txt = new TextView(this);
            txt.setBackgroundResource(R.drawable.shape_stroke_blue);
            txt.setPadding(dpTopx(10), dpTopx(5), dpTopx(10), dpTopx(5));
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            txt.setTextColor(getResources().getColor(R.color.txtblue));
            txt.setText(info.getLabelname());
            txt.setTag(info);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LabelInfo info = (LabelInfo) v.getTag();
                    Intent intent = new Intent(NewsDetailActivity.this, LabelNewsActivity.class);
                    intent.putExtra("title", info.getLabelname());
                    intent.putExtra("count", "46512");
                    intent.putExtra("labelId", info.getLabelid());
                    startActivity(intent);
                }
            });
            txt.measure(0, 0);
            if (isOutBound(lly_labels, txt)) {
                lly_labels1.addView(txt, lp);
            } else {
                lly_labels.addView(txt, lp);
            }
        }
    }

    private boolean isOutBound(ViewGroup viewGroup, View view) {
        if (viewGroup.getChildCount() == 0) return false;
        viewGroup.measure(0, 0);
        int remainW = getResources().getDisplayMetrics().widthPixels - viewGroup.getMeasuredWidthAndState() - dpTopx(10);
        return remainW < view.getMeasuredWidth();
    }

    private int dpTopx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5);
    }

    @Override
    public void onClick(View v) {
        if (mInfo == null) return;
        int id = v.getId();
        if (id == R.id.btn_edit) {
            //写评论
            rly_edit.setVisibility(View.VISIBLE);
            edit_input.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(edit_input, 0);
        } else if (id == R.id.btn_good) {
            //点赞
            goGood();
        } else if (id == R.id.btn_love) {
            //收藏
            goLove();
        } else if (id == R.id.btn_talk) {
            //评论
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("docId", mInfo.getDocId());
            startActivityForResult(intent, 1001);
        } else if (id == R.id.btn_cancel_edit) {
            rly_edit.setVisibility(View.GONE);
        } else if (id == R.id.btn_commit_edit) {
            String talk = edit_input.getText().toString();
            if (TextUtils.isEmpty(talk)) {
                showToast("请输入评论");
                return;
            }
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(NewsDetailActivity.this.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            goTalk(talk);
        } else if (id == R.id.btn_menu) {
            Dialog dialog = new FontSettingDialog(this, fontSize, getLight(), new FontSettingDialog.onSettingListener() {
                @Override
                public void onSetting(int txtSize, int light) {
                    SharedPrefAction.putInt("fontSize", txtSize);
                    fontSize = txtSize;
                    setLight(light);
                    webView.getSettings().setTextZoom(txtSize);
                }

                @Override
                public void onSettingEnd(int light) {
                    SharedPrefAction.putInt("lightSize", light);
                }
            });
            dialog.show();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = getResources().getDisplayMetrics().widthPixels;
            dialog.getWindow().setAttributes(lp);
        }
    }

    private void setLight(int lightSize) {
        float brightValue = lightSize / 100.f;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (brightValue < 0 ? -1.0f : brightValue);
        getWindow().setAttributes(lp);
    }

    private int getLight() {
        int value = 0;
        ContentResolver cr = getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
            value = value * 100 / 255;
        } catch (Settings.SettingNotFoundException e) {

        }
        return value;
    }

    private void goTalk(String comment) {
        if (TextUtils.isEmpty(comment)) {
            showToast("请输入评论内容");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("docId", docId);
        map.put("remarkContent", comment);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.goComment, map), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        showToast("评论成功");
                        edit_input.getText().clear();
                        rly_edit.setVisibility(View.GONE);
                        btn_talk.addMsg();
                        return;
                    }
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    /**
     * 点赞
     */
    private void goGood() {
        mInfo.good(!mInfo.isGooded());
        btn_good.setIcon(mInfo.isGooded() ? R.drawable.yizan : R.drawable.zan);
        List<String> list = new ArrayList<>();
        list.add(docId);
        list.add(String.valueOf(mInfo.getIsLove()));
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.goGood, list), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        btn_good.addMsg(mInfo.isGooded() ? 1 : -1);
                        setResult(Activity.RESULT_OK);
                        return;
                    }
                    mInfo.good(!mInfo.isGooded());
                    btn_good.setIcon(mInfo.isGooded() ? R.drawable.yizan : R.drawable.zan);
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    /**
     * 收藏
     */
    public void goLove() {
        mInfo.collect(!mInfo.isCollected());
        btn_love.setImageResource(mInfo.isCollected() ? R.drawable.shoucangdianji : R.drawable.shoucang);
        List<String> list = new ArrayList<>();
        list.add(docId);
        list.add(String.valueOf(mInfo.getIsCollect()));
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.changeCollect, list), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) return;
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONObject data = jo.optJSONObject("data");
                    if ("success".equals(data.optString("result"))) {
                        setResult(Activity.RESULT_OK);
                        return;
                    }
                    mInfo.collect(!mInfo.isCollected());
                    btn_love.setImageResource(mInfo.isCollected() ? R.drawable.shoucangdianji : R.drawable.shoucang);
                    showToast(data.optJSONObject("data").optString("errorMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(R.string.tip_error);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            initData();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rly_edit.getVisibility() == View.VISIBLE) {
                onClick(btn_edit_cancel);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 从网页中检索图片资源
     *
     * @param htmlCode
     * @return
     */
    public static String[] initImageUrls(String htmlCode) {
        List<String> imageSrcList = new ArrayList<>();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }
        if (imageSrcList == null || imageSrcList.size() == 0) {
            return null;
        }
        return imageSrcList.toArray(new String[imageSrcList.size()]);
    }
}
