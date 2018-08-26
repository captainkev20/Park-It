package com.example.kevinwalker.parkit.users;

import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.maps.UserCurrentLocation;
import com.example.kevinwalker.parkit.maps.UserParkedLocation;
import com.example.kevinwalker.parkit.payments.PaymentType;
import com.example.kevinwalker.parkit.vehicle.Vehicle;

import java.util.UUID;

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
    private ImageView avatar;
    private Badge[] badges;
    private Long userScore;
    private Vehicle[] vehicles;
    private PaymentType[] paymentTypes;
    private boolean isUserParked = false;
    private UserParkedLocation userParkedLocation = new UserParkedLocation(36.0656975,-79.7860938,0);
    private UserCurrentLocation userCurrentLocation = new UserCurrentLocation(36.0656975,-79.7860938,0);

    public User() {}

    public User(boolean isUserParked) {
        this.isUserParked = isUserParked;
    }

    public UserParkedLocation getUserParkedLocation() { return userParkedLocation; }

    public void setUserParkedLocation(UserParkedLocation userParkedLocation) { this.userParkedLocation = userParkedLocation; }

    public boolean isUserParked() { return isUserParked; }

    public void setUserParked(boolean userParked) { isUserParked = userParked; }

    public UserCurrentLocation getUserCurrentLocation() { return userCurrentLocation; }

    public void setUserCurrentLocation(UserCurrentLocation userCurrentLocation) { this.userCurrentLocation = userCurrentLocation; }

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

    public ImageView getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageView avatar) {
        this.avatar = avatar;
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
