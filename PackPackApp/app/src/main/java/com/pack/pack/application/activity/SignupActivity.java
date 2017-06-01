package com.pack.pack.application.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.data.util.UserUtil;
import com.pack.pack.application.data.util.UsernameExistenceTestTask;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.service.FetchAddressIntentService;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.oauth1.client.AccessToken;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.pack.pack.application.AppController.FAILURE_RESULT;
import static com.pack.pack.application.AppController.LOCATION_PARCELABLE_ADDRESS_KEY;
import static com.pack.pack.application.AppController.RESULT_RECEIVER;
import static com.pack.pack.application.AppController.LOCATION_FINE_ACCESS_REQUEST_CODE;
import static com.pack.pack.application.AppController.SUCCESS_RESULT;
import static com.pack.pack.application.AppController.PLACE_AUTO_COMPLETE_REQ_CODE;

/**
 *
 * @author Saurav
 *
 */
public class SignupActivity extends AbstractAppCompatActivity {

    private EditText input_name;
    private EditText input_email;
    private EditText input_password;
    private EditText input_password_confirm;
    private EditText input_city;
    private EditText input_country;
    private AppCompatButton btn_signup;
    private TextView link_login;

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
        input_password_confirm = (EditText) findViewById(R.id.input_password_confirm);
        input_city = (EditText) findViewById(R.id.input_city);
        input_country = (EditText) findViewById(R.id.input_country);
        btn_signup = (AppCompatButton) findViewById(R.id.btn_signup);
        link_login = (TextView) findViewById(R.id.link_login);

        final Calendar c = Calendar.getInstance();
        int year = 1978;//c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

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
                goToNextPage();
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

    private void goToNextPage() {
        String email = input_email.getText() != null ? input_email.getText().toString().trim() : null;
        String passwd = input_password.getText() != null ? input_password.getText().toString().trim() : null;
        String passwd2 = input_password_confirm.getText() != null ? input_password_confirm.getText().toString().trim() : null;
        String name = input_name.getText() != null ? input_name.getText().toString().trim() : null;
        String city = input_city.getText() != null ? input_city.getText().toString().trim() : null;
        String country = input_country.getText() != null ? input_country.getText().toString().trim() : null;

        boolean valid = true;
        boolean isPasswd = true;
        boolean isPasswd2 = true;
        if(email == null || email.trim().isEmpty()) {
            Snackbar.make(input_email, "Email is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
            return;
        }
        if(passwd == null || passwd.trim().isEmpty()) {
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
        }
        if(name == null || name.trim().isEmpty()) {
            Snackbar.make(input_name, "Name is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(city == null || city.trim().isEmpty()) {
            Snackbar.make(input_city, "City is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        if(country == null || country.trim().isEmpty()) {
            Snackbar.make(input_country, "Country is empty", Snackbar.LENGTH_LONG).show();
            valid = false;
        }

        if(isPasswd && isPasswd2 && !passwd.equals(passwd2)) {
            Snackbar.make(input_password, "Password did not match", Snackbar.LENGTH_LONG).show();
            valid = false;
        }

        if(valid) {
            if(!UserUtil.isValidEmailAddressFormat(email)) {
                Snackbar.make(input_email, "Email address is invalid", Snackbar.LENGTH_LONG).show();
                valid = false;
            }
            else {
                String validationError = UserUtil.applyPasswordPolicy(passwd);
                if(validationError != null) {
                    Snackbar.make(input_password, validationError.trim(), Snackbar.LENGTH_LONG).show();
                    valid = false;
                }
            }
        }

        if(!valid)
            return;

        UsernameExistenceTestTask task = new UsernameExistenceTestTask(SignupActivity.this);
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
        }
        /*LocalAddress addr = null;
        if(address != null) {
            addr = new LocalAddress(null, null, address.getCountryName(), address.getLocality());
        }*/
        Intent intent = new Intent(SignupActivity.this, PreSignupActivity.class);
        intent.putExtra(PreSignupActivity.EMAIL, email);
        intent.putExtra(PreSignupActivity.PASSWD, passwd);
        intent.putExtra(PreSignupActivity.NAME, name);
        intent.putExtra(PreSignupActivity.CITY, city);
        intent.putExtra(PreSignupActivity.COUNTRY, country);
        finish();
        startActivity(intent);
    }
}
