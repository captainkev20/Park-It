package com.example.kevinwalker.parkit.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.users.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationHelper {

    private static final String TAG = CustomLocation.class.getName();
    private boolean mLocationPermissionStatus = false;
    private CustomLocation customLocation;
    private boolean mhasLocationPermission = false;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Context mContext;

    public LocationHelper(Context mContext) {
        this.mContext = mContext;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    public CustomLocation getCurrentLocation() {
        try {
            if (mhasLocationPermission) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            User user = FirestoreHelper.getInstance().getCurrentUser();
                            customLocation = new CustomLocation((Location) task.getResult());
                            user.setUserCurrentLocation(customLocation);
                            FirestoreHelper.getInstance().setCurrentUser(user);
                        } else {
                            customLocation = new CustomLocation();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security issue");
        }

        return FirestoreHelper.getInstance().getCurrentUser().getUserCurrentLocation();
    }

    public boolean hasLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show user rationale BEFORE permission box
            if (ActivityCompat.shouldShowRequestPermissionRationale((NavDrawer) mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getmContext())
                        .setTitle(getmContext().getResources().getString(R.string.PERMISSION_DIALOG_TITLE))
                        .setMessage(getmContext().getResources().getString(R.string.PERMISSION_DIALOG_MESSAGE))
                        .setPositiveButton(getmContext().getResources().getString(R.string.PERMISSION_DIALOG_POSITIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions((Activity) getmContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                // They said yes, so set to true
                                mhasLocationPermission = true;
                            }
                        })
                        .setNegativeButton(getmContext().getResources().getString(R.string.PERMISSION_DIALOG_NEGATIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "CustomLocation not available", Toast.LENGTH_SHORT).show();
                                // UserProfileFragment said no, set to false
                                mhasLocationPermission = false;
                            }
                        }).show();
            } else {
                // Request permission
                ActivityCompat.requestPermissions((NavDrawer) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }

            return mhasLocationPermission;

        } else {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper());
            mhasLocationPermission = true;
            return mhasLocationPermission;
        }
    }

    public void startLocationUpdates() {

        // Create the location request to start receiving updates
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                NavDrawer.saveCurrentUserLocation(new CustomLocation(locationResult.getLastLocation()));
            }
        };

        if (ContextCompat.checkSelfPermission(mContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper());
        }
    }

    public boolean ismLocationPermissionStatus() {
        return mLocationPermissionStatus;
    }

    public void setmLocationPermissionStatus(boolean mLocationPermissionStatus) {
        this.mLocationPermissionStatus = mLocationPermissionStatus;
    }

    public FusedLocationProviderClient getmFusedLocationProviderClient() {
        return mFusedLocationProviderClient;
    }

    public void setmFusedLocationProviderClient(FusedLocationProviderClient mFusedLocationProviderClient) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
    }

    public CustomLocation getCustomLocation() {
        return customLocation;
    }

    public void setCustomLocation(CustomLocation customLocation) {
        this.customLocation = customLocation;
    }

    public boolean isMhasLocationPermission() {
        return mhasLocationPermission;
    }

    public void setMhasLocationPermission(boolean mhasLocationPermission) {
        this.mhasLocationPermission = mhasLocationPermission;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
