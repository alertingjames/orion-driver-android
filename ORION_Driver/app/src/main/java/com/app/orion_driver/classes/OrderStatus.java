package com.app.orion_driver.classes;

import java.util.HashMap;
import java.util.Map;

public class OrderStatus {
    public Map<String, String> statusStr = new HashMap<>();
    public Map<String, String> nextStatus = new HashMap<>();
    public Map<String, String> nextStatusStr = new HashMap<>();
    public Map<String, Integer> statusIndex = new HashMap<>();

    public void initOrderStatus(){
        statusStr.put("placed", "Placed");
        statusStr.put("confirmed", "Confirmed");
        statusStr.put("prepared", "Prepared");
        statusStr.put("ready", "Ready");
        statusStr.put("delivered", "Delivered");

        nextStatus.put("placed", "confirmed");
        nextStatus.put("confirmed", "prepared");
        nextStatus.put("prepared", "ready");
        nextStatus.put("ready", "delivered");

        nextStatusStr.put("placed", "Confirm");
        nextStatusStr.put("confirmed", "Prepare");
        nextStatusStr.put("prepared", "Ready");
        nextStatusStr.put("ready", "Delivery");
        nextStatusStr.put("delivered", "Completed");

        statusIndex.put("placed", 0);
        statusIndex.put("confirmed", 1);
        statusIndex.put("prepared", 2);
        statusIndex.put("ready", 3);
        statusIndex.put("delivered", 4);
    }
}






















