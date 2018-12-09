package com.example.kevinwalker.parkit.maps;


import android.location.Location;



public class CustomLocation extends Location {

    private long timeStamp = 0;
    private String parkedAddress = "";


    public CustomLocation() {
        super(new Location("customLocation"));
    }

    public CustomLocation(Location customLocation) {
        super(customLocation);
    }

    public CustomLocation(Location customLocation, long timeStamp, String parkedAddress) {
        super(customLocation);
        this.timeStamp = timeStamp;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getParkedAddress() {
        return parkedAddress;
    }

    public void setParkedAddress(String parkedAddress) {
        this.parkedAddress = parkedAddress;
    }
}
