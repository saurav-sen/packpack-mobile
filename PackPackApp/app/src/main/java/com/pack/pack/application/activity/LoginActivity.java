package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.pack.pack.application.AppController.SIGNUP_ACTIVITY_REQUEST_CODE;
import static com.pack.pack.application.AppController.RESET_PASSWD_ACTIVITY_REQUEST_CODE;

import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 *
 * @author Saurav
 *
 */
public class LoginActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private EditText input_email;
    private EditText input_password;
    private AppCompatButton btn_login;
    private AppCompatButton btn_signup;
    private TextView link_forgot_passwd;

    private ProgressDialog progressDialog;

    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean existingUser = false;

        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        link_forgot_passwd = (TextView) findViewById(R.id.link_forgot_passwd);

        /*if(existingUser) {
            boolean loginStatus = getIntent().getBooleanExtra("loginStatus", false);
            if(loginStatus) {
                input_email.setVisibility(View.INVISIBLE);
                input_password.setVisibility(View.INVISIBLE);
            } else {
                input_email.setVisibility(View.VISIBLE);
                input_password.setVisibility(View.VISIBLE);
            }
            btn_signup.setVisibility(View.INVISIBLE);
            btn_login.setVisibility(View.INVISIBLE);
            link_forgot_passwd.setVisibility(View.INVISIBLE);
        } else {*/
            input_email.setVisibility(View.VISIBLE);
            input_password.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_signup.setVisibility(View.VISIBLE);
            link_forgot_passwd.setVisibility(View.VISIBLE);
        //}


        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("passwd");
        if(email != null) {
            input_email.setText(email);
        }
        if(password != null) {
            input_password.setText(password);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo userInfo = new UserInfo(input_email.getText().toString(), input_password.getText().toString());
                doLogin(userInfo);
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignup();
            }
        });
        link_forgot_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doResetPassword();
            }
        });
    }

    private void doResetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivityForResult(intent, RESET_PASSWD_ACTIVITY_REQUEST_CODE);
    }

    private void showProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    private void doLogin(UserInfo userInfo) {
        new LoginTask(this, this).execute(userInfo);
    }

    @Override
    public void onPreStart() {
        showProgressDialog();
    }

    @Override
    public void onSuccess(Object data) {
        LoggedInUserInfo userInfo = (LoggedInUserInfo)data;
        AccessToken token = userInfo.getAccessToken();
        JUser user = userInfo.getUser();
        getIntent().putExtra("loginStatus", true);
        finish();
        startMainActivity();
    }

    @Override
    public void onPostComplete() {

    }

    public void onFailure(String errorMsg) {
        hideProgressDialog();
        getIntent().putExtra("email", input_email.getText().toString());
        getIntent().putExtra("passwd", input_password.getText().toString());
        getIntent().putExtra("loginStatus", false);
        finish();
        startActivity(getIntent());
        btn_login.setEnabled(true);
        btn_signup.setEnabled(true);
    }

    private void doSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, SIGNUP_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == SIGNUP_ACTIVITY_REQUEST_CODE) {
                finish();
                startMainActivity();
            } else if (requestCode == RESET_PASSWD_ACTIVITY_REQUEST_CODE){
                btn_login.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}