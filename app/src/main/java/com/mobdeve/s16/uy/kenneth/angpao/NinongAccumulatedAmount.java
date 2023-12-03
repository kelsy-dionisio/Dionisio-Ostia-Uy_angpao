package com.mobdeve.s16.uy.kenneth.angpao;

public class NinongAccumulatedAmount {
    private int ninongId;
    private String prefix;
    private String name;
    private int accumulatedAmount;

    public NinongAccumulatedAmount(int ninongId, String prefix, String name, int accumulatedAmount) {
        this.ninongId = ninongId;
        this.prefix = prefix;
        this.name = name;
        this.accumulatedAmount = accumulatedAmount;
    }

    public int getNinongId() {
        return ninongId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public int getAccumulatedAmount() {
        return accumulatedAmount;
    }
}