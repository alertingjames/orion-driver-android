package com.app.orion_driver.models;

import java.util.ArrayList;

public class Product {

    int idx = 0;
    int userId = 0;
    int storeId = 0;
    int brandId = 0;
    String name = "";
    String picture_url = "";
    String category = "";
    String subcategory = "";
    String gender = "";
    String genderKey = "";
    double price = 0.0f;
    double new_price = 0.0f;
    String unit = "";
    String description = "";
    String registered_time = "";
    String status = "";
    float ratings = 0.0f;
    int likes = 0;
    boolean liked = false;
    double delivery_price = 0.0d;
    int delivery_days = 0;
    String brand_name = "";
    String brand_logo = "";

    ArrayList<Picture> _pictureList = new ArrayList<>();

    public Product(){

    }

    public void setBrand_logo(String brand_logo) {
        this.brand_logo = brand_logo;
    }

    public String getBrand_logo() {
        return brand_logo;
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

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGenderKey(String genderKey) {
        this.genderKey = genderKey;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getGender() {
        return gender;
    }

    public String getGenderKey() {
        return genderKey;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public float getRatings() {
        return ratings;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNew_price(double new_price) {
        this.new_price = new_price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRegistered_time(String registered_time) {
        this.registered_time = registered_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void set_pictureList(ArrayList<Picture> _pictureList) {
        this._pictureList.clear();
        this._pictureList.addAll(_pictureList);
    }

    public int getIdx() {
        return idx;
    }

    public int getUserId() {
        return userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public double getNew_price() {
        return new_price;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getRegistered_time() {
        return registered_time;
    }

    public String getStatus() {
        return status;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public ArrayList<Picture> get_pictureList() {
        return _pictureList;
    }
}
