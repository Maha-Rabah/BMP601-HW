package com.maha.homework_bmp601.DataModel;

public class Delegate {
    private int id;
    private String name;
    private String phone;
    //change type to int as it should be regionID instead of region name
    private String regionId;
    private String photo;


    public Delegate(int id, String name, String phone, String region, String photo) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.regionId = region;
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
        return regionId;
    }
    public String getPhoto() {
        return photo;
    }

}
