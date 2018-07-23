package com.example.kevinwalker.parkit;

import android.content.Intent;
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
import com.example.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.example.kevinwalker.parkit.payments.PaymentFragment;
import com.example.kevinwalker.parkit.spot.SpotFragment;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener {

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
    private static final String TAG = NavDrawer.class.getName();


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

        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("testString").child("I0DSL2PvQIuafqv7r4O4").child("testString");*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        //DatabaseReference testRef = database.getReference("testString");
        DatabaseReference locationReference = database.getReference("location");
        locationReference.setValue(new Location(1234123, 1234323));

        myRef.setValue("KevinWalker");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
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

    @Override
    public void logOff() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(NavDrawer.this, Login.class));
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
}