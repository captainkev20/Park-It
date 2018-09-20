package com.example.kevinwalker.parkit;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.kevinwalker.parkit.authentication.Login;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.maps.MapsFragment;
import com.example.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.example.kevinwalker.parkit.payments.PaymentFragment;
import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.spot.SpotFragment;
import com.example.kevinwalker.parkit.spot.SpotListings;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener, MapsFragment.MapsCallBack, SpotListings.SpotListingsInteraction, UserProfileFragment.UserProfileCallback {

    private FloatingActionButton fab;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FragmentTransaction userFragmentTransaction;
    private FragmentTransaction paymentFragmentTransaction;
    private FragmentTransaction spotFragmentTransaction;
    private MapsFragment mapFragment;
    private UserProfileFragment userProfileFragment;
    private PaymentFragment paymentFragment;
    private SpotListings spotListingFragment;
    private SpotFragment spotFragment;
    private FrameLayout container;
    private static User currentUser = new User();

    private boolean userExists = false;
    private static final String TAG = NavDrawer.class.getName();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DocumentReference userDocument;

    private Boolean mLocationPermissionStatus = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location androidCurrentLocation = new Location("test");
    private Context mContext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                spotFragment = new SpotFragment();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.container, spotFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        fab.setVisibility(View.GONE);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemTextColor(null);
        navigationView.setItemTextAppearance(R.style.MenuTextStyle);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        setTitle(getResources().getString(R.string.map_nav_title));

        // Get base user information from Login Intent
        final Intent intent = getIntent();
        if (intent != null) {
            currentUser.setUserUUID(intent.getStringExtra(Login.EXTRA_USER));
            currentUser.setUserEmail(intent.getStringExtra(Login.EXTRA_USER_EMAIL));
        }

        // Initialize our User DocumentReference
        userDocument = firebaseFirestore.collection("users").document(currentUser.getUserUUID());

        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser.getUserUUID().trim().isEmpty()) {
                        currentUser.setUserUUID(intent.getStringExtra(Login.EXTRA_USER));
                        mergeCurrentUserWithFirestore(currentUser);
                    }
                    if (currentUser.getUserEmail().trim().isEmpty()) {
                        currentUser.setUserEmail(intent.getStringExtra(Login.EXTRA_USER_EMAIL));
                        mergeCurrentUserWithFirestore(currentUser);
                    }
                } else {
                    mergeCurrentUserWithFirestore(currentUser);
                }
                // Initialize map after getting back status of User in database. Values will be up to date at this point
                initMapFragmentTransaction();
            }
        });
    }

    private void initMapFragmentTransaction() {
        // Set the default fragment to display
        container = findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapsFragment();
        fragmentTransaction.replace(R.id.container, mapFragment);
        fragmentTransaction.commit();
    }

    private void mergeCurrentUserWithFirestore(User currentUser) {
        userDocument.set(currentUser, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showLogOffAlertDialog() {
        LogOffAlertDialogFragment newFragment = new LogOffAlertDialogFragment();
        newFragment.show(getSupportFragmentManager(), "AlertDiaLog");
    }

    private void showGetUserInfoAlertDialog(){

    }

    @Override
    public void logOff() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(NavDrawer.this, Login.class));
    }

    public void saveCurrentUserLocation(CustomLocation currentUserLocation){
        currentUser.setUserCurrentLocation(currentUserLocation);
        mergeCurrentUserWithFirestore(currentUser);
    }

    public void saveUserParkedLocation(CustomLocation userParkedLocation, boolean isParked) {
        currentUser.setUserParkedLocation(userParkedLocation);
        currentUser.setUserParked(isParked);
        mergeCurrentUserWithFirestore(currentUser);
    }

    /*private void fetchCurrentLocation() {
        try {
            if (mLocationPermissionStatus) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            androidCurrentLocation = (Location) location.getResult();

                            CustomLocation customLocation = new CustomLocation(androidCurrentLocation);
                            currentUser.setUserCurrentLocation(customLocation);
                            //mapsCallBack.userLocationUpdate(customLocation);

                            //currentLocationUpdated(new CustomLocation((Location)task.getResult()));
                            //animateCamera(navDrawer.getCurrentUser().getUserCurrentLocation(), DEFAULT_ZOOM, currentAddress);

                            *//*if (mapFirstRun) {
                                currentLocationUpdated(new CustomLocation((Location)task.getResult()));
                                animateCamera(navDrawer.getCurrentUser().getUserCurrentLocation(), DEFAULT_ZOOM, currentAddress);
                            } else {
                                currentLocationUpdated(new CustomLocation((Location)task.getResult()));
                            }*//*
                        } else {
                            Toast.makeText(mContext, "Current location unavailable...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security issue");
        }
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_log_off) {
            showLogOffAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_my_profile) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            userProfileFragment = new UserProfileFragment();
            fragmentTransaction.replace(R.id.container, userProfileFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_listings) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            spotListingFragment = SpotListings.newInstance(1);
            fragmentTransaction.replace(R.id.container, spotListingFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_map) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = new MapsFragment();
            fragmentTransaction.replace(R.id.container, mapFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_payments) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            paymentFragment = new PaymentFragment();
            fragmentTransaction.replace(R.id.container, paymentFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPositiveClick() {

    }

    @Override
    public void onNegativeClick() {

    }

    @Override
    public void userLocationUpdate(CustomLocation location) {
        saveCurrentUserLocation(location);
    }

    @Override
    public void parkedLocationUpdate(CustomLocation userParkedLocation, boolean isParked) {
        saveUserParkedLocation(userParkedLocation, isParked);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        mergeCurrentUserWithFirestore(currentUser);
    }

    @Override
    public void onSpotListingInteraction(Spot item) {

    }

    @Override
    public void setFabVisibility(int viewVisibilityConstant) {
        fab.setVisibility(viewVisibilityConstant);
    }

    public boolean isUserExists() {
        return userExists;
    }

    @Override
    public void userUpdated(User user) {
        setCurrentUser(user);
    }
}