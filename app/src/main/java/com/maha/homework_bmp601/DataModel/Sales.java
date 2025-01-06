package com.maha.homework_bmp601.DataModel;

public class Sales {
    private int saleID;
    private int delegateID;
    private int regionID;
    private double saleAmount;
    private int saleMonth;
    private int saleYear;
    public Sales(int saleID, int delegateID, int regionID, double saleAmount, int saleMonth, int saleYear) {
        this.saleID = saleID;
        this.delegateID = delegateID;
        this.regionID = regionID;
        this.saleAmount = saleAmount;
        this.saleMonth = saleMonth;
        this.saleYear = saleYear;
    }
    public int getSaleID() {
        return saleID;
    }
    public int getDelegateID() {
        return delegateID;
    }
    public int getRegionID() {
        return regionID;
        }
    public double getSaleAmount() {
        return saleAmount;
    }
    public int getSaleMonth() {
        return saleMonth;
    }
    public int getSaleYear() {
        return saleYear;
    }
}
