package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

public class OrionAddress {
    int id = 0;
    int userId = 0;
    int storeId = 0;
    String address = "";
    String country = "";
    String area = "";
    String street = "";
    String house = "";
    LatLng latLng = null;

    public OrionAddress(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getArea() {
        return area;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
