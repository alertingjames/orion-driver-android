package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

public class OrderItem {

    int id = 1;
    int order_id = 1;
    int user_id = 1;
    int vendor_id = 1;
    int store_id = 1;
    String store_name = "";
    int product_id = 1;
    String product_name = "";
    String category = "";
    String subcategory = "";
    String gender = "";
    String gender_key = "";
    double price = 0.0f;
    String unit = "SGD";
    int quantity = 0;
    String date_time = "";
    String picture_url = "";
    int delivery_days = 0;
    double delivery_price = 0.0d;
    String status = "";
    String orderID = "";
    String contact = "";
    int discount = 0;
    String vendor_contact = "";
    double paid_amount = 0;
    String paid_time = "";
    String payment_status = "";
    int paid_id = 1;
    String address = "";
    String address_line = "";
    LatLng latLng = null;

    public OrderItem(){}

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress_line(String address_line) {
        this.address_line = address_line;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_line() {
        return address_line;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setPaid_amount(double paid_amount) {
        this.paid_amount = paid_amount;
    }

    public void setPaid_time(String paid_time) {
        this.paid_time = paid_time;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public void setPaid_id(int paid_id) {
        this.paid_id = paid_id;
    }

    public double getPaid_amount() {
        return paid_amount;
    }

    public String getPaid_time() {
        return paid_time;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public int getPaid_id() {
        return paid_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGender_key(String gender_key) {
        this.gender_key = gender_key;
    }

    public void setDelivery_days(int delivery_days) {
        this.delivery_days = delivery_days;
    }

    public void setDelivery_price(double delivery_price) {
        this.delivery_price = delivery_price;
    }

    public void setVendor_contact(String vendor_contact) {
        this.vendor_contact = vendor_contact;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getGender() {
        return gender;
    }

    public String getGender_key() {
        return gender_key;
    }

    public int getDelivery_days() {
        return delivery_days;
    }

    public double getDelivery_price() {
        return delivery_price;
    }

    public String getVendor_contact() {
        return vendor_contact;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getId() {
        return id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getPicture_url() {
        return picture_url;
    }
}
