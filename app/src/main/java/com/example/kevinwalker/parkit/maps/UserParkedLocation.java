package com.example.kevinwalker.parkit.maps;

public class UserParkedLocation {

    private double parkedLatitude;
    private double parkedLongitude;
    private long parkedTimeStamp;

    public UserParkedLocation() {}


    public UserParkedLocation(double parkedLatitude, double parkedLongitude, long parkedTimeStamp) {
        this.parkedLatitude = parkedLatitude;
        this.parkedLongitude = parkedLongitude;
        this.parkedTimeStamp = parkedTimeStamp;
    }

    public double getParkedLatitude() {
        return parkedLatitude;
    }

    public void setParkedLatitude(double parkedLatitude) {
        this.parkedLatitude = parkedLatitude;
    }

    public double getParkedLongitude() {
        return parkedLongitude;
    }

    public void setParkedLongitude(double parkedLongitude) {
        this.parkedLongitude = parkedLongitude;
    }

    public long getParkedTimeStamp() {
        return parkedTimeStamp;
    }

    public void setParkedTimeStamp(long parkedTimeStamp) {
        this.parkedTimeStamp = parkedTimeStamp;
    }

}
