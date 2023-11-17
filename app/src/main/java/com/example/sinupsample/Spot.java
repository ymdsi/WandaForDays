package com.example.sinupsample;

public class Spot {
    private String spotName;
    private String address;
    private String details;
private String spotId;
    private String creatorKey;
    private String photoUrl;
    public Spot(String spotName, String address, String details, String creatorKey, String photoUrl, String spotId) {
        this.spotName = spotName;
        this.address = address;
        this.details = details;
        this.creatorKey = creatorKey;
        this.photoUrl = photoUrl;
        this.spotId = spotId;
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

    public String getCreatorKey() { return  creatorKey;}
    public String getPhotoUrl() {return photoUrl;}
    public String getSpotId() {return spotId;}
}
