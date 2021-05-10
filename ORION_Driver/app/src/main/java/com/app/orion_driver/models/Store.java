package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

public class Store {
    int id = 1;
    int userId = 1;
    String name = "";
    String phoneNumber = "";
    String address = "";
    double delivery_price = 0.0d;
    int delivery_days = 0;
    String logoUrl = "";
    String _status = "";
    String _registered_time = "";
    float ratings = 0.0f;
    int reviews = 0;
    LatLng latLng = null;
    int orderedItemsCount = 0;
    String orderedDateTime = "";
    String orderdStatus = "";
    double distance = 0.0d;
    int orderId = 0;

    public Store(){

    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderdStatus(String orderdStatus) {
        this.orderdStatus = orderdStatus;
    }

    public String getOrderdStatus() {
        return orderdStatus;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setOrderedItemsCount(int orderedItemsCount) {
        this.orderedItemsCount = orderedItemsCount;
    }

    public void setOrderedDateTime(String orderedDateTime) {
        this.orderedDateTime = orderedDateTime;
    }

    public int getOrderedItemsCount() {
        return orderedItemsCount;
    }

    public String getOrderedDateTime() {
        return orderedDateTime;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setDelivery_price(double delivery_price) {
        this.delivery_price = delivery_price;
    }

    public void setDelivery_days(int delivery_days) {
        this.delivery_days = delivery_days;
    }

    public double getDelivery_price() {
        return delivery_price;
    }

    public int getDelivery_days() {
        return delivery_days;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public float getRatings() {
        return ratings;
    }

    public int getReviews() {
        return reviews;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public void set_registered_time(String _registered_time) {
        this._registered_time = _registered_time;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String get_status() {
        return _status;
    }

    public String get_registered_time() {
        return _registered_time;
    }
}
