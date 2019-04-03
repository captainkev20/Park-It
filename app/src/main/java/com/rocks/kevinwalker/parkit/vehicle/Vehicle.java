package com.rocks.kevinwalker.parkit.vehicle;

import android.widget.ImageView;

public class Vehicle {

    private String vehicleMake = "";
    private String vehicleModel = "";
    private String vehicleName = "";
    private String vehicleLicensePlate = "";
    private String vehicleUUID = "";
    private int vehicleYear = 0;
    private ImageView vehiclePicture;
    private String vehiclePhotoURL = "";

    public Vehicle(){}

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public ImageView getVehiclePicture() {
        return vehiclePicture;
    }

    public void setVehiclePicture(ImageView vehiclePicture) {
        this.vehiclePicture = vehiclePicture; }

    public String getVehicleName() { return vehicleName; }

    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public String getVehicleLicensePlate() { return vehicleLicensePlate; }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate; }

    public String getVehicleUUID() { return vehicleUUID; }

    public void setVehicleUUID(String vehicleUUID) { this.vehicleUUID = vehicleUUID; }

    public String getVehiclePhotoURL() { return vehiclePhotoURL; }

    public void setVehiclePhotoURL(String vehiclePhotoURL) { this.vehiclePhotoURL = vehiclePhotoURL; }
}
