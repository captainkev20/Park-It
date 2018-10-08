package com.example.kevinwalker.parkit.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kevinwalker.parkit.NavDrawer;
import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationHelper {

    private boolean mLocationPermissionStatus = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private CustomLocation customLocation;
    private boolean mhasLocationPermission = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = CustomLocation.class.getName();

    private Context context;

    public LocationHelper(Context context) {
        this.context = context;
    }


    public CustomLocation getCurrentLocation() {
        //final CustomLocation customLocation;
        try {
            if (mhasLocationPermission) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            customLocation = new CustomLocation((Location) task.getResult());
                        } else {
                            customLocation = new CustomLocation();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security issue");
        }
        return customLocation;
    }

    public boolean hasLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show user rationale BEFORE permission box
            if (ActivityCompat.shouldShowRequestPermissionRationale((NavDrawer) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getResources().getString(R.string.PERMISSION_DIALOG_TITLE))
                        .setMessage(getContext().getResources().getString(R.string.PERMISSION_DIALOG_MESSAGE))
                        .setPositiveButton(getContext().getResources().getString(R.string.PERMISSION_DIALOG_POSITIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                // They said yes, so set to true
                                mhasLocationPermission = true;
                            }
                        })
                        .setNegativeButton(getContext().getResources().getString(R.string.PERMISSION_DIALOG_NEGATIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "CustomLocation not available", Toast.LENGTH_SHORT).show();
                                // UserProfileFragment said no, set to false
                                mhasLocationPermission = false;
                            }
                        }).show();
            } else {
                // Request permission
                ActivityCompat.requestPermissions((NavDrawer) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }

            return mhasLocationPermission;

        } else {
            return true;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface LocationHelperCallback {
        void userCurrentLocationUpdated(CustomLocation customLocation);
    }

}
