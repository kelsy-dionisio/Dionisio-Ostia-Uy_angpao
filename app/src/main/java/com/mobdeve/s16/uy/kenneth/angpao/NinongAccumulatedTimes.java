package com.mobdeve.s16.uy.kenneth.angpao;

public class NinongAccumulatedTimes {
    private int ninongId;
    private String type;
    private String name;
    private int accumulatedTimes;

    public NinongAccumulatedTimes(int ninongId, String type, String name, int accumulatedTimes) {
        this.ninongId = ninongId;
        this.type = type;
        this.name = name;
        this.accumulatedTimes = accumulatedTimes;
    }

    public int getNinongId() {
        return ninongId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getAccumulatedTimes() {
        return accumulatedTimes;
    }
}
