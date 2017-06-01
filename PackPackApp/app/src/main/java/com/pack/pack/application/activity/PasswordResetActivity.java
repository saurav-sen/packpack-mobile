package com.pack.pack.application.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.PasswordResetVerifierActivity;
import com.pack.pack.application.data.util.UserUtil;
import com.pack.pack.application.data.util.UsernameExistenceTestTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private EditText passwd_reset_emailInfo;

    private EditText passwd_reset_newPasswd;

    private EditText passwd_reset_confirmPasswd;

    private Button passwd_reset_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        passwd_reset_emailInfo = (EditText) findViewById(R.id.passwd_reset_emailInfo);
        passwd_reset_newPasswd = (EditText) findViewById(R.id.passwd_reset_newPasswd);
        passwd_reset_confirmPasswd = (EditText) findViewById(R.id.passwd_reset_confirmPasswd);

        passwd_reset_submit = (Button) findViewById(R.id.passwd_reset_submit);
        passwd_reset_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
    }

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(data == null) {
            Snackbar.make(passwd_reset_emailInfo, "Sorry!! Something went wrong", Snackbar.LENGTH_LONG).show();
            return;
        }
        JStatus status = (JStatus) data;
        if(status.getStatus() == StatusType.ERROR) {
            String errorMsg = status.getInfo();
            if(errorMsg == null) {
                errorMsg = "Sorry!! Something went wrong";
            }
            Snackbar.make(passwd_reset_emailInfo, errorMsg, Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(PasswordResetActivity.this, PasswordResetVerifierActivity.class);
        intent.putExtra(PasswordResetVerifierActivity.EMAIL_ADDRESS, passwd_reset_emailInfo.getText().toString());
        intent.putExtra(PasswordResetVerifierActivity.NEW_PASSWORD, passwd_reset_newPasswd.getText().toString());
        finish();
        startActivity(intent);
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        if(errorMsg == null) {
            errorMsg = "Sorry!! Something went wrong";
        }
        Snackbar.make(passwd_reset_emailInfo, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    private void onSubmit() {
        String email = passwd_reset_emailInfo.getText() != null ? passwd_reset_emailInfo.getText().toString() : null;

        if(email == null || email.trim().isEmpty()) {
            Snackbar.make(passwd_reset_emailInfo, "Email can't be empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(!UserUtil.isValidEmailAddressFormat(email.trim())) {
            Snackbar.make(passwd_reset_emailInfo, "Email can't be empty", Snackbar.LENGTH_LONG).show();
            return;
        }

        String passwd = passwd_reset_newPasswd.getText() != null ? passwd_reset_newPasswd.getText().toString() : null;
        if(passwd == null || passwd.trim().isEmpty()) {
            Snackbar.make(passwd_reset_newPasswd, "Password can't be empty", Snackbar.LENGTH_LONG).show();
            return;
        }
        String errMsg = UserUtil.applyPasswordPolicy(passwd);
        if(errMsg != null) {
            Snackbar.make(passwd_reset_newPasswd, errMsg, Snackbar.LENGTH_LONG).show();
            return;
        }
        String passwd2 = passwd_reset_confirmPasswd.getText() != null ? passwd_reset_confirmPasswd.getText().toString() : null;
        if(passwd2 == null || passwd2.trim().isEmpty()) {
            Snackbar.make(passwd_reset_confirmPasswd, "Please confirm password", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(!passwd.equals(passwd2)) {
            Snackbar.make(passwd_reset_confirmPasswd, "Passwords didn't match", Snackbar.LENGTH_LONG).show();
            return;
        }

        String validationError = UserUtil.applyPasswordPolicy(passwd);
        if(validationError != null) {
            Snackbar.make(passwd_reset_newPasswd, validationError.trim(), Snackbar.LENGTH_LONG).show();
            return;
        }

        boolean valid = true;

        UsernameExistenceTestTask task = new UsernameExistenceTestTask(PasswordResetActivity.this);
        task.execute(email);

        long timeout = 3 * 60 * 1000;
        long count = 0;
        while (!task.isValidationComplete() && count <= timeout) {
            try {
                Thread.sleep(100);
                count = count + 100;
            } catch (InterruptedException e) {
                // ignore it.
            }
        }

        if(count > timeout) {
            Snackbar.make(passwd_reset_emailInfo, "[Timed OUT]: Failed validating EMail", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(task.isValidUserName()) { // means it is a new user (NOT regsitered hence valid userName)
            String errorMsg = task.getErrorMsg();
            if(errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = "Email ID is NOT registered with us";
            }
            Snackbar.make(passwd_reset_emailInfo, errorMsg, Snackbar.LENGTH_LONG).show();
            return;
        }
        /*valid = valid & task.isValidUserName();

        if(!valid) {
            Snackbar.make(passwd_reset_emailInfo, "Not a registered user", Snackbar.LENGTH_LONG).show();
            return;
        }*/
        new IssueResetPasswordLinkTask(PasswordResetActivity.this).addListener(PasswordResetActivity.this).execute(email);
    }

    private class IssueResetPasswordLinkTask extends AbstractNetworkTask<String, Integer, JStatus> {

        private String errMsg;

        IssueResetPasswordLinkTask(Context context) {
            super(false, false, false, context, false, true);
        }

        @Override
        protected String getFailureMessage() {
            return errMsg;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ISSUE_PASSWD_RESET_LINK;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.User.USERNAME, inputObject);
            return apiParams;
        }

        @Override
        protected JStatus executeApi(API api) throws Exception {
            try {
                return (JStatus) api.execute();
            } catch (Exception e) {
                errMsg = e.getMessage();
                Log.e("IssueResetPasswdOTP", e.getMessage(), e);
                JStatus status = new JStatus();
                status.setInfo("Something went wrong");
                status.setStatus(StatusType.ERROR);
                return status;
            }
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }
    }
}
