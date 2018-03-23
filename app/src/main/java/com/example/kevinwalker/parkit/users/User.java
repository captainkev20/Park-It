package com.example.kevinwalker.parkit.users;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.kevinwalker.parkit.R;
import com.example.kevinwalker.parkit.vehicle.Vehicle;
import com.example.kevinwalker.parkit.badges.Badge;
import com.example.kevinwalker.parkit.payments.PaymentType;

import java.util.UUID;

public class User extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private UUID userUUID;
    private Address[] address;
    private License license;
    private ImageView avatar;
    private Badge[] badges;
    private Long userScore;
    private Vehicle[] vehicles;
    private PaymentType[] paymentTypes;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UUID getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID;
    }

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


    }
}
