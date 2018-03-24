package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

public class PreSignupActivity2 extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

    private static final String LOG_TAG = "PreSignupActivity2";

    public static final String DOB = "DOB";

    private EditText signup_verifier;

    private ProgressDialog progressDialog;

    private String email;
    private String passwd;
    private String name;
    private String city;
    private String country;
    private String dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_signup2);

        email = getIntent().getStringExtra(PreSignupActivity.EMAIL);
        passwd = getIntent().getStringExtra(PreSignupActivity.PASSWD);
        name = getIntent().getStringExtra(PreSignupActivity.NAME);
        city = getIntent().getStringExtra(PreSignupActivity.CITY);
        country = getIntent().getStringExtra(PreSignupActivity.COUNTRY);
        dob = getIntent().getStringExtra(DOB);

        TextView signup_note = (TextView) findViewById(R.id.signup_note);
        signup_note.setText("Please enter the verification code sent over <" + email + ">");
        signup_verifier = (EditText) findViewById(R.id.signup_verifier);
        Button btn_signup_done = (Button) findViewById(R.id.btn_signup_done);
        btn_signup_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifierCode = signup_verifier.getText() != null ? signup_verifier.getText().toString() : null;
                if (verifierCode == null || verifierCode.trim().isEmpty()) {
                    Snackbar.make(signup_verifier, "Verification Code is mandatory", Snackbar.LENGTH_LONG).show();
                    return;
                }
                doSignUp(verifierCode);
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog != null)
            progressDialog.dismiss();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public void onPreStart(String taskID) {
        showProgressDialog();
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        LoggedInUserInfo userInfo = (LoggedInUserInfo)data;
        AccessToken token = userInfo.getAccessToken();
        JUser user = userInfo.getUser();
        getIntent().putExtra("loginStatus", true);
        finish();
        startMainActivity();
    }

    protected void onSignUpSuccess() {
        UserInfo userInfo = new UserInfo(email.toString(), passwd.toString());
        doLogin(userInfo);
    }

    private void doLogin(UserInfo userInfo) {
        new LoginTask(this, this, false).execute(userInfo);
    }

    protected void onSignUpFailure(String errorMsg) {
        Toast.makeText(PreSignupActivity2.this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        hideProgressDialog();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, LandingPageActivity.class);
        startActivity(intent);
    }

    private void doSignUp(String verifierCode) {

        /*LocalAddress addr = null;
        if(address != null) {
            addr = new LocalAddress(null, null, address.getCountryName(), address.getLocality());
        }*/
        UserSignUpInfo userSignUpInfo = new UserSignUpInfo(name, email, passwd, city, country, dob, verifierCode);
        new SignUpTask().execute(userSignUpInfo);
    }

    private class UserSignUpInfo {

        private String name;
        private String email;
        private String passwd;
        private String city;
        private String country;
        private String dob;

        private String verifierCode;

        UserSignUpInfo(String name, String email, String passwd, String city, String country, String dob, String verifierCode) {
            this.name = name;
            this.email = email;
            this.passwd = passwd;
            this.city = city;
            this.dob = dob;
            this.country = country;
            this.verifierCode = verifierCode;
        }

        String getName() {
            return name;
        }

        String getEmail() {
            return email;
        }

        String getPasswd() {
            return passwd;
        }

        String getCity() {
            return city;
        }

        String getCountry() {
            return country;
        }

        String getDob() {
            return dob;
        }

        String getVerifierCode() {
            return verifierCode;
        }
    }

    private class LocalAddress {
        private String city;
        private String state;
        private String country;
        private String locality;

        LocalAddress(String city, String state, String country, String locality) {
            this.city = city;
            this.state = state;
            this.country = country;
            this.locality = locality;
        }

        String getCity() {
            return city;
        }

        String getState() {
            return  state;
        }

        String getCountry() {
            return country;
        }

        String getLocality() {
            return locality;
        }
    }

    private class SignUpTask extends AsyncTask<UserSignUpInfo, Void, Void> {

        private String errorMsg;

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(UserSignUpInfo... userSignUpInfos) {
            if(userSignUpInfos == null || userSignUpInfos.length == 0)
                return null;
            UserSignUpInfo userSignUpInfo = userSignUpInfos[0];
            try {
                String name = userSignUpInfo.getName();
                String username = userSignUpInfo.getEmail();
                String passwd = userSignUpInfo.getPasswd();
                String city = userSignUpInfo.getCity();
                String country = userSignUpInfo.getCountry();
                String dob = userSignUpInfo.getDob();
                String verifier = userSignUpInfo.getVerifierCode();
                API api = APIBuilder.create(ApiConstants.BASE_URL).setAction(COMMAND.SIGN_UP)
                        .addApiParam(APIConstants.User.Register.NAME, name)
                        .addApiParam(APIConstants.User.Register.EMAIL, username)
                        .addApiParam(APIConstants.User.Register.PASSWORD, passwd)
                                //.addApiParam(APIConstants.User.Register.LOCALITY, locality)
                        .addApiParam(APIConstants.User.Register.CITY, city)
                        .addApiParam(APIConstants.User.Register.COUNTRY, country)
                                //.addApiParam(APIConstants.User.Register.STATE, state)
                                //.addApiParam(APIConstants.User.Register.COUNTRY, country)
                        .addApiParam(APIConstants.User.Register.DOB, dob)
                                //.addApiParam(APIConstants.User.Register.PROFILE_PICTURE, null)
                        .addApiParam(APIConstants.User.Register.VERIFIER, verifier)
                        .build();
                api.execute();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
                errorMsg = "ERROR: " + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            if(errorMsg == null) {
                onSignUpSuccess();
            }
            else {
                onSignUpFailure(errorMsg);
            }
        }
    }
}
