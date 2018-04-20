package com.example.kevinwalker.parkit.spot;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by hollis on 4/19/18.
 */

public class Spot {

    private LatLng latLng;
    private String address;
    private boolean occupied;
    private boolean userOccupied;
    private Date lastAccessed;

    public Spot() {};

    public Spot(LatLng latLng) {
        this.latLng = latLng;
    }

    public Spot(LatLng latLng, String address, boolean occupied, boolean userOccupied, Date lastAccessed) {
        this.latLng = latLng;
        this.address = address;
        this.occupied = occupied;
        this.userOccupied = userOccupied;
        this.lastAccessed = lastAccessed;
    }

    public LatLng getSpotLatlng() {
        return latLng;
    }

    public void setSpotLatlng(LatLng spotLatlng) {
        this.latLng = spotLatlng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isUserOccupied() {
        return userOccupied;
    }

    public void setUserOccupied(boolean userOccupied) {
        this.userOccupied = userOccupied;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

}
