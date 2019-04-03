package com.rocks.kevinwalker.parkit;

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
import de.hdodenhof.circleimageview.CircleImageView;

import com.rocks.kevinwalker.parkit.about.AboutFragment;
import com.rocks.kevinwalker.parkit.authentication.Login;
import com.rocks.kevinwalker.parkit.maps.CustomLocation;
import com.rocks.kevinwalker.parkit.maps.MapsFragment;
import com.rocks.kevinwalker.parkit.notifications.LogOffAlertDialogFragment;
import com.rocks.kevinwalker.parkit.payments.NewPaymentFragment;
import com.rocks.kevinwalker.parkit.payments.Payment;
import com.rocks.kevinwalker.parkit.payments.PaymentListingsFragment;
import com.rocks.kevinwalker.parkit.spot.NewSpotFragment;
import com.rocks.kevinwalker.parkit.spot.Spot;
import com.rocks.kevinwalker.parkit.spot.SpotListingsFragment;
import com.rocks.kevinwalker.parkit.users.User;
import com.rocks.kevinwalker.parkit.users.UserProfileFragment;
import com.rocks.kevinwalker.parkit.utils.FirestoreHelper;
import com.rocks.kevinwalker.parkit.utils.LocationHelper;
import com.rocks.kevinwalker.parkit.vehicle.NewVehicleFragment;
import com.rocks.kevinwalker.parkit.vehicle.Vehicle;
import com.rocks.kevinwalker.parkit.vehicle.VehicleListingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LogOffAlertDialogFragment.AlertDialogFragmentInteractionListener,
        MapsFragment.MapsCallBack,
        SpotListingsFragment.SpotListingsInteraction,
        VehicleListingFragment.VehicleListingsInteraction,
        NewSpotFragment.NewSpotCallback,
        NewVehicleFragment.NewVehicleCallback,
        NewPaymentFragment.NewPaymentCallback,
        UserProfileFragment.UserProfileCallback,
        PaymentListingsFragment.PaymentListingsInteraction,
        FirestoreHelper.OnDataUpdated, View.OnClickListener {

    private static final String TAG = NavDrawer.class.getName();

    @BindView(R.id.add_spot_fab) FloatingActionButton addSpotFloatingActionButton;
    @BindView(R.id.add_vehicle_fab) FloatingActionButton addVehicleFloatingActionButton;
    @BindView(R.id.add_payment_fab) FloatingActionButton addPaymentFloatingActionButton;
    @BindView(R.id.nav_progress_bar) ProgressBar navProgressBar;
    CircleImageView navHeaderProfilePicture;

    protected DrawerLayout drawer;
    protected Toolbar toolbar;
    private FrameLayout container;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private MapsFragment mapFragment;
    private NewSpotFragment newSpotFragment;
    private UserProfileFragment userProfileFragment;
    private SpotListingsFragment spotListingsFragment;
    private NewPaymentFragment newPaymentFragment;
    private VehicleListingFragment vehicleListingFragment;
    private PaymentListingsFragment paymentListingsFragment;
    private NewVehicleFragment newVehicleFragment;
    private AboutFragment aboutFragment;

    private String currentFragmentTAG = "";
    private static final String mapFragmentTag = "mapFragmentTag";
    private static final String newSpotFragmentTag = "newSpotFragmentTag";
    private static final String newVehicleFragmentTag = "newVehicleFragmentTag";
    private static final String userProfileFragmentTag = "userProfileFragmentTag";
    private static final String spotListingFragmentTag = "spotListingFragment";
    private static final String newPaymentFragmentTag = "newPaymentFragmentTag";
    private static final String vehicleListingFragmentTag = "vehicleListingFragmentTag";
    private static final String paymentListingFragmentTag = "paymentListingFragmentTag";
    private static final String aboutFragmentTag = "aboutFragmentTag";

    private boolean userExists = false;
    private boolean isUserInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirestoreHelper.getInstance(this);

        if (FirestoreHelper.getInstance().getCurrentUser() != null) {
            FirestoreHelper.getInstance(this).initializeFirestore();
            FirestoreHelper.getInstance(this).initializeFirestoreSpot();
            FirestoreHelper.getInstance(this).initializeFirestoreVehicle();
            FirestoreHelper.getInstance(this).getUserNavProfileHeaderFromFirebase();
        }

        setContentView(R.layout.activity_nav_drawer);

        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        addSpotFloatingActionButton.setOnClickListener(this);
        addVehicleFloatingActionButton.setOnClickListener(this);
        addPaymentFloatingActionButton.setOnClickListener(this);

        addSpotFloatingActionButton.setVisibility(View.GONE);
        addVehicleFloatingActionButton.setVisibility(View.GONE);
        addPaymentFloatingActionButton.setVisibility(View.GONE);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        navHeaderProfilePicture = navHeader.findViewById(R.id.image_logo);

        navigationView.setItemTextColor(null);
        navigationView.setItemIconTintList(null);
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

    @Override
    protected  void onDestroy() { super.onDestroy(); }

    private void showLogOffAlertDialog() {
        LogOffAlertDialogFragment newFragment = new LogOffAlertDialogFragment();
        newFragment.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void logOff() {
        FirebaseAuth.getInstance().signOut();
        FirestoreHelper.logOff();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(new Intent(NavDrawer.this, Login.class));
    }

    public static void saveCurrentUserLocation(CustomLocation currentUserLocation) {
        if (FirestoreHelper.getInstance().getCurrentUser() != null) {
            FirestoreHelper.getInstance().getCurrentUser().setUserCurrentLocation(currentUserLocation);
            FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
        }
    }

    public void saveUserParkedLocation(CustomLocation userParkedLocation, boolean isParked) {
        if (FirestoreHelper.getInstance().getCurrentUser() != null) {
            FirestoreHelper.getInstance().getCurrentUser().setUserParkedLocation(userParkedLocation);
            FirestoreHelper.getInstance().getCurrentUser().setUserParked(isParked);
            FirestoreHelper.getInstance().mergeCurrentUserWithFirestore();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            setSpotFabVisibility(View.GONE);
            setVehicleFabVisibility(View.GONE);
            setTitle(R.string.map_nav_title);
            switch (currentFragmentTAG) {
                case userProfileFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case spotListingFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case newPaymentFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case vehicleListingFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case paymentListingFragmentTag:
                    setCurrentFragment(mapFragment);
                    break;
                case aboutFragmentTag:
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
            return true;
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
            addSpotFloatingActionButton.setVisibility(View.GONE);
            addVehicleFloatingActionButton.setVisibility(View.GONE);
            addPaymentFloatingActionButton.setVisibility(View.GONE);
            setTitle(getResources().getString(R.string.profile_nav_title));

        } else if (id == R.id.nav_listings) {
            if (spotListingsFragment == null) {
                spotListingsFragment = SpotListingsFragment.newInstance(1);
            }
            setCurrentFragment(spotListingsFragment);
            addSpotFloatingActionButton.setVisibility(View.VISIBLE);
            addVehicleFloatingActionButton.setVisibility(View.GONE);
            addPaymentFloatingActionButton.setVisibility(View.GONE);
            setTitle(getResources().getString(R.string.listings_nav_title));

        } else if (id == R.id.nav_map) {
            if (mapFragment == null) {
                mapFragment = new MapsFragment();
            }
            setCurrentFragment(mapFragment);
            addSpotFloatingActionButton.setVisibility(View.GONE);
            addVehicleFloatingActionButton.setVisibility(View.GONE);
            addPaymentFloatingActionButton.setVisibility(View.GONE);
            setTitle(getResources().getString(R.string.map_nav_title));

        } else if (id == R.id.nav_payments) {
            if (paymentListingsFragment == null) {
                paymentListingsFragment = new PaymentListingsFragment();
            }
            setCurrentFragment(paymentListingsFragment);
            addSpotFloatingActionButton.setVisibility(View.GONE);
            addVehicleFloatingActionButton.setVisibility(View.GONE);
            addPaymentFloatingActionButton.setVisibility(View.VISIBLE);
            setTitle(getResources().getString(R.string.payments_nav_title));

        } else if (id == R.id.nav_vehicles) {
            if (vehicleListingFragment == null) {
                vehicleListingFragment = new VehicleListingFragment();
            }
            setCurrentFragment(vehicleListingFragment);
            addVehicleFloatingActionButton.setVisibility(View.VISIBLE);
            addPaymentFloatingActionButton.setVisibility(View.GONE);
            setTitle(getResources().getString(R.string.vehicles_nav_title));

        } else if (id == R.id.nav_about) {
            if (aboutFragment == null) {
                aboutFragment = new AboutFragment();
            }
            setCurrentFragment(aboutFragment);
            addVehicleFloatingActionButton.setVisibility(View.GONE);
            addPaymentFloatingActionButton.setVisibility(View.GONE);
            addSpotFloatingActionButton.setVisibility(View.GONE);
            setTitle(getResources().getString(R.string.about_nav_title));
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
            case newPaymentFragmentTag:
                fragment = newPaymentFragment;
                break;
            case vehicleListingFragmentTag:
                fragment = vehicleListingFragment;
                break;
            case newVehicleFragmentTag:
                fragment = newVehicleFragment;
                break;
            case paymentListingFragmentTag:
                fragment = paymentListingsFragment;
                break;
            case aboutFragmentTag:
                fragment = aboutFragment;
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
        } else if (fragment instanceof NewPaymentFragment) {
            fragmentTag = newPaymentFragmentTag;
        } else if (fragment instanceof VehicleListingFragment) {
            fragmentTag = vehicleListingFragmentTag;
        } else if (fragment instanceof  NewVehicleFragment) {
            fragmentTag = newVehicleFragmentTag;
        } else if (fragment instanceof  PaymentListingsFragment) {
            fragmentTag = paymentListingFragmentTag;
        } else if (fragment instanceof AboutFragment) {
            fragmentTag = aboutFragmentTag;
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
        } else if (fragment instanceof NewPaymentFragment) {
            currentFragmentTAG = newPaymentFragmentTag;
        } else if (fragment instanceof VehicleListingFragment) {
            currentFragmentTAG = vehicleListingFragmentTag;
        } else if (fragment instanceof NewVehicleFragment) {
            currentFragmentTAG = newVehicleFragmentTag;
        } else if (fragment instanceof PaymentListingsFragment) {
            currentFragmentTAG = paymentListingFragmentTag;
        } else if (fragment instanceof AboutFragment) {
            currentFragmentTAG = aboutFragmentTag;
        }
    }

    @Override
    public void onPositiveClick() {
        //FirestoreHelper.logOff();
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
    public void onVehicleListingInteraction(Vehicle item) {

    }

    @Override
    public void onPaymentListingInteraction(Payment item) {

    }

    @Override
    public void setSpotFabVisibility(int viewVisibilityConstant) {
        if (getCurrentFragmentTag().equals(spotListingFragmentTag)) {
            addSpotFloatingActionButton.setVisibility(viewVisibilityConstant);
        }
    }

    @Override
    public void setVehicleFabVisibility(int viewVisibilityConstant) {
        if (getCurrentFragmentTag().equals(vehicleListingFragmentTag))
        addVehicleFloatingActionButton.setVisibility(viewVisibilityConstant);
    }

    @Override
    public void setPaymentFabVisibility(int viewVisibilityConstant) {
        if (getCurrentFragmentTag().equals(paymentListingFragmentTag)) {
            addPaymentFloatingActionButton.setVisibility(viewVisibilityConstant);
        }
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
        addSpotFloatingActionButton.setVisibility(View.VISIBLE);
        setCurrentFragment(spotListingsFragment);
    }

    @Override
    public void navigateToVehicleListings() {
        // Checks fragment state and resets Recycler View to show added vehicle
        if (vehicleListingFragment == null) {
            vehicleListingFragment = new VehicleListingFragment();
            vehicleListingFragment.resetRecyclerView();
        } else {
            vehicleListingFragment = new VehicleListingFragment();
        }
        addVehicleFloatingActionButton.setVisibility(View.VISIBLE);
        setCurrentFragment(vehicleListingFragment);
    }

    @Override
    public void navigateToPaymentListings() {

        if (paymentListingsFragment == null) {
            paymentListingsFragment = new PaymentListingsFragment();
            paymentListingsFragment.resetRecyclerView();
        } else {
            paymentListingsFragment = new PaymentListingsFragment();
        }
        addPaymentFloatingActionButton.setVisibility(View.VISIBLE);
        setCurrentFragment(paymentListingsFragment);
    }

    @Override
    public void onAllSpotsUpdated(ArrayList<Spot> spots) {
        spotListingsFragment.resetRecyclerView(spots);
    }

    @Override
    public void onAllMapSpotsUpdated(ArrayList<Spot> mapSpots) {
        mapFragment.placeSpotsOnMap(mapSpots);
    }

    @Override
    public void onAllVehiclesUpdated(ArrayList<Vehicle> vehicles) {
        vehicleListingFragment.resetRecyclerView(vehicles);
    }

    @Override
    public void onAllPaymentsUpdated(ArrayList<Payment> payments) {
        paymentListingsFragment.resetRecyclerView(payments);
    }

    public boolean isUserExists() {
        return userExists;
    }

    private void updateNavDrawerHeaderProfilePicture() {
    }

    private void hideAllFABs() {
        addSpotFloatingActionButton.setVisibility(View.GONE);
        addVehicleFloatingActionButton.setVisibility(View.GONE);
        addPaymentFloatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void userUpdated(User user) {

    }

    @Override
    public void profilePictureUpdated(Uri filePath) {
        userProfileFragment.updateUserProfilePicture(filePath);
    }

    @Override
    public void navHeaderProfilePictureUpdated(Uri filePath) {
        Picasso.get().load(filePath).centerCrop().resize(145,125).rotate(90).into(navHeaderProfilePicture);
    }

    // Needed to be able to determine current fragment and show/hide add spot button accordingly
    public String getCurrentFragmentTag() {
        return currentFragmentTAG;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // TODO: Ask Hollis why isUserInitalized is creating problem
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_spot_fab:
                if (newSpotFragment == null) {
                    newSpotFragment = new NewSpotFragment();
                }

                setCurrentFragment(newSpotFragment);

                hideAllFABs();

                setTitle(getResources().getString(R.string.add_new_spot));
                break;

            case R.id.add_vehicle_fab:
                if (newVehicleFragment == null) {
                    newVehicleFragment = new NewVehicleFragment();
                }

                hideAllFABs();

                setCurrentFragment(newVehicleFragment);

                setTitle(getResources().getString(R.string.add_new_vehicle));
                break;

            case R.id.add_payment_fab:
                if (newPaymentFragment == null) {
                    newPaymentFragment = new NewPaymentFragment();
                }

                hideAllFABs();

                setCurrentFragment(newPaymentFragment);

                setTitle(getResources().getString(R.string.payments_add_new_title));
                break;
        }
    }
}