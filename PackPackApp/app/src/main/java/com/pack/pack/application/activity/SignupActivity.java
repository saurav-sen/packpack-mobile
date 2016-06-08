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

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.oauth1.client.AccessToken;

public class SignupActivity extends AppCompatActivity {

    private EditText input_name;
    private EditText input_email;
    private EditText input_password;
    private EditText input_address;
    private AppCompatButton btn_signup;
    private TextView link_login;

    private ProgressDialog progressDialog;

    private static final String LOG_TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        input_address = (EditText) findViewById(R.id.input_address);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        link_login = (TextView) findViewById(R.id.link_login);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doSignUp() {
        
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

    protected void onSignUpSuccess(AccessToken accessToken) {

    }

    protected void onSignUpFailure(String errorMsg) {

    }

    private class UserSignUpInfo {
        private String name;
        private String email;
        private String passwd;
        private Address address;

        UserSignUpInfo(String name, String email, String passwd, Address address) {
            this.name = name;
            this.email = email;
            this.passwd = passwd;
            this.address = address;
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

        Address getAddress() {
            return address;
        }
    }

    private class Address {
        private String city;
        private String state;
        private String country;
        private String locality;

        Address(String city, String state, String country, String locality) {
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

    private class SignUpTask extends AsyncTask<UserSignUpInfo, Void, AccessToken> {

        private String errorMsg;

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected AccessToken doInBackground(UserSignUpInfo... userSignUpInfos) {
            if(userSignUpInfos == null || userSignUpInfos.length == 0)
                return null;
            UserSignUpInfo userSignUpInfo = userSignUpInfos[0];
            AccessToken accessToken = null;
            try {
                String name = userSignUpInfo.getName();
                String username = userSignUpInfo.getEmail();
                String passwd = userSignUpInfo.getPasswd();
                String locality = null;
                String city = null;
                String state = null;
                String country = null;
                Address address = userSignUpInfo.getAddress();
                if(address != null) {
                    locality = address.getLocality();
                    city = address.getLocality();
                    state = address.getState();
                    country = address.getCountry();
                }
                API api = APIBuilder.create().setAction(COMMAND.SIGN_UP)
                        .addApiParam(APIConstants.User.Register.NAME, name)
                        .addApiParam(APIConstants.User.Register.EMAIL, username)
                        .addApiParam(APIConstants.User.Register.PASSWORD, passwd)
                        .addApiParam(APIConstants.User.Register.LOCALITY, locality)
                        .addApiParam(APIConstants.User.Register.CITY, city)
                        .addApiParam(APIConstants.User.Register.STATE, state)
                        .addApiParam(APIConstants.User.Register.COUNTRY, country)
                        .addApiParam(APIConstants.User.Register.DOB, null)
                        .addApiParam(APIConstants.User.Register.PROFILE_PICTURE, null)
                        .build();
                api.execute();
                api = APIBuilder.create().setAction(COMMAND.SIGN_IN)
                        .addApiParam(APIConstants.Login.CLIENT_KEY, AppController.ANDROID_APP_CLIENT_KEY)
                        .addApiParam(APIConstants.Login.CLIENT_SECRET, AppController.ANDROID_APP_CLIENT_SECRET)
                        .addApiParam(APIConstants.Login.USERNAME, username)
                        .addApiParam(APIConstants.Login.PASSWORD, passwd)
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
            hideProgressDialog();
            if(accessToken != null && accessToken.getToken() != null) {
                onSignUpSuccess(accessToken);
            }
            else {
                onSignUpFailure(errorMsg);
            }
        }
    }
}
