package com.example.manna_project.MainAgreementActivity_Util.Setting;

public class SettingListItem {
    private int img;
    private String txt;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public SettingListItem(int img, String txt) {
        this.img = img;
        this.txt = txt;
    }
}