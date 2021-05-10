package com.app.orion_driver.models;

import com.google.android.gms.maps.model.LatLng;

public class User {
    int _idx = 0;
    String _name="";
    String _email = "";
    String _password = "";
    String _photoUrl = "";
    String _phone_number = "";
    String _address = "";
    String _country = "";
    String _area = "";
    String _street = "";
    String _house = "";
    LatLng latLng = null;
    String role = "";
    String _status = "";
    String _acc_status = "";
    String _registered_time = "";
    int _stores = 0;

    public User(){}

    public void set_acc_status(String _acc_status) {
        this._acc_status = _acc_status;
    }

    public String get_acc_status() {
        return _acc_status;
    }

    public void set_country(String _country) {
        this._country = _country;
    }

    public void set_area(String _area) {
        this._area = _area;
    }

    public void set_street(String _street) {
        this._street = _street;
    }

    public void set_house(String _house) {
        this._house = _house;
    }

    public void set_stores(int _stores) {
        this._stores = _stores;
    }

    public String get_country() {
        return _country;
    }

    public String get_area() {
        return _area;
    }

    public String get_street() {
        return _street;
    }

    public String get_house() {
        return _house;
    }

    public int get_stores() {
        return _stores;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public void set_photoUrl(String _photoUrl) {
        this._photoUrl = _photoUrl;
    }

    public void set_phone_number(String _phone_number) {
        this._phone_number = _phone_number;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public void set_registered_time(String _registered_time) {
        this._registered_time = _registered_time;
    }

    public int get_idx() {
        return _idx;
    }

    public String get_name() {
        return _name;
    }

    public String get_email() {
        return _email;
    }

    public String get_password() {
        return _password;
    }

    public String get_photoUrl() {
        return _photoUrl;
    }

    public String get_phone_number() {
        return _phone_number;
    }

    public String get_address() {
        return _address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String get_status() {
        return _status;
    }

    public String get_registered_time() {
        return _registered_time;
    }
}
