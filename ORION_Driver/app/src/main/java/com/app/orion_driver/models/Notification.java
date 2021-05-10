package com.app.orion_driver.models;

public class Notification {
    int id = 0;
    int receiver_id = 0;
    int sender_id = 0;
    String sender_name = "";
    String sender_email = "";
    String sender_phone = "";
    String message = "";
    String date_time = "";
    String image = "";

    public Notification(){}

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public void setSender_phone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getId() {
        return id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getSender_email() {
        return sender_email;
    }

    public String getSender_phone() {
        return sender_phone;
    }

    public String getMessage() {
        return message;
    }

    public String getDate_time() {
        return date_time;
    }
}
