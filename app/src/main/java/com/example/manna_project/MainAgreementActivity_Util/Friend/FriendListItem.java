package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.widget.ImageView;
import android.widget.TextView;

public class FriendListItem {
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

    public FriendListItem(int img, String txt) {
        this.img = img;
        this.txt = txt;
    }
}