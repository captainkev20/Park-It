package com.example.kevinwalker.parkit.spot;

import com.example.kevinwalker.parkit.payments.PaymentType;



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
    private int hourlyRate = 0;
    private double dailyRate = 0.00;
    private double monthlyRate = 0.00;
    private long dateCreated = 0;
    private long dateClockIn = 0;
    private long dateClockOut = 0;
    private long dateLastEdit = 0;
    private int spotDistance = 0;

    private PaymentType paymentType = PaymentType.CASH;

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

    public int getHourlyRate() { return hourlyRate; }

    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public int getSpotDistance() { return spotDistance; }

    public void setSpotDistance(int spotDistance) { this.spotDistance = spotDistance; }

    public PaymentType getPaymentType() { return paymentType; }

    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

}
