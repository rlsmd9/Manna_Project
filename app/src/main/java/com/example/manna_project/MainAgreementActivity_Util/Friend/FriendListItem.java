package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

public class FriendListItem {
    MannaUser user;

    public FriendListItem(MannaUser user) {
        this.user = user;
    }

    public MannaUser getUser() {
        return user;
    }

    public void setUser(MannaUser user) {
        this.user = user;
    }
}