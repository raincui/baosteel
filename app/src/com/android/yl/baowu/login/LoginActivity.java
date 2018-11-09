package com.android.yl.baowu.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.yl.baowu.R;
import com.android.yl.baowu.basebusiness.business.BusinessCallback;
import com.android.yl.baowu.basebusiness.business.NetApi;
import com.android.yl.baowu.basebusiness.business.ProtocolUrl;
import com.android.yl.baowu.basebusiness.entity.UserInfo;
import com.android.yl.baowu.basebusiness.util.AppUtil;
import com.android.yl.baowu.basebusiness.util.MD5;
import com.android.yl.baowu.basebusiness.util.SaveDataGlobal;
import com.android.yl.baowu.baseui.MainActivity;
import com.android.yl.baowu.mine.MineNewsActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.android.yl.baowu.mine.MineNewsActivity.FRAGMENT_MINE_PHONE;
import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    public static String LOGINNAME = "loginName";
    public static String LOGINPWD = "loginPwd";

    UMShareAPI mShareAPI;
    private static int REQUEST_CODE_PHONE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        mShareAPI = UMShareAPI.get(this);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        String loginName = SaveDataGlobal.getString(LOGINNAME, "");
        String loginPwd = SaveDataGlobal.getString(LOGINPWD, "");
        if (!TextUtils.isEmpty(loginName)) {
            mEmailView.setText(loginName);
            mPasswordView.setText(loginPwd);
            attemptLogin();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            login(email, password);
        }
    }


    private void login(final String userName, final String password) {
        String passwordEcode = MD5.getMD5(password);
        Map<String, Object> param = new HashMap<>();
        param.put(LOGINNAME, userName);
        param.put(LOGINPWD, passwordEcode);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userLogin, param), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                showProgress(false);
                if (!flag) {
                    mPasswordView.requestFocus();
                    return;
                }
                try {
                    JSONObject jo = new JSONObject(json);
                    Type type = TypeToken.get(UserInfo.class).getType();
                    UserInfo info = new Gson().fromJson(jo.optString("data"), type);
                    SaveDataGlobal.setUserInfo(info);
                    SaveDataGlobal.putString(LOGINNAME, userName);
                    SaveDataGlobal.putString(LOGINPWD, password);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean isEmailValid(String email) {
        return AppUtil.isPhoneNum(email);
    }

    private boolean isPasswordValid(String password) {
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("title", "注册");
        intent.putExtra("isRegister", true);
        startActivity(intent);
    }

    public void onForgetPD(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("title", "忘记密码");
        startActivity(intent);
    }

    public void onWechat(View view) {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
    }

    public void onQQ(View view) {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, authListener);
    }

    public void onXina(View view) {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.SINA, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
            String uid = data.get("uid");
            String name = data.get("name");
            String accessToken = data.get("accessToken");
            int type = platform == SHARE_MEDIA.QQ ? 4 : (platform == SHARE_MEDIA.WEIXIN ? 2 : 3);
            auth(name, uid, accessToken, type);
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            Toast.makeText(mContext, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 三方登陆授权
     *
     * @param userName
     * @param openId
     * @param accessToken
     */
    private void auth(String userName, String openId, String accessToken, int type) {
        Map<String, Object> param = new HashMap<>();
        param.put("userName", userName);
        param.put("openId", openId);
        param.put("accessToken", accessToken);
        param.put("userType", type);
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.userAuth, param), new BusinessCallback(this) {
            @Override
            public void subCallback(boolean flag, String json) {
                if (isFinishing()) return;
                showProgress(false);
                if (!flag) {
                    mPasswordView.requestFocus();
                    return;
                }
                try {
                    JSONObject jo = new JSONObject(json);
                    Type type = TypeToken.get(UserInfo.class).getType();
                    UserInfo info = new Gson().fromJson(jo.optString("data"), type);
                    SaveDataGlobal.setUserInfo(info);
                    if (TextUtils.isEmpty(info.getUserPhone())) {
                        MineNewsActivity.start(LoginActivity.this, "绑定手机", FRAGMENT_MINE_PHONE, REQUEST_CODE_PHONE);
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHONE) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

}

