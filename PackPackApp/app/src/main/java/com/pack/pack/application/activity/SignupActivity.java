package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.UserUtil;
import com.pack.pack.application.data.util.UsernameExistenceTestTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;

import java.util.HashMap;
import java.util.Map;

import static com.pack.pack.application.AppController.LOCATION_COARSE_ACCESS_REQUEST_CODE;

//import com.pack.pack.application.data.util.LoginTask;

/**
 *
 * @author Saurav
 *
 */
public class SignupActivity extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

    private EditText input_name;
    private EditText input_email;
    /*private EditText input_password;
    private EditText input_password_confirm;*/
    private AppCompatButton btn_signup;
   // private TextView link_login;

    //private android.location.Address address;

    private static final String LOG_TAG = "SignupActivity";

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    private String email;
   // private String passwd;
    private String name;

    private double longitude = 28.704060;
    private double latitude = 77.102493;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_COARSE_ACCESS_REQUEST_CODE);
        } else {
            startAddressService();
        }

        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        /*input_password = (EditText) findViewById(R.id.input_password);
        input_password_confirm = (EditText) findViewById(R.id.input_password_confirm);*/
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
       // link_login = (TextView) findViewById(R.id.link_login);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextPage();
            }
        });

        /*link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void startAddressService() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_COARSE_ACCESS_REQUEST_CODE);
            return;
        } else {
            LocationManager locationManager = null;
            try {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            if(locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            } else {
                latitude = 28.704060;
                longitude = 77.102493;
            }
        }

        /*Intent service = new Intent(this, FetchAddressIntentService.class);
        service.putExtra(RESULT_RECEIVER, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                switch (resultCode) {
                    case SUCCESS_RESULT:
                        address = resultData.getParcelable(LOCATION_PARCELABLE_ADDRESS_KEY);
                        input_address.setText(address.getLocality() + ", " + address.getCountryName() + ", " + address.getPostalCode());

                        break;
                    case FAILURE_RESULT:
                        break;
                }
            }
        });
        startService(service);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == LOCATION_COARSE_ACCESS_REQUEST_CODE
                && grantResults != null && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAddressService();
        }
    }

    private void goToNextPage() {
        email = input_email.getText() != null ? input_email.getText().toString().trim() : null;
       /* passwd = input_password.getText() != null ? input_password.getText().toString().trim() : null;
        String passwd2 = input_password_confirm.getText() != null ? input_password_confirm.getText().toString().trim() : null;*/
        name = input_name.getText() != null ? input_name.getText().toString().trim() : null;

        boolean valid = true;
       // boolean isPasswd = true;
       // boolean isPasswd2 = true;
        if(email == null || email.trim().isEmpty()) {
            Snackbar.make(input_email, "Email is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
            return;
        }
        /*if(passwd == null || passwd.trim().isEmpty()) {
            Snackbar.make(input_password, "Password is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
            isPasswd = false;
            return;
        }
        if(isPasswd) {
            String errMsg = UserUtil.applyPasswordPolicy(passwd);
            if(errMsg != null) {
                Snackbar.make(input_password, errMsg, Snackbar.LENGTH_LONG).show();
                valid = false;
                return;
            }
        }
        if(passwd2 == null || passwd2.trim().isEmpty()) {
            Snackbar.make(input_password_confirm, "Confirm Password is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
            isPasswd2 = false;
        }*/
        if(name == null || name.trim().isEmpty()) {
            Snackbar.make(input_name, "Name is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }

       /* if(isPasswd && isPasswd2 && !passwd.equals(passwd2)) {
            Snackbar.make(input_password, "Password did not match", Snackbar.LENGTH_LONG).show();
            valid = false;
        }*/

        if(valid) {
            if(!UserUtil.isValidEmailAddressFormat(email)) {
                Snackbar.make(input_email, "Email address is invalid", Snackbar.LENGTH_LONG).show();
                valid = false;
            }
            /*else {
                String validationError = UserUtil.applyPasswordPolicy(passwd);
                if(validationError != null) {
                    Snackbar.make(input_password, validationError.trim(), Snackbar.LENGTH_LONG).show();
                    valid = false;
                }
            }*/
        }

        if(!valid)
            return;

        /*UsernameExistenceTestTask task = new UsernameExistenceTestTask(SignupActivity.this);
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
            Snackbar.make(input_email, "[Timed OUT]: Failed validating EMail", Snackbar.LENGTH_LONG).show();
            return;
        }

        if(!task.isValidUserName()) {
            String errorMsg = task.getErrorMsg();
            if(errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = "Email ID is already registered with us";
            }
            Snackbar.make(input_email, errorMsg, Snackbar.LENGTH_LONG).show();
            return;
        }
        valid = valid & task.isValidUserName();

        if(!valid) {
            Snackbar.make(input_email, "Sorry something went wrong", Snackbar.LENGTH_LONG).show();
            return;
        }*/

        IssueVerificationCodeInfo dto = new IssueVerificationCodeInfo(email, name);
        new IssueSignupVerifier(SignupActivity.this).addListener(SignupActivity.this).execute(dto);
    }

    private void goToNextPage2() {
        /*LocalAddress addr = null;
        if(address != null) {
            addr = new LocalAddress(null, null, address.getCountryName(), address.getLocality());
        }*/
        Intent intent = new Intent(SignupActivity.this, PreSignupActivity2.class);
        intent.putExtra(PreSignupActivity2.EMAIL, email);
        //intent.putExtra(PreSignupActivity2.PASSWD, passwd);
        intent.putExtra(PreSignupActivity2.NAME, name);
        intent.putExtra(PreSignupActivity2.LONGITUDE, longitude);
        intent.putExtra(PreSignupActivity2.LATITUDE, latitude);
        finish();
        startActivity(intent);
    }

    @Override
    public void onPreStart(String taskID) {

    }

    @Override
    public void onSuccess(String taskID, Object data) {
        goToNextPage2();
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Snackbar.make(input_email, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostComplete(String taskID) {

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
