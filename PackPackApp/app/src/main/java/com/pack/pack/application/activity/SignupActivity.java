package com.pack.pack.application.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.service.FetchAddressIntentService;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

import java.util.Calendar;

import static com.pack.pack.application.AppController.FAILURE_RESULT;
import static com.pack.pack.application.AppController.LOCATION_PARCELABLE_ADDRESS_KEY;
import static com.pack.pack.application.AppController.RESULT_RECEIVER;
import static com.pack.pack.application.AppController.LOCATION_FINE_ACCESS_REQUEST_CODE;
import static com.pack.pack.application.AppController.SUCCESS_RESULT;
import static com.pack.pack.application.AppController.PLACE_AUTO_COMPLETE_REQ_CODE;

public class SignupActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private EditText input_name;
    private EditText input_email;
    private EditText input_password;
    private EditText input_city;
    private DatePicker input_dob;
    private AppCompatButton btn_signup;
    private TextView link_login;

    private ProgressDialog progressDialog;

    //private android.location.Address address;

    private static final String LOG_TAG = "SignupActivity";

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_FINE_ACCESS_REQUEST_CODE);
        } else {
            startAddressService();
        }

        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        input_city = (EditText) findViewById(R.id.input_city);
        input_dob = (DatePicker) findViewById(R.id.input_dob);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        link_login = (TextView) findViewById(R.id.link_login);

        final Calendar c = Calendar.getInstance();
        int year = 1978;//c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
       /* tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));*/

        // set current date into datepicker
        input_dob.init(year, month, day, null);

        /*input_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(
                            PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SignupActivity.this);
                    startActivityForResult(intent, PLACE_AUTO_COMPLETE_REQ_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });*/

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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_AUTO_COMPLETE_REQ_CODE) {
            if(resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(LOG_TAG, "Name:: " + place.getName().toString());
                Log.i(LOG_TAG, "Address:: " + place.getAddress().toString());
                input_address.setText(place.getAddress());
            } else {
                Log.i(LOG_TAG, "Failed to retrieve location/address");
            }
        }
    }*/

    private void startAddressService() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_FINE_ACCESS_REQUEST_CODE);
            return;
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
        if(requestCode == LOCATION_FINE_ACCESS_REQUEST_CODE
                && grantResults != null && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAddressService();
        }
    }

    private void doSignUp() {
        String email = input_email.getText() != null ? input_email.getText().toString().trim() : null;
        String passwd = input_password.getText() != null ? input_password.getText().toString().trim() : null;
        String name = input_name.getText() != null ? input_name.getText().toString().trim() : null;
        String city = input_city.getText() != null ? input_city.getText().toString().trim() : null;
        StringBuilder dob = new StringBuilder();
        dob.append(input_dob.getDayOfMonth());
        dob.append("/");
        dob.append(input_dob.getMonth());
        dob.append("/");
        dob.append(input_dob.getYear());

        boolean valid = true;
        if(email == null || email.isEmpty()) {
            Snackbar.make(input_email, "Email is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(passwd == null || passwd.isEmpty()) {
            Snackbar.make(input_password, "Password is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(name == null || name.isEmpty()) {
            Snackbar.make(input_name, "Name is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(city == null || city.isEmpty()) {
            Snackbar.make(input_city, "City is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(!valid)
            return;
        /*LocalAddress addr = null;
        if(address != null) {
            addr = new LocalAddress(null, null, address.getCountryName(), address.getLocality());
        }*/
        UserSignUpInfo userSignUpInfo = new UserSignUpInfo(name, email, passwd, city, dob.toString());
        new SignUpTask().execute(userSignUpInfo);
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

    protected void onSignUpSuccess() {
        UserInfo userInfo = new UserInfo(input_email.getText().toString(), input_password.getText().toString());
        doLogin(userInfo);
    }

    private void doLogin(UserInfo userInfo) {
        new LoginTask(this, this).execute(userInfo);
    }

    protected void onSignUpFailure(String errorMsg) {
        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
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

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPostComplete() {

    }

    public void onFailure(String errorMsg) {
        hideProgressDialog();
        /*Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", input_email.getText().toString());
        intent.putExtra("passwd", input_password.getText().toString());
        intent.putExtra("loginStatus", false);
        finish();
        startActivity(intent);*/
    }

    private class UserSignUpInfo {
        private String name;
        private String email;
        private String passwd;
        private String city;
        private String dob;

        UserSignUpInfo(String name, String email, String passwd, String city, String dob) {
            this.name = name;
            this.email = email;
            this.passwd = passwd;
            this.city = city;
            this.dob = dob;
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
                String dob = userSignUpInfo.getDob();
                API api = APIBuilder.create().setAction(COMMAND.SIGN_UP)
                        .addApiParam(APIConstants.User.Register.NAME, name)
                        .addApiParam(APIConstants.User.Register.EMAIL, username)
                        .addApiParam(APIConstants.User.Register.PASSWORD, passwd)
                        //.addApiParam(APIConstants.User.Register.LOCALITY, locality)
                        .addApiParam(APIConstants.User.Register.CITY, city)
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
