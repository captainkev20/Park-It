package com.example.kevinwalker.parkit.vehicle;

import android.widget.ImageView;

/**
 * Created by kevinwalker on 3/19/18.
 */

public class Vehicle {

    private String make;
    private String model;
    private Short year;
    private ImageView picture;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(Short year) {
        this.year = year;
    }

    public ImageView getPicture() {
        return picture;
    }

    public void setPicture(ImageView picture) {
        this.picture = picture;
    }

}
