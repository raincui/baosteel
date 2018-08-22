package com.android.baosteel.lan.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.UserInfo;
import com.android.baosteel.lan.basebusiness.util.AppUtil;
import com.android.baosteel.lan.basebusiness.util.MD5;
import com.android.baosteel.lan.basebusiness.util.SaveDataGlobal;
import com.android.baosteel.lan.baseui.MainActivity;
import com.android.baosteel.lan.baseui.ui.BaseActivity;
import com.baosight.lan.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

    }

    public void onQQ(View view) {

    }

    public void onXina(View view) {

    }


}

