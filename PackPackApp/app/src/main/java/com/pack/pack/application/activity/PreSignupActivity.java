package com.pack.pack.application.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PreSignupActivity extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

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
                IssueVerificationCodeInfo dto = new IssueVerificationCodeInfo(email, name);
                new IssueSignupVerifier(PreSignupActivity.this).addListener(PreSignupActivity.this).execute(dto);
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

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        goToNext();
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Snackbar.make(input_dob, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    private void goToNext() {
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
        Intent intent = new Intent(PreSignupActivity.this, PreSignupActivity2.class);
        intent.putExtra(EMAIL, email);
        intent.putExtra(PASSWD, passwd);
        intent.putExtra(NAME, name);
        intent.putExtra(CITY, city);
        intent.putExtra(COUNTRY, country);
        intent.putExtra(PreSignupActivity2.DOB, dob.toString());
        finish();
        startActivity(intent);
    }

    private class IssueVerificationCodeInfo {

        private String email;

        private String nameOfUser;

        IssueVerificationCodeInfo(String email, String nameOfUser) {
            this.email = email;
            this.nameOfUser = nameOfUser;
        }

        public String getEmail() {
            return email;
        }

        public String getNameOfUser() {
            return nameOfUser;
        }
    }

    private class IssueSignupVerifier extends AbstractNetworkTask<IssueVerificationCodeInfo, Integer, JStatus> {

        private String errMsg;

        public IssueSignupVerifier(Context context) {
            super(false, false, false, context, false, true);
        }

        @Override
        protected String getFailureMessage() {
            return errMsg;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ISSUE_SIGNUP_VERIFIER;
        }

        @Override
        protected Map<String, Object> prepareApiParams(IssueVerificationCodeInfo inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.User.Register.EMAIL, inputObject.getEmail());
            apiParams.put(APIConstants.User.Register.NAME, inputObject.getNameOfUser());
            return apiParams;
        }

        @Override
        protected JStatus executeApi(API api) throws Exception {
            try {
                return (JStatus) api.execute();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                errMsg = "Failed Issing Verification Code";
                return null;
            }
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }
    }
}
