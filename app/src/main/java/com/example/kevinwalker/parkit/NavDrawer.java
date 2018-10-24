package com.example.kevinwalker.parkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.kevinwalker.parkit.authentication.Login;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.maps.MapsFragment;
import com.example.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.example.kevinwalker.parkit.payments.PaymentFragment;
import com.example.kevinwalker.parkit.spot.NewSpotFragment;
import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.spot.SpotListings;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener,
        MapsFragment.MapsCallBack,
        SpotListings.SpotListingsInteraction,
        NewSpotFragment.NewSpotCallback,
        UserProfileFragment.UserProfileCallback,
        FirestoreHelper.OnDataUpdated {

    private static final String TAG = NavDrawer.class.getName();

    @BindView(R.id.add_spot_fab) FloatingActionButton addSpotFloatingActionButton;
    @BindView(R.id.nav_progress_bar) ProgressBar navProgressBar;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private FrameLayout container;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapsFragment mapFragment;
    private NewSpotFragment newSpotFragment;

    private String currentFragmentTAG = "";

    // TODO: Add FragmentTags

    private boolean userExists = false;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private boolean isUserInitialized = false;
    static DocumentReference userDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirestoreHelper.getInstance(this).initializeFirestore();
        FirestoreHelper.getInstance(this).initializeFirestoreSpot();

        setContentView(R.layout.activity_nav_drawer);

        ButterKnife.bind(this);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        addSpotFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                newSpotFragment = new NewSpotFragment();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.container, newSpotFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        addSpotFloatingActionButton.setVisibility(View.GONE);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemTextColor(null);
        navigationView.setItemTextAppearance(R.style.MenuTextStyle);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        setTitle(getResources().getString(R.string.map_nav_title));

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

        if (isUserInitialized) {
            navProgressBar.setVisibility(View.GONE);
        } else {
            navProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showLogOffAlertDialog() {
        LogOffAlertDialogFragment newFragment = new LogOffAlertDialogFragment();
        newFragment.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void logOff() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(new Intent(NavDrawer.this, Login.class));
        FirestoreHelper.getInstance().setCurrentUser(new User());
    }

    public static void saveCurrentUserLocation(CustomLocation currentUserLocation){
        FirestoreHelper.getInstance().getCurrentUser().setUserCurrentLocation(currentUserLocation);
        //FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
    }

    public void saveUserParkedLocation(CustomLocation userParkedLocation, boolean isParked) {
        FirestoreHelper.getInstance().getCurrentUser().setUserParkedLocation(userParkedLocation);
        //FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
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

        Fragment menuFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_my_profile) {
            menuFragment = new UserProfileFragment();

        } else if (id == R.id.nav_listings) {
            menuFragment = SpotListings.newInstance(1);

        } else if (id == R.id.nav_map) {
            menuFragment = new MapsFragment();

        } else if (id == R.id.nav_payments) {
            menuFragment = new PaymentFragment();

        } else if (id == R.id.nav_settings) {
            // TODO: Replicate for other nav options
//            if (familyFragment == null) {
//                familyFragment = FamilyFragment.newInstance();
//            }
//            setCurrentFragment(familyFragment);
//            fab.show();
//            setTitle(getResources().getString(R.string.family_toolbar_title));
        } else if (id == R.id.nav_about) {

        }

        // TODO: Delete once functionality is replaced
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, menuFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        if (fragmentManager.getFragments().contains(fragment)) {
            if (!fragmentManager.getFragments().isEmpty()) {
                ft.hide(getFragmentForTag());
            }
            setCurrentFragmentTAG(fragment);
            ft.show(fragment).commit();
        } else {
            if (!fragmentManager.getFragments().isEmpty()) {
                ft.hide(getFragmentForTag());
            }
            setCurrentFragmentTAG(fragment);
            ft.add(R.id.container, fragment, currentFragmentTAG).commit();
        }
    }

    private Fragment getFragmentForTag() {
        Fragment fragment = new Fragment();
        switch (currentFragmentTAG) {
            // TODO: Replace with your FragmentTags
//            case homeFragmentTag:
//                fragment = homeFragment;
//                break;
//            default:
//                fragment = homeFragment;
//                break;
        }
        return fragment;
    }

    private void setCurrentFragmentTAG(Fragment fragment) {
        // TODO: Replace with your FragmentTags
//        if (fragment instanceof HomeFragment) {
//            currentFragmentTAG = homeFragmentTag;
//        } else if (fragment instanceof MapsFragment) {
//            currentFragmentTAG = mapsFragmentTag;
//        } else if (fragment instanceof FrontDeskFragment) {
//            currentFragmentTAG = frontDeskFragmentTag;
//        } else if (fragment instanceof FamilyFragment) {
//            currentFragmentTAG = familyFragmentTag;
//        } else if (fragment instanceof QueueFragment) {
//            currentFragmentTAG = queueFragmentTag;
//        } else if (fragment instanceof FamilyMemberFragment) {
//            currentFragmentTAG = familyMemberFragmentTag;
//        } else if (fragment instanceof AddFamilyMemberFragment) {
//            currentFragmentTAG = addFamilyMemberFragmentTag;
//        }
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
        addSpotFloatingActionButton.setVisibility(viewVisibilityConstant);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    public boolean isUserExists() { return userExists; }

    @Override
    public void userUpdated(User user) {

    }

    @Override
    public void onUserUpdated(User user) {
        isUserInitialized = true;
        navProgressBar.setVisibility(View.INVISIBLE);
        if (mapFragment == null) {
            initMapFragmentTransaction();
        }

        if (fragmentManager != null) {
            if (!fragmentManager.getFragments().isEmpty()) {
                if (fragmentManager.getFragments().get(0) instanceof MapsFragment) {
                    ((MapsFragment) fragmentManager.getFragments().get(0)).refreshUI();
                }
            }
        }
    }
}