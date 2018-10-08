package com.example.kevinwalker.parkit.maps;


import android.location.Location;


/**
 * Created by hollis on 7/22/18.
 */

public class CustomLocation extends Location {

    private long timeStamp = 0;


    public CustomLocation() {
        super(new Location("customLocation"));
    }

    public CustomLocation(Location customLocation) {
        super(customLocation);
    }

    public CustomLocation(Location customLocation, long timeStamp) {
        super(customLocation);
        this.timeStamp = timeStamp;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
