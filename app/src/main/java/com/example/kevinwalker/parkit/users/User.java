package com.example.kevinwalker.parkit.users;

import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.maps.CustomLocation;
import com.example.kevinwalker.parkit.payments.PaymentType;
import com.example.kevinwalker.parkit.vehicle.Vehicle;

import java.util.ArrayList;

public class User {

    private String firstName = "";
    private String lastName = "";
    private String userUUID = "";
    private String userProfilePhotoURL = "";
    private String userEmail = "";
    private String userPhone = "";
    private float userRating = 0;
    private Address[] address;
    private License license;
    private Badge[] badges;
    private Long userScore;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private PaymentType[] paymentTypes;
    private boolean isUserParked = false;
    private CustomLocation userParkedLocation;
    private CustomLocation userCurrentLocation;

    public User() {
        userParkedLocation = new CustomLocation();
        userParkedLocation.setLongitude(-79.889510);
        userParkedLocation.setLatitude(36.047091);
        userCurrentLocation = new CustomLocation();
        userCurrentLocation.setLongitude(-79.889510);
        userCurrentLocation.setLatitude(36.047091);
        vehicles.add(new Vehicle());
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

    public String getUserProfilePhotoURL() { return userProfilePhotoURL; }

    public void setUserProfilePhotoURL(String userProfilePhotoURL) { this.userProfilePhotoURL = userProfilePhotoURL; }

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

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public PaymentType[] getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(PaymentType[] paymentTypes) {
        this.paymentTypes = paymentTypes;
    }


}
