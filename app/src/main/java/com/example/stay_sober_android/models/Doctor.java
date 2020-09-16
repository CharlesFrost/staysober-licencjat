package com.example.stay_sober_android.models;

import android.app.Activity;

public class Doctor extends User {
    private String phoneNumber;

    public Doctor(String name, String phoneNumber) {
        super(name);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
