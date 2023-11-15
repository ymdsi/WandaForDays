package com.example.sinupsample;

public class Spot {
    private String spotName;
    private String address;
    private String details;

    public Spot(String spotName, String address, String details) {
        this.spotName = spotName;
        this.address = address;
        this.details = details;
    }

    public String getSpotName() {
        return spotName;
    }

    public String getAddress() {
        return address;
    }

    public String getDetails() {
        return details;
    }
}
