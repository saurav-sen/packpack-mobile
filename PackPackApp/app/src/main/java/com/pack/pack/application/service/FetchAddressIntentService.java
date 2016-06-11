package com.pack.pack.application.service;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pack.pack.application.AppController;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.pack.pack.application.AppController.SUCCESS_RESULT;
import static com.pack.pack.application.AppController.FAILURE_RESULT;
import static com.pack.pack.application.AppController.LOCATION_PARCELABLE_ADDRESS_KEY;
import static com.pack.pack.application.AppController.LOCATION_PARCELABLE_ERR_MSG_KEY;
import static com.pack.pack.application.AppController.RESULT_RECEIVER;

/**
 * Created by Saurav on 09-06-2016.
 */
public class FetchAddressIntentService extends IntentService implements LocationListener {

    private LocationManager locationManager;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    private static final int ACCESS_FINE_LOCATION_REQ_CODE = 111;

    private Geocoder geocoder;

    private ResultReceiver receiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errMsg = null;
        Location location = null;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                if(location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } else {
            errMsg = "Failed to retrieve location from GPS Provider.";
        }

        List<Address> addresses = null;
        if(errMsg == null) {
            receiver = intent.getParcelableExtra(RESULT_RECEIVER);
            if(location != null) {
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    errMsg = e.getMessage();
                }
            }
            else {
                errMsg = "Failed to retrieve location from GPS Provider.";
            }
        }

        if(addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            deliverResultToReceiver(SUCCESS_RESULT, address);
        } else {
            deliverResultToReceiver(FAILURE_RESULT, errMsg);
        }
    }

    private void deliverResultToReceiver(int code, String result) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(LOCATION_PARCELABLE_ERR_MSG_KEY, result);
        receiver.send(code, bundle);
    }

    private void deliverResultToReceiver(int code, Address result) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(LOCATION_PARCELABLE_ADDRESS_KEY, result);
        receiver.send(code, bundle);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
