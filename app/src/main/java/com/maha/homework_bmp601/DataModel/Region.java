package com.maha.homework_bmp601.DataModel;

public class Region{
    private int region_id;
    private String region_name;
    public Region(int region_id,String region_name){
        this.region_id=region_id;
        this.region_name=region_name;
    }
    public int getId(){
        return region_id;
    }
    public String getRegion_name() {
        return region_name;
    }
}
