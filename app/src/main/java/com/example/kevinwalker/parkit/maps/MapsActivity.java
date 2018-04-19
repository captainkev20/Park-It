
package com.example.kevinwalker.parkit.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.kevinwalker.parkit.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionStatus = false;
    private GoogleMap map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 19f;
    private Button btn_park;
    private Button btn_leave;
    private LatLng currentLatLng = new LatLng(36.0656975,-79.7860938);
    private String currentAddress = "";
    private Boolean markerVisible = false;
    private Geocoder geocoder;
    private List<Address> addressList;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Boolean isUserParked = false;
    private Marker userMarker;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String SHARED_PREFS_PARKED_LATITUDE_KEY = "parked_latitude";
    private static String SHARED_PREFS_PARKED_LONGITUDE_KEY = "parked_longitude";
    private static String SHARED_PREFS_IS_PARKED_KEY = "is_parked";

    // TODO: Add boolean for current GPS connection status - update using the overidden methods at the bottom of the class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        geocoder = new Geocoder(getApplicationContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btn_park = findViewById(R.id.btn_park);
        btn_park.setOnClickListener(this);
        btn_leave = findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(this);

        initMap();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_park:
                isUserParked = true;
                saveLatlngAsUserParked(currentLatLng);
                moveCamera(getCurrentLatLng(), DEFAULT_ZOOM, currentAddress);
                placeMarkerOnMap(currentLatLng, currentAddress, BitmapDescriptorFactory.fromResource(R.drawable.ic_castle), true);
                btn_park.setEnabled(false);
                btn_leave.setEnabled(true);
                break;

            case R.id.btn_leave:
                isUserParked = false;
                // TODO: Remove only the User's parked marker - do not use map.clear()
                userMarker.remove();
                btn_leave.setEnabled(false);
                btn_park.setEnabled(true);
                break;

        }
    }

    private void saveLatlngAsUserParked(LatLng latLng) {

        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putBoolean(SHARED_PREFS_IS_PARKED_KEY, true);
        editor.putString(SHARED_PREFS_PARKED_LATITUDE_KEY, String.valueOf(latLng.latitude)).apply();
        editor.putString(SHARED_PREFS_PARKED_LONGITUDE_KEY, String.valueOf(latLng.longitude)).apply();
    }

    private boolean isUserParked() {
        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        return sharedPreferences.getBoolean(SHARED_PREFS_IS_PARKED_KEY, false);
    }

    private LatLng getParkedLatlngFromSharedPrefs() {
        sharedPreferences = this.getPreferences(MODE_PRIVATE);

        return new LatLng(Double.parseDouble(sharedPreferences.getString(SHARED_PREFS_PARKED_LATITUDE_KEY, "")), Double.parseDouble(sharedPreferences.getString(SHARED_PREFS_PARKED_LONGITUDE_KEY, "")));
    }

    private void initMap() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show user rationale BEFORE permission box
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Location Permission")
                        .setMessage("Hi there! Our app can't function properly without your location. Will you please grant it?")
                        .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                // They said yes, so set to true
                                mLocationPermissionStatus = true;
                            }
                        })
                        .setNegativeButton("No thanks!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT).show();
                                // User said no, set to false
                                mLocationPermissionStatus = false;
                            }
                        }).show();
            } else {
                // Request permission
                ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Already have permission, so set up map
            mLocationPermissionStatus = true;
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);
        }
    }

    protected void startLocationUpdates() {

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
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };


        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "Moving camera to: ");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            initMap();
                            // TODO: Do not continue the MapsActivity, continue to request permission or force them out of the Activity
                            return;
                        } else {
                            mLocationPermissionStatus = true;
                            initMap();
                        }
                    }
                }
            }
        }
    }

    // TODO: Add or remove arguments to match our needs for the various Marker types we'll be using/ defining
    protected void placeMarkerOnMap(LatLng latLng, String title, BitmapDescriptor bitmapDescriptor, boolean markerVisible) {
        userMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(bitmapDescriptor)
                .visible(markerVisible));

    }

    private String getAddressFromGeocoder(LatLng latLng) {

        String address = "";
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.getStackTrace().toString();
        }

        addressList.clear();

        return address;
    }

    private void setCurrentAddress(String address) {
        currentAddress = address;
    }

    private String getCurrentAddress() {
        return this.currentAddress;
    }

    private LatLng getCurrentLatLng() {
        return this.currentLatLng;
    }


    private void setCurrentLatLng(Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    // Update objects and perform actions as necessary once fetchCurrentLocation task completes
    private void currentLocationUpdated(Location location) {
        setCurrentLatLng(location);
        setCurrentAddress(getAddressFromGeocoder(getCurrentLatLng()));
        moveCamera(getCurrentLatLng(), 19F, "Your current location");
    }

    private void fetchCurrentLocation() {
        try {
            if (mLocationPermissionStatus) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            currentLocationUpdated((Location) task.getResult());
                        } else {
                            Toast.makeText(MapsActivity.this, "Current location unavailable...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security issue");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (isUserParked) {
            placeMarkerOnMap(getParkedLatlngFromSharedPrefs(), currentAddress, BitmapDescriptorFactory.fromResource(R.drawable.ic_castle), true);
        }

        map = googleMap;

        // Checks for permission
        if(mLocationPermissionStatus) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fetchCurrentLocation();

            // Adds blue dot to current location once map is centered on it
            map.setMyLocationEnabled(true);

            // Set the button to be enabled when the map is ready
            btn_park.setEnabled(true);
            //btn_leave.setEnabled(true);
        }
    }

    // TODO: Update user's Marker
    @Override
    public void onLocationChanged(Location location) {
        currentLocationUpdated(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // TODO: Clear 'GPS unavailable' notification
    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // TODO: Notify user that their GPS is unavailable
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // TODO: Pull information from Marker; display to user
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        if (isUserParked) {
            placeMarkerOnMap(getParkedLatlngFromSharedPrefs(), currentAddress, BitmapDescriptorFactory.fromResource(R.drawable.ic_castle), true);
        }
    }
}