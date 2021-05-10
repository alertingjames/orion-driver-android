package com.app.orion_driver.models;

import java.util.ArrayList;

public class Payment {
    int id = 0;
    double totalPrice = 0;
    double subTotalPrice = 0;
    double deliveryFee = 0;
    int bonus = 0;
    ArrayList<OrderItem> items = new ArrayList<>();

    public Payment(){}

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setSubTotalPrice(double subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public int getId() {
        return id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getSubTotalPrice() {
        return subTotalPrice;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public int getBonus() {
        return bonus;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }
}
