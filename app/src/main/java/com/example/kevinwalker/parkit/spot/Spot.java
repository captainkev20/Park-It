package com.example.kevinwalker.parkit.spot;

import java.math.BigDecimal;

/**
 * Created by hollis on 8/19/18.
 */

public class Spot {
    private double latitude = 0;
    private double longitude = 0;
    private String name = "";
    private String ownerUUID = "";
    private String spotUUID = "";
    private boolean occupied = false;
    private SpotSize spotSize = SpotSize.STANDARD;
    private Surface surface = Surface.ASPHALT;
    private Ownership ownership = Ownership.SELF;
    private BigDecimal hourlyRate = BigDecimal.valueOf(0.00);
    private BigDecimal dailyRate = BigDecimal.valueOf(0.00);
    private BigDecimal monthlyRate = BigDecimal.valueOf(0.00);
    private long dateCreated = 0;
    private long dateClockIn = 0;
    private long dateClockOut = 0;
    private long dateLastEdit = 0;

    public Spot() {
        this.dateCreated = System.currentTimeMillis();
        this.dateLastEdit = dateCreated;
    }

    public Spot(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = System.currentTimeMillis();
        this.dateLastEdit = dateCreated;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getSpotUUID() { return spotUUID; }

    public void setSpotUUID(String spotUUID) { this.spotUUID = spotUUID; }

    public SpotSize getSpotSize() {
        return spotSize;
    }

    public void setSpotSize(SpotSize spotSize) {
        this.spotSize = spotSize;
    }

    public long getDateLastEdit() {
        return dateLastEdit;
    }

    public void setDateLastEdit(long dateLastEdit) {
        this.dateLastEdit = dateLastEdit;
    }

    public Ownership getOwnership() {
        return ownership;
    }

    public void setOwnership(Ownership ownership) {
        this.ownership = ownership;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateClockIn() {
        return dateClockIn;
    }

    public void setDateClockIn(long dateClockIn) {
        this.dateClockIn = dateClockIn;
    }

    public long getDateClockOut() {
        return dateClockOut;
    }

    public void setDateClockOut(long dateClockOut) {
        this.dateClockOut = dateClockOut;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }
}
