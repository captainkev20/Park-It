package com.example.kevinwalker.parkit.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.users.User;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapsFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final String TAG = MapsFragment.class.getName();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionStatus = false;
    private GoogleMap map;
    private Boolean mapFirstRun;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 19f;
    private Button btn_park;
    private Button btn_leave;
    private FloatingActionButton btn_find_user_parked;
    private LatLng currentLatLng = new LatLng(36.0656975,-79.7860938); // Initialized to City View
    private LatLng parkedLatLng = new LatLng(0,0); // Initialized to middle of map
    private UserParkedLocation userParkedLocation = new UserParkedLocation(36.0656975, -79.7860938, 0);
    private String currentAddress = "";
    private Boolean markerVisible = false;
    private Geocoder geocoder;
    private List<Address> addressList = new ArrayList<>();
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Boolean isUserParked = true;
    private Marker userMarker;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private SharedPreferences sharedPreferences;
    private static String SHARED_PREFS_PARKED_LATITUDE_KEY = "parked_latitude";
    private static String SHARED_PREFS_PARKED_LONGITUDE_KEY = "parked_longitude";
    private static String SHARED_PREFS_IS_PARKED_KEY = "is_parked";
    private static String USER_PARKED_LOCATION_KEY = "parked_location";
    private SupportMapFragment mapFragment;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDatabaseReference = database.getReference("users");
    private User currentUser = new User();
    private MapView mapView;
    private View mView;
    private static final String EPOCH_TIME = "fragment_timestamp";
    private static final String SAVED_LOCATION = "saved_location";
    private long epochTimeStamp;
    private Boolean shouldRefreshMap = true;
    private Location androidCurrentLocation = new Location("test");
    private UserCurrentLocation pojoCurrentLocation = new UserCurrentLocation(36.0656975, -79.7860938,0);
    private MapsFragment.MapsCallBack mapsCallBack;
    private Context mContext;

    // TODO: Add boolean for current GPS connection status - update using the overidden methods at the bottom of the class

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchCurrentLocation();
        mapsCallBack = this.mapsCallBack;

        userDatabaseReference = userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        geocoder = new Geocoder(getActivity().getApplicationContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_maps, container, false);
        if(savedInstanceState!=null) {
            androidCurrentLocation = (Location) savedInstanceState.get(SAVED_LOCATION);
        }

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapsFragment.MapsCallBack) {
            mapsCallBack = (MapsFragment.MapsCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MapsFragment.MapsCallBack");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Create views here. Best to do here as by this point view will be properly created and inflated
        btn_park = getView().findViewById(R.id.btn_park);
        btn_park.setOnClickListener(this);
        btn_leave = getView().findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(this);
        btn_find_user_parked = getView().findViewById(R.id.btn_find_user_parked);
        btn_find_user_parked.setOnClickListener(this);
        mapView = mView.findViewById(R.id.map);
        getActivity().setTitle(getResources().getString(R.string.map_nav_title));

        if(isUserParked) {
            btn_find_user_parked.setEnabled(true);
            loadUserParkingDataFromFirebase();
        } else {
            Log.i(TAG, "fromOnCreate");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mapView != null) {
            initMap(mapView);
            startLocationUpdates();
            //loadUserParkingDataFromFirebase();
        }

        if (savedInstanceState!= null) {
            epochTimeStamp = savedInstanceState.getLong(EPOCH_TIME);
            Log.i(TAG, String.valueOf(epochTimeStamp));
        }

        UserCurrentLocation tempLocation = new UserCurrentLocation();
        tempLocation.setLatitude(androidCurrentLocation.getLatitude());
        tempLocation.setLongitude(androidCurrentLocation.getLongitude());
        mapsCallBack.locationUpdate(tempLocation);
    }

    private void saveEpochTimeToBundle(Bundle outState) {
        long epochTime;
        Date myDate = new Date();
        epochTime = myDate.getTime();
        Log.i(TAG, String.valueOf(epochTime));
        outState.putLong(EPOCH_TIME, epochTime);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveEpochTimeToBundle(outState);
        outState.putParcelable(SAVED_LOCATION, androidCurrentLocation);
        Log.i(TAG, "onSaveInstanceState");
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_park:

                if (mLocationPermissionStatus) {
                    isUserParked = true;
                    Log.i(TAG, String.valueOf(isUserParked));
                    saveUserParkedLocation(new UserParkedLocation(pojoCurrentLocation.getLatitude(), pojoCurrentLocation.getLongitude(), 0));
                    animateCamera(pojoCurrentLocation, DEFAULT_ZOOM, currentAddress);

                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.ic_marker);

                    placeMarkerOnMap(pojoCurrentLocation, currentAddress, drawable, true);
                    btn_park.setEnabled(false);
                    btn_leave.setEnabled(true);
                    btn_find_user_parked.setEnabled(true);
                    break;

                } else {
                    Toast.makeText(getActivity(), "UserCurrentLocation not available", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    mLocationPermissionStatus = false;
                }

                break;

            case R.id.btn_leave:

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getResources().getString(R.string.LEAVE_DIALOG_TITLE))
                        .setMessage(getResources().getString(R.string.LEAVE_DIALOG_MESSAGE))
                        .setNegativeButton(getResources().getString(R.string.LEAVE_DIALOG_NEGATIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                                }})
                        .setPositiveButton(getResources().getString(R.string.LEAVE_DIALOG_POSITIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                // If user clicks yes, call to leaveSpace to clear data and marker
                                leaveSpace();
                            }
                        }).show();

                break;

            case R.id.btn_find_user_parked:
                loadUserParkingDataFromFirebase();
                animateCamera(pojoCurrentLocation, DEFAULT_ZOOM, currentAddress);
                break;
        }
    }

    private void saveUserCurrentLocation(UserCurrentLocation currentUserLocation) {
        mapsCallBack.locationUpdate(currentUserLocation);

    }

    private void saveUserParkedLocation(UserParkedLocation userParkedLocation) {
        mapsCallBack.parkedLocation(userParkedLocation);
    }

    private void loadUserParkingDataFromFirebase() {

        if (isUserParked) {
            userParkedLocation = currentUser.getUserParkedLocation();
        }

    }

    private void initMap(MapView mapFragment) {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show user rationale BEFORE permission box
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.PERMISSION_DIALOG_TITLE))
                        .setMessage(getResources().getString(R.string.PERMISSION_DIALOG_MESSAGE))
                        .setPositiveButton(getResources().getString(R.string.PERMISSION_DIALOG_POSITIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                // They said yes, so set to true
                                mLocationPermissionStatus = true;
                                fetchCurrentLocation();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.PERMISSION_DIALOG_NEGATIVE_BUTTON_TEXT), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "UserCurrentLocation not available", Toast.LENGTH_SHORT).show();
                                // UserProfileFragment said no, set to false
                                mLocationPermissionStatus = false;
                            }
                        }).show();
            } else {
                // Request permission
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Already have permission, so set up map
            mLocationPermissionStatus = true;
            mapFragment.onCreate(null);
            mapFragment.onResume();
            mapFragment.getMapAsync(this);     
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        if (ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper());
        }
    }

    private void animateCamera(UserCurrentLocation currentUserLocation, float zoom, String title) {
        Log.d(TAG, "Moving camera to: ");
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pojoCurrentLocation.getLatitude(), pojoCurrentLocation.getLongitude()), zoom));
    }

    private boolean shouldRefreshMap() {
        return true;
    }

    private boolean isLocationAccurate(FusedLocationProviderClient mFusedLocationProviderClient) {

        try {
            if (mLocationPermissionStatus) {
                final Task determineLocationAccuracy = mFusedLocationProviderClient.getLastLocation();
                determineLocationAccuracy.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            androidCurrentLocation = (Location) determineLocationAccuracy.getResult();
                            if (mapFirstRun) {

                                } else {
                                }
                            } else {
                                Toast.makeText(getActivity(), "Current location unavailable...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (SecurityException e) {
                Log.e(TAG, "Security issue");
            }
            return true;
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            initMap(mapView);
                            // TODO: Do not continue the MapsFragment, continue to request permission or force them out of the Activity
                            return;
                        } else {
                            mLocationPermissionStatus = true;
                            initMap(mapView);
                        }
                    }
                }
            }
        }
    }

    // TODO: Add or remove arguments to match our needs for the various Marker types we'll be using/ defining
    protected void placeMarkerOnMap(UserCurrentLocation userCurrentLocation, String title, Drawable drawable, boolean markerVisible) {
        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(pojoCurrentLocation.getLatitude(), pojoCurrentLocation.getLongitude()))
                .title(title)
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_marker))
                .visible(markerVisible));

        setMarkerBounce(userMarker);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void setMarkerBounce(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/duration), 0);
                marker.setAnchor(0.5f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
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


    private void setCurrentLatLng(UserCurrentLocation location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    // Update objects and perform actions as necessary once fetchCurrentLocation task completes
    private void currentLocationUpdated(UserCurrentLocation location) {
        setCurrentLatLng(location);
        setCurrentAddress(getAddressFromGeocoder(getCurrentLatLng()));
        mapsCallBack.locationUpdate(location);
    }

    // Method to convert Android Location to custom POJO UserCurrentLocation object for use through program
    private UserCurrentLocation convertAndroidLocationToPojo(Location androidLocation) {

        if (androidLocation != null) {
            return new UserCurrentLocation(androidLocation.getLatitude(), androidLocation.getLongitude(), 0);
        } else {
            return new UserCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        try {
            if (mLocationPermissionStatus) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            androidCurrentLocation = (Location) location.getResult();


                            pojoCurrentLocation = convertAndroidLocationToPojo(androidCurrentLocation);

                            if (mapFirstRun) {
                                currentLocationUpdated(convertAndroidLocationToPojo((Location)task.getResult()));
                                animateCamera(pojoCurrentLocation, DEFAULT_ZOOM, currentAddress);
                            } else {
                                currentLocationUpdated(convertAndroidLocationToPojo((Location)task.getResult()));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Current location unavailable...", Toast.LENGTH_SHORT).show();
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

        MapsInitializer.initialize(mContext);

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapFirstRun = true;


        if (isUserParked) {

            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.ic_marker);
            placeMarkerOnMap(pojoCurrentLocation, currentAddress, drawable, true);
        } else {
            animateCamera(pojoCurrentLocation, DEFAULT_ZOOM, currentAddress);
            Log.i(TAG, String.valueOf(isUserParked));
        }

        // Checks for permission
        if(mLocationPermissionStatus) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fetchCurrentLocation();

            // Adds blue dot to current location once map is centered on it
            map.setMyLocationEnabled(true);

            // Set the button to be enabled when the map is ready
            btn_park.setEnabled(true);
        }
    }

    // TODO: Update user's Marker
    @Override
    public void onLocationChanged(Location location) {
        currentLocationUpdated(pojoCurrentLocation);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, String.valueOf(epochTimeStamp));

        if (isUserParked) {
            Log.i(TAG, String.valueOf(isUserParked));
            loadUserParkingDataFromFirebase();
            btn_leave.setEnabled(true);
            btn_park.setEnabled(false);
            btn_find_user_parked.setEnabled(true);
        } else {
            btn_park.setEnabled(true);
            btn_leave.setEnabled(false);
        }
    }

    public void leaveSpace() {
        isUserParked = false;
        userMarker.remove();
        btn_leave.setEnabled(false);
        btn_park.setEnabled(true);
        btn_find_user_parked.setEnabled(false);
    }

    public interface MapsCallBack {
        void locationUpdate(UserCurrentLocation location);

        void parkedLocation(UserParkedLocation userParkedLocation);
    }
}