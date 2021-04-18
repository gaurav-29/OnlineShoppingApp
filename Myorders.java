package com.android_batch_31.designdemo;

public class Myorders {

    private String billid, billdate, amount;

    public Myorders(String billid, String billdate, String amount) {
        this.billid = billid;
        this.billdate = billdate;
        this.amount = amount;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
