package com.example.sinupsample;

public class Spot {
    private String spotName;
    private String address;
    private String details;
private String spotId;
    private String creatorKey;
    private String photoUrl;

    private String sponsa;
    public Spot(String spotName, String address, String details, String creatorKey, String photoUrl, String spotId, String sponsa) {
        this.spotName = spotName;
        this.address = address;
        this.details = details;
        this.creatorKey = creatorKey;
        this.photoUrl = photoUrl;
        this.spotId = spotId;
        this.sponsa = sponsa;
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

    public boolean getSponsa() {
        // sponsaがnullでない場合、かつ"true"と等しい場合にtrueを返す
        return sponsa != null && sponsa.equals("true");
    }
}
