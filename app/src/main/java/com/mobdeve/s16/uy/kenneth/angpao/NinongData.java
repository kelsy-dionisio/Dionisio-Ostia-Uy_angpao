package com.mobdeve.s16.uy.kenneth.angpao;

public class NinongData {

    int ninongId;
    String prefix, pic, name, addInfo;

    public NinongData(int ninongId, String pic, String prefix, String name, String addInfo) {
        this.ninongId = ninongId;
        this.pic = pic;
        this.prefix = prefix;
        this.name = name;
        this.addInfo = addInfo;
    }
    public int getId() {
        return ninongId;
    }
    public String getPic() {
        return pic;
    }
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String toString() {
        return prefix + " " + name;
    }


}
