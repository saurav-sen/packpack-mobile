package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.pack.pack.application.AppController.SIGNUP_ACTIVITY_REQUEST_CODE;
import static com.pack.pack.application.AppController.RESET_PASSWD_ACTIVITY_REQUEST_CODE;
import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_KEY;
import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_SECRET;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.oauth1.client.AccessToken;

/**
 *
 * @author Saurav
 *
 */
public class LoginActivity extends AppCompatActivity {

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

        String oAuthToken = AppController.getInstance().getoAuthToken();
        if(oAuthToken != null) {
            finish();
            startMainActivity();
        }

        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        link_forgot_passwd = (TextView) findViewById(R.id.link_forgot_passwd);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
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

    private void doLogin() {
        btn_login.setEnabled(false);
        showProgressDialog();
        UserLoginInfo userLoginInfo = new UserLoginInfo(input_email.getText().toString(), input_password.getText().toString());
        new LoginTask().execute(userLoginInfo);
        hideProgressDialog();
    }

    protected void onLoginSuccess(AccessToken accessToken) {
        AppController.getInstance().setoAuthToken(accessToken.getToken());
        startMainActivity();
    }

    protected void onLoginFailure(String errorMsg) {
        finish();
        startActivity(getIntent());
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

    private class LoginTask extends AsyncTask<UserLoginInfo, Integer, AccessToken> {

        private String errorMsg;

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected AccessToken doInBackground(UserLoginInfo... userLoginInfos) {
            if(userLoginInfos == null || userLoginInfos.length == 0)
                throw new RuntimeException("Failed to login.");
            AccessToken accessToken = null;
            UserLoginInfo userLoginInfo = userLoginInfos[0];
            try {
                API api = APIBuilder.create().setAction(COMMAND.SIGN_IN)
                        .addApiParam(APIConstants.Login.CLIENT_KEY, ANDROID_APP_CLIENT_KEY)
                        .addApiParam(APIConstants.Login.CLIENT_SECRET, ANDROID_APP_CLIENT_SECRET)
                        .addApiParam(APIConstants.Login.USERNAME, userLoginInfo.getUsername())
                        .addApiParam(APIConstants.Login.PASSWORD, userLoginInfo.getPasswd())
                        .build();
                accessToken = (AccessToken) api.execute();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
                errorMsg = e.getMessage();
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);
            if(accessToken != null && accessToken.getToken() != null) {
                onLoginSuccess(accessToken);
            }
            else {
                onLoginFailure(errorMsg);
            }
            hideProgressDialog();
        }
    }

    private class UserLoginInfo {
        private String username;
        private String passwd;

        UserLoginInfo(String username, String passwd) {
            this.username = username;
            this.passwd = passwd;
        }

        String getUsername() {
            return username;
        }

        String getPasswd() {
            return  passwd;
        }
    }
}
