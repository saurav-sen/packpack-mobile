package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;

public class PreSignupActivity extends AbstractAppCompatActivity  implements IAsyncTaskStatusListener {

    private DatePicker input_dob;

    private AppCompatButton btn_signup;
    //private TextView link_login;

    private String email;
    private String passwd;
    private String name;
    private String city;
    private String country;

    public static final String EMAIL = "EMAIL";
    public static final String PASSWD = "PASSWD";
    public static final String NAME = "NAME";
    public static final String CITY = "CITY";
    public static final String COUNTRY = "COUNTRY";

    private static final String LOG_TAG = "PreSignupActivity";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_signup);

        email = getIntent().getStringExtra(EMAIL);
        passwd = getIntent().getStringExtra(PASSWD);
        name = getIntent().getStringExtra(NAME);
        city = getIntent().getStringExtra(CITY);
        country = getIntent().getStringExtra(COUNTRY);

        input_dob = (DatePicker) findViewById(R.id.input_dob);

        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        //link_login = (TextView) findViewById(R.id.link_login);

        final Calendar c = Calendar.getInstance();
        int year = 1978;//c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // set choosen date into datepicker
        input_dob.init(year, month, day, null);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        /*link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreSignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });*/
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
        //startMainActivity();
        startFollowCategoryActivity();
    }

    protected void onSignUpSuccess() {
        UserInfo userInfo = new UserInfo(email.toString(), passwd.toString());
        doLogin(userInfo);
    }

    private void doLogin(UserInfo userInfo) {
        new LoginTask(this, this, false).execute(userInfo);
    }

    protected void onSignUpFailure(String errorMsg) {
        Toast.makeText(PreSignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        hideProgressDialog();
        /*Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", input_email.getText().toString());
        intent.putExtra("passwd", input_password.getText().toString());
        intent.putExtra("loginStatus", false);
        finish();
        startActivity(intent);*/
    }

    private void startFollowCategoryActivity() {
        Intent intent = new Intent(this, FollowCategoryActivity.class);
        finish();
        startActivity(intent);
    }

    private void doSignUp() {
        StringBuilder dob = new StringBuilder();
        dob.append(input_dob.getDayOfMonth());
        dob.append("/");
        dob.append(input_dob.getMonth());
        dob.append("/");
        dob.append(input_dob.getYear());

        /*LocalAddress addr = null;
        if(address != null) {
            addr = new LocalAddress(null, null, address.getCountryName(), address.getLocality());
        }*/
        UserSignUpInfo userSignUpInfo = new UserSignUpInfo(name, email, passwd, city, country, dob.toString());
        new SignUpTask().execute(userSignUpInfo);
    }

    private class UserSignUpInfo {
        private String name;
        private String email;
        private String passwd;
        private String city;
        private String country;
        private String dob;

        UserSignUpInfo(String name, String email, String passwd, String city, String country, String dob) {
            this.name = name;
            this.email = email;
            this.passwd = passwd;
            this.city = city;
            this.dob = dob;
            this.country = country;
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
