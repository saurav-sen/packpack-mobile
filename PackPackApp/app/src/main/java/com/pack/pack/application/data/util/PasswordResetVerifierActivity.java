package com.pack.pack.application.data.util;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pack.pack.application.R;
import com.pack.pack.application.activity.LoginActivity;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetVerifierActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    public static final String EMAIL_ADDRESS = "email_addr";
    public static final String NEW_PASSWORD = "new_password";

    private EditText passwd_reset_verifier;

    private Button passwd_reset_confirm;

    private String userName;

    private String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_verifier);

        userName = getIntent().getStringExtra(EMAIL_ADDRESS);
        newPassword = getIntent().getStringExtra(NEW_PASSWORD);

        passwd_reset_verifier = (EditText) findViewById(R.id.passwd_reset_verifier);
        passwd_reset_confirm = (Button) findViewById(R.id.passwd_reset_confirm);
        passwd_reset_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifier = passwd_reset_verifier.getText() != null ?
                        passwd_reset_verifier.getText().toString() : null;
                if(verifier == null || verifier.isEmpty()) {
                    Snackbar.make(passwd_reset_verifier, "Invalid code",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                PasswdResetDetails resetDetails = new PasswdResetDetails(
                        userName, verifier.trim(), newPassword);
                new ResetPasswordTask(PasswordResetVerifierActivity.this)
                        .addListener(PasswordResetVerifierActivity.this).execute(resetDetails);
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(PasswordResetVerifierActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(data == null) {
            Snackbar.make(passwd_reset_verifier, "Invalid code",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        startLoginActivity();
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Snackbar.make(passwd_reset_verifier, errorMsg,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    private class PasswdResetDetails {

        private String userName;
        private String verifier;
        private String newPassword;

        PasswdResetDetails(String userName, String verifier, String newPassword) {
            this.userName = userName;
            this.verifier = verifier;
            this.newPassword = newPassword;
        }

        public String getVerifier() {
            return verifier;
        }

        public void setVerifier(String verifier) {
            this.verifier = verifier;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    private class ResetPasswordTask extends AbstractNetworkTask<PasswdResetDetails, Integer, JStatus> {

        private String errMsg;

        ResetPasswordTask(Context context) {
            super(false, false, false, context, false, true);
        }

        @Override
        protected String getFailureMessage() {
            return errMsg;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.RESET_USER_PASSWD;
        }

        @Override
        protected Map<String, Object> prepareApiParams(PasswdResetDetails inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.User.USERNAME, inputObject.getUserName());
            apiParams.put(APIConstants.User.PasswordReset.VERIFIER_CODE, inputObject.getVerifier());
            apiParams.put(APIConstants.User.PasswordReset.NEW_PASSWORD, inputObject.getNewPassword());
            return apiParams;
        }

        @Override
        protected JStatus executeApi(API api) throws Exception {
            try {
                return (JStatus) api.execute();
            } catch (Exception e) {
                errMsg = "Failed: " + e.getMessage();
                return null;
            }
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }
    }
}
