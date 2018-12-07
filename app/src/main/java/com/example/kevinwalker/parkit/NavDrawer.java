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

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.kevinwalker.parkit.authentication.Login;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.maps.MapsFragment;
import com.example.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.example.kevinwalker.parkit.payments.PaymentFragment;
import com.example.kevinwalker.parkit.spot.NewSpotFragment;
import com.example.kevinwalker.parkit.spot.Spot;
import com.example.kevinwalker.parkit.spot.SpotListingsFragment;
import com.example.kevinwalker.parkit.users.User;
import com.example.kevinwalker.parkit.users.UserProfileFragment;
import com.example.kevinwalker.parkit.utils.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener,
        MapsFragment.MapsCallBack,
        SpotListingsFragment.SpotListingsInteraction,
        NewSpotFragment.NewSpotCallback,
        UserProfileFragment.UserProfileCallback,
        FirestoreHelper.OnDataUpdated {

    private static final String TAG = NavDrawer.class.getName();

    @BindView(R.id.add_spot_fab)
    FloatingActionButton addSpotFloatingActionButton;
    @BindView(R.id.nav_progress_bar)
    ProgressBar navProgressBar;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private FrameLayout container;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private MapsFragment mapFragment;
    private NewSpotFragment newSpotFragment;
    private UserProfileFragment userProfileFragment;
    private SpotListingsFragment spotListingsFragment;
    private PaymentFragment paymentFragment;

    private String currentFragmentTAG = "";
    private static final String mapFragmentTag = "mapFragmentTag";
    private static final String newSpotFragmentTag = "newSpotFragmentTag";
    private static final String userProfileFragmentTag = "userProfileFragmentTag";
    private static final String spotListingFragmentTag = "spotListingFragment";
    private static final String paymentFragmentTag = "paymentFragmentTag";

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
                if (newSpotFragment == null) {
                    newSpotFragment = new NewSpotFragment();
                }
                setFabVisibility(View.GONE);
                setCurrentFragment(newSpotFragment);

                setTitle(getResources().getString(R.string.add_new_spot));
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
        fragmentTransaction.commit();
        mapFragment = new MapsFragment();
        setCurrentFragment(mapFragment);
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

    public static void saveCurrentUserLocation(CustomLocation currentUserLocation) {
        FirestoreHelper.getInstance().getCurrentUser().setUserCurrentLocation(currentUserLocation);
        FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
    }

    public void saveUserParkedLocation(CustomLocation userParkedLocation, boolean isParked) {
        FirestoreHelper.getInstance().getCurrentUser().setUserParkedLocation(userParkedLocation);
        FirestoreHelper.getInstance().getCurrentUser().setUserParked(isParked);
        FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            setFabVisibility(View.GONE);
            setTitle(R.string.map_nav_title);
            switch (currentFragmentTAG) {
                case userProfileFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case spotListingFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case paymentFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                default:
                    setCurrentFragment(mapFragment);
                    break;
            }
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
            if (userProfileFragment == null) {
                userProfileFragment = new UserProfileFragment();
            }
            setCurrentFragment(userProfileFragment);
            setFabVisibility(View.GONE);
            setTitle(getResources().getString(R.string.profile_nav_title));

        } else if (id == R.id.nav_listings) {
            if (spotListingsFragment == null) {
                spotListingsFragment = SpotListingsFragment.newInstance(1);
            }
            setCurrentFragment(spotListingsFragment);
            setFabVisibility(View.VISIBLE);
            setTitle(getResources().getString(R.string.listings_nav_title));

        } else if (id == R.id.nav_map) {
            if (mapFragment == null) {
                mapFragment = new MapsFragment();
            }
            setCurrentFragment(mapFragment);
            setFabVisibility(View.GONE);
            setTitle(getResources().getString(R.string.map_nav_title));

        } else if (id == R.id.nav_payments) {
            if (paymentFragment == null) {
                paymentFragment = new PaymentFragment();
            }
            setCurrentFragment(paymentFragment);
            setFabVisibility(View.GONE);
            setTitle(getResources().getString(R.string.payments_nav_title));

        } else if (id == R.id.nav_settings) {
            // TODO: Replicate for other nav options

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        // If fragment attempting to show already been instantiated AND other fragments that have been displayed
        if (fragmentManager.getFragments().contains(fragment)) {
            // Quit process if user clicks on fragment that has already been displayed
            if (currentFragmentTAG.equals(getTagForFragment(fragment))) {
                return;
            }
            // If not empty, a fragment exists
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
        Fragment fragment;
        switch (currentFragmentTAG) {
            case mapFragmentTag:
                fragment = mapFragment;
                break;
            case newSpotFragmentTag:
                fragment = newSpotFragment;
                break;
            case userProfileFragmentTag:
                fragment = userProfileFragment;
                break;
            case spotListingFragmentTag:
                fragment = spotListingsFragment;
                break;
            case paymentFragmentTag:
                fragment = paymentFragment;
                break;
            default:
                fragment = mapFragment;
                break;
        }
        return fragment;
    }

    private String getTagForFragment(Fragment fragment) {
        String fragmentTag = "";
        if (fragment instanceof MapsFragment) {
            fragmentTag = mapFragmentTag;
        } else if (fragment instanceof NewSpotFragment) {
            fragmentTag = newSpotFragmentTag;
        } else if (fragment instanceof UserProfileFragment) {
            fragmentTag = userProfileFragmentTag;
        } else if (fragment instanceof SpotListingsFragment) {
            fragmentTag = spotListingFragmentTag;
        } else if (fragment instanceof PaymentFragment) {
            fragmentTag = paymentFragmentTag;
        }

        return fragmentTag;
    }

    private void setCurrentFragmentTAG(Fragment fragment) {
        if (fragment instanceof MapsFragment) {
            currentFragmentTAG = mapFragmentTag;
        } else if (fragment instanceof NewSpotFragment) {
            currentFragmentTAG = newSpotFragmentTag;
        } else if (fragment instanceof UserProfileFragment) {
            currentFragmentTAG = userProfileFragmentTag;
        } else if (fragment instanceof SpotListingsFragment) {
            currentFragmentTAG = spotListingFragmentTag;
        } else if (fragment instanceof PaymentFragment) {
            currentFragmentTAG = paymentFragmentTag;
        }
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
    public void onSpotListingInteraction(Spot item) {
    }

    @Override
    public void setFabVisibility(int viewVisibilityConstant) {
        addSpotFloatingActionButton.setVisibility(viewVisibilityConstant);
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    public void navigateToSpotListings() {
        // Checks fragment state and resets Recycler View to show added spot
        if (spotListingsFragment == null) {
            spotListingsFragment = new SpotListingsFragment();
            spotListingsFragment.resetRecyclerView();
        } else {
            spotListingsFragment = new SpotListingsFragment();
        }
        setCurrentFragment(spotListingsFragment);
    }

    @Override
    public void onAllSpotsUpdated(ArrayList<Spot> spots) {
        spotListingsFragment.resetRecyclerView(spots);
    }

    public boolean isUserExists() {
        return userExists;
    }

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
                    mapFragment.refreshUI();
                }
            }
        }
    }
}