package com.maha.homework_bmp601.DataModel;

public class Commission {
    private int commissionID;
    private int delegateID;
    private int saleID;
    private double commissionAmount;
    public Commission(int commissionID, int delegateID, int saleID, double commissionAmount) {
        this.commissionID = commissionID;
        this.delegateID = delegateID;
        this.saleID = saleID;
        this.commissionAmount = commissionAmount;
    }
    public int getCommissionID() {
        return commissionID;
    }
    public int getDelegateID() {
        return delegateID;
        }
    public int getSaleID() {
        return saleID;
    }
    public double getCommissionAmount() {
        return commissionAmount;
    }
}
