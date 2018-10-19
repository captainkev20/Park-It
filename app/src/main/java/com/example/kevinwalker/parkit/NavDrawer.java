package com.example.kevinwalker.parkit;

import android.content.Intent;
import android.net.Uri;
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
import com.example.kevinwalker.parkit.spot.NewSpotFragment;
import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.spot.SpotFragment;
import com.example.kevinwalker.parkit.spot.SpotListings;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener, MapsFragment.MapsCallBack, SpotListings.SpotListingsInteraction, NewSpotFragment.NewSpotCallback, UserProfileFragment.UserProfileCallback {

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
    private NewSpotFragment newSpotFragment;
    private FrameLayout container;

    private boolean userExists = false;
    private static final String TAG = NavDrawer.class.getName();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static DocumentReference userDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        FirestoreHelper.getInstance().initializeFirestore();
        FirestoreHelper.getInstance().initializeFirestoreSpot();

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
                newSpotFragment = new NewSpotFragment();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.container, newSpotFragment);
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

        initMapFragmentTransaction();
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

    @Override
    public void logOff() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(NavDrawer.this, Login.class));
    }

    public static void saveCurrentUserLocation(CustomLocation currentUserLocation){
        FirestoreHelper.getInstance().getCurrentUser().setUserCurrentLocation(currentUserLocation);
        FirestoreHelper.getInstance().mergeCurrentUserWithFirestore(FirestoreHelper.getInstance().getCurrentUser());
        //FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
    }

    public void saveUserParkedLocation(CustomLocation userParkedLocation, boolean isParked) {
        FirestoreHelper.getInstance().getCurrentUser().setUserParkedLocation(userParkedLocation);
        FirestoreHelper.getInstance().mergeCurrentUserWithFirestore(FirestoreHelper.getInstance().getCurrentUser());
        FirestoreHelper.getInstance().getCurrentUser().setUserParked(isParked);
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
    public void parkedLocationUpdate(CustomLocation userParkedLocation, boolean isParked) {
        saveUserParkedLocation(userParkedLocation, isParked);
    }

    @Override
    public void onSpotListingInteraction(Spot item) {}

    @Override
    public void setFabVisibility(int viewVisibilityConstant) {
        fab.setVisibility(viewVisibilityConstant);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    public boolean isUserExists() {
        return userExists;
    }

    // Relates to UserProfileFragment
    @Override
    public void userUpdated(User user) {
    }
}