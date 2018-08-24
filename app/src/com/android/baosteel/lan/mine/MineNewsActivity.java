package com.android.baosteel.lan.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.UserInfo;
import com.android.baosteel.lan.basebusiness.util.SaveDataGlobal;
import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.android.baosteel.lan.login.LoginActivity;
import com.android.baosteel.lan.login.Register1Fragment;
import com.android.baosteel.lan.login.RegisterActivity;
import com.android.baosteel.lan.news.comment.LookFragment;
import com.android.baosteel.lan.news.comment.MyAnswerFragment;
import com.android.baosteel.lan.news.comment.MyCollectFragment;
import com.android.baosteel.lan.news.comment.MyCommentFragment;
import com.baosight.lan.R;

import java.util.Map;

/**
 * @author yulong.cui
 *         Create DateTime: 2017/3/6
 */
public class MineNewsActivity extends BaseActivity {
    private TextView txt_title;
    public final static int FRAGMENT_MINE_COLLECT = 1000;
    public final static int FRAGMENT_MINE_TALK = 1001;
    public final static int FRAGMENT_MINE_ANSWER = 1002;
    public final static int FRAGMENT_MINE_LOOK = 1003;
    public final static int FRAGMENT_MINE_SETTING = 1004;
    public final static int FRAGMENT_MINE_ABOUT = 1005;
    public final static int FRAGMENT_MINE_FONT = 1006;
    public final static int FRAGMENT_MINE_MODIFY = 1007;
    public final static int FRAGMENT_MINE_PHONE = 1008;
    public final static int FRAGMENT_MINE_NICK = 1009;

    @Override
    protected void initTitle() {
        super.initTitle();
        txt_title = findView(R.id.tv_title);
        String title = getIntent().getStringExtra("title");
        txt_title.setText(title);
    }

    public static void start(Context context, String title, int fragment) {
        Intent intent = new Intent(context, MineNewsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("fragment", fragment);
        context.startActivity(intent);
    }

    public static void start(Fragment context, String title, int fragment, int resultId) {
        Intent intent = new Intent(context.getContext(), MineNewsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("fragment", fragment);
        context.startActivityForResult(intent, resultId);
    }

    public static void start(Activity context, String title, int fragment, int resultId) {
        Intent intent = new Intent(context, MineNewsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("fragment", fragment);
        context.startActivityForResult(intent, resultId);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
    }

    @Override
    protected void initData() {
        super.initData();
        int fragmentId = getIntent().getIntExtra("fragment", FRAGMENT_MINE_COLLECT);
        Fragment fragment;
        switch (fragmentId) {
            case FRAGMENT_MINE_ANSWER:
                fragment = new MyAnswerFragment();
                break;
            case FRAGMENT_MINE_COLLECT:
                fragment = new MyCollectFragment();
                break;
            case FRAGMENT_MINE_TALK:
                fragment = new MyCommentFragment();
                break;
            case FRAGMENT_MINE_LOOK:
                fragment = new LookFragment();
                break;
            case FRAGMENT_MINE_SETTING:
                fragment = new SettingFragment();
                break;
            case FRAGMENT_MINE_ABOUT:
                fragment = new AboutFragment();
                break;
            case FRAGMENT_MINE_FONT:
                fragment = new FontFragment();
                break;
            case FRAGMENT_MINE_MODIFY:
                fragment = ModifyFragment.newInstance();
                setResult(Activity.RESULT_OK);
                break;
            case FRAGMENT_MINE_PHONE:
                fragment = Register1Fragment.newInstance(false, true);
                break;
            case FRAGMENT_MINE_NICK:
                fragment = NickNameFragment.newInstance();
                break;
            default:
                fragment = new MyCollectFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commitAllowingStateLoss();

    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_common);
        initView();
        initListener();
        initData();
    }

    public void onAbout(View view) {
        start(this, "关于我们", FRAGMENT_MINE_ABOUT);
    }

    public void onFontSize(View view) {
        start(this, "字体大小", FRAGMENT_MINE_FONT);
    }

    public void onClearCache(View view) {
        showToast("缓存已清理");
    }

    public void onPhone(View view) {
        MineNewsActivity.start(this, "修改手机", MineNewsActivity.FRAGMENT_MINE_PHONE);
    }

    public void onPassword(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("title", "修改密码");
        startActivity(intent);
    }

    public void onNickName(View view) {
        start(this, "修改昵称", FRAGMENT_MINE_NICK);
    }

    public void onLogout(View view) {
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userLogout), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) {
                    return;
                }
                showToast("已退出登陆");
                SaveDataGlobal.remove(LoginActivity.LOGINNAME);
                SaveDataGlobal.remove(LoginActivity.LOGINPWD);
                SaveDataGlobal.setUserInfo(null);
                setResult(Activity.RESULT_OK);
                finish();

            }
        });
    }

    @Override
    public void onNext(Map<String, Object> param) {
        super.onNext(param);
        final String phone = param.get("userPhone").toString();
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userPhoneUpdate, param), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                if (!flag) {
                    return;
                }
                showToast("绑定成功");
                UserInfo info = SaveDataGlobal.getUserInfo();
                info.setUserPhone(phone);
                info.setLoginName(phone);
                SaveDataGlobal.putString(LoginActivity.LOGINNAME, phone);
                finish();
            }
        });
    }
}
