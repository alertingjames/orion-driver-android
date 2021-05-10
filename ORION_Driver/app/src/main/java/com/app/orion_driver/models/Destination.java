package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

public class Destination {
    int id = 0;
    int userId = 0;
    String title = "";
    String address = "";
    String addressLine = "";
    String picture_url = "";
    LatLng latLng = null;

    public Destination(){}

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
