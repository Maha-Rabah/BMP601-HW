package com.maha.homework_bmp601;

public class Delegate {
    private int id;
    private String name;
    private String phone;
    private String region;
    private String photo;


    public Delegate(int id, String name, String phone, String region, String photo) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.region = region;
        this.photo=photo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getRegion() {
        return region;
    }
    public String getPhoto() {
        return photo;
    }

}