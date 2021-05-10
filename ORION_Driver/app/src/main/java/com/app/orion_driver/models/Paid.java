package com.app.orion_driver.models;

public class Paid {
    int id = 0;
    int user_id = 0;
    int vendor_id = 0;
    int store_id = 0;
    int order_id = 0;
    String orderID = "";
    int itemsCount = 0;
    double paid_amount = 0;
    String paid_time = "";
    String payment_status = "";
    String charge_id = "";
    String transfer_id = "";
    String store_name = "";

    public Paid(){}

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_name() {
        return store_name;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
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

    public void setCharge_id(String charge_id) {
        this.charge_id = charge_id;
    }

    public void setTransfer_id(String transfer_id) {
        this.transfer_id = transfer_id;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getOrderID() {
        return orderID;
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

    public String getCharge_id() {
        return charge_id;
    }

    public String getTransfer_id() {
        return transfer_id;
    }
}
