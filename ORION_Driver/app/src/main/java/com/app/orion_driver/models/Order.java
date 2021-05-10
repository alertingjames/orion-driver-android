package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Order {

    int id = 1;
    int user_id = 1;
    String orderID = "";
    double price = 0.0d;
    String unit = "SGD";
    double shipping = 0.0d;
    String date = "";
    String email = "";
    String phone_number = "";
    String address = "";
    String address_line = "";
    String status = "";
    int discount = 0;
    LatLng latLng = null;

    ArrayList<OrderItem> items = new ArrayList<>();

    public Order(){}

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress_line(String address_line) {
        this.address_line = address_line;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getOrderID() {
        return orderID;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public double getShipping() {
        return shipping;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_line() {
        return address_line;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }
}

