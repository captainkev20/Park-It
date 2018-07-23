package com.example.kevinwalker.parkit;

/**
 * Created by hollis on 7/22/18.
 */

public class Location {
    private double lat;
    private double lon;
    private long timeStamp;

    public Location() {}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
