package com.example.stay_sober_android.models;

public class User {
    private String name;
    private double longitude;
    private double latitude;
    private int range;
    private String description;
    private String image;
    private boolean reacher;
    private boolean giver;
    private String about;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User(String name, double longitude, double latitude, int range, String description, String image, boolean reacher, boolean giver) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.range = range;
        this.description = description;
        this.image = image;
        this.reacher = reacher;
        this.giver = giver;
    }


    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReacher() {
        return reacher;
    }

    public void setReacher(boolean reacher) {
        this.reacher = reacher;
    }

    public boolean isGiver() {
        return giver;
    }

    public void setGiver(boolean giver) {
        this.giver = giver;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
