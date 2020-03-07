package com.example.beproj3.Models;

public class Locatioi {
    String longitude ,latitude ,uid;

    public  Locatioi(){}

    public Locatioi(String longitude, String latitude, String uid) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.uid = uid;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

