package com.example.kevinwalker.parkit.users;

import android.location.Location;

import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.maps.UserParkedLocation;
import com.example.kevinwalker.parkit.payments.PaymentType;
import com.example.kevinwalker.parkit.vehicle.Vehicle;

/**
 * Created by hollis on 4/19/18.
 */

public class User {

    private String firstName = "";
    private String lastName = "";
    private String userUUID = "";
    private String userEmail = "";
    private String userPhone = "";
    private float userRating = 0;
    private Address[] address;
    private License license;
    private Badge[] badges;
    private Long userScore;
    private Vehicle[] vehicles;
    private PaymentType[] paymentTypes;
    private boolean isUserParked = false;
//    private CustomLocation userParkedLocation = new CustomLocation(new Location("testParkedLocation"),0);
//    private CustomLocation userCurrentLocation = new CustomLocation(new Location("testCurrentLocation"),0);
    private CustomLocation userParkedLocation;
    private CustomLocation userCurrentLocation;

    public User() {
        userParkedLocation = new CustomLocation(new Location("testParkedLocation"),0);
        userParkedLocation.setLongitude(-79.889510);
        userParkedLocation.setLatitude(36.047091);
        userCurrentLocation = new CustomLocation(new Location("testCurrentLocation"),0);
        userCurrentLocation.setLongitude(-79.889510);
        userCurrentLocation.setLatitude(36.047091);
    }

    public User(boolean isUserParked) {
        this.isUserParked = isUserParked;
    }

    public boolean isUserParked() { return isUserParked; }

    public CustomLocation getUserParkedLocation() {
        return userParkedLocation;
    }

    public void setUserParkedLocation(CustomLocation userParkedLocation) {
        this.userParkedLocation = userParkedLocation;
    }

    public CustomLocation getUserCurrentLocation() {
        return userCurrentLocation;
    }

    public void setUserCurrentLocation(CustomLocation userCurrentLocation) {
        this.userCurrentLocation = userCurrentLocation;
    }

    public void setUserParked(boolean userParked) { isUserParked = userParked; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getUserPhone() { return userPhone; }

    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public float getUserRating() { return userRating; }

    public void setUserRating(float userRating) { this.userRating = userRating; }

    public Address[] getAddress() {
        return address;
    }

    public void setAddress(Address[] address) {
        this.address = address;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Badge[] getBadges() {
        return badges;
    }

    public void setBadges(Badge[] badges) {
        this.badges = badges;
    }

    public Long getUserScore() {
        return userScore;
    }

    public void setUserScore(Long userScore) {
        this.userScore = userScore;
    }

    public Vehicle[] getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    public PaymentType[] getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(PaymentType[] paymentTypes) {
        this.paymentTypes = paymentTypes;
    }


}
