package com.example.kevinwalker.parkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.FrameLayout;

import com.example.kevinwalker.parkit.authentication.Login;
import com.example.kevinwalker.parkit.maps.MapsFragment;
import com.example.kevinwalker.parkit.maps.UserCurrentLocation;
import com.example.kevinwalker.parkit.maps.UserParkedLocation;
import com.example.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.example.kevinwalker.parkit.payments.PaymentFragment;
import com.example.kevinwalker.parkit.spot.SpotFragment;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener, MapsFragment.MapsCallBack {

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
    private SpotFragment spotFragment;
    private FrameLayout container;
    private User currentUser;
    private boolean userExists = false;
    private static final String TAG = NavDrawer.class.getName();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDatabaseReference = database.getReference();

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemTextColor(null);
        navigationView.setItemTextAppearance(R.style.MenuTextStyle);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        Intent intent = getIntent();
        if (intent != null) {
            currentUser = new User();
            currentUser.setUserUUID(intent.getStringExtra(Login.EXTRA_USER));
            currentUser.setUserEmail(intent.getStringExtra(Login.EXTRA_USER_EMAIL));
        }

        userDatabaseReference = database.getReference("users").child(currentUser.getUserUUID());


        // Set the default fragment to display
        container = findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        userFragmentTransaction = fragmentManager.beginTransaction();
        paymentFragmentTransaction = fragmentManager.beginTransaction();
        spotFragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapsFragment();

        userProfileFragment = new UserProfileFragment();
        paymentFragment = new PaymentFragment();
        spotFragment = new SpotFragment();
        fragmentTransaction.replace(R.id.container, mapFragment);
        fragmentTransaction.commit();
        userFragmentTransaction.commit();
        paymentFragmentTransaction.commit();
        spotFragmentTransaction.commit();


        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 //Log.i(TAG, dataSnapshot.getValue().toString());

                    if (dataSnapshot.getValue(User.class) == null) {
                        userDatabaseReference.setValue(currentUser);
                    } else {
                        userExists = true;
                        currentUser = dataSnapshot.getValue(User.class);
                    }
                    //Log.i(TAG, currentUser.getUserUUID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Failed to write");
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

    public void saveCurrentUserLocation(UserCurrentLocation currentUserLocation){
        // Check to verify a user exists. If there is no user, we don't want to do location updates
        if(userExists) {
            currentUser.setUserCurrentLocation(currentUserLocation);
            userDatabaseReference.setValue(currentUser);
        }
    }

    public void saveUserParkedLocation(UserParkedLocation userParkedLocation) {
        // Check to verify a user exists. If there is no user, we don't want to do location updates
        if(userExists) {
            currentUser.setUserParkedLocation(userParkedLocation);
            userDatabaseReference.setValue(currentUser);
        }
    }


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
            fragmentTransaction.replace(R.id.container, userProfileFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_listings) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, spotFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_map) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, mapFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_payments) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
    public void locationUpdate(UserCurrentLocation location) {
        saveCurrentUserLocation(location);
    }

    @Override
    public void parkedLocation(UserParkedLocation userParkedLocation) {
        saveUserParkedLocation(userParkedLocation);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}