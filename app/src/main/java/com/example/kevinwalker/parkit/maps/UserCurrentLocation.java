package com.example.kevinwalker.parkit.maps;

/**
 * Created by hollis on 7/22/18.
 */

public class UserCurrentLocation {

    private double latitude;
    private double longitude;
    private long timeStamp;

    public UserCurrentLocation() {}


    public UserCurrentLocation(double latitude, double longitude, long timeStamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
