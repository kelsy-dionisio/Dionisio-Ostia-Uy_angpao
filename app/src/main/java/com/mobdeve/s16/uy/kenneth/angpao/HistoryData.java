package com.mobdeve.s16.uy.kenneth.angpao;

import android.content.Intent;

public class HistoryData {
    String name;
    String amount;
    String date;
    int angpaoId, ninongId;
    String ninongName, ninongPrefix;

    public HistoryData(int angpaoId, int ninongId, String amount, String date, String ninongName, String ninongPrefix) {
        this.angpaoId = angpaoId;
        this.ninongId = ninongId;
        this.amount = amount;
        this.date = date;
        this.ninongName = ninongName;
        this.ninongPrefix = ninongPrefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNinongName() { return ninongName; }
    public void setNinongName(String ninongName) {
        this.ninongName = ninongName;
    }

    public String getNinongPrefix() { return ninongPrefix; }
    public void setNinongPrefix(String ninongPrefix) {
        this.ninongPrefix = ninongPrefix;
    }

    public String toString() {
        return "HistoryData{" +
                "ninongPrefix='" + ninongPrefix + '\'' +
                ", ninongName='" + ninongName + '\'' +
                ", amount='" + amount + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
    public int getAngpaoId() {
        return angpaoId;
    }

}
