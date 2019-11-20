package com.example.manna_project.MainAgreementActivity_Util.Invited;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

import java.util.Calendar;

public class InvitedListItem {
    private String id;
    private String title;
    private MannaUser leader;
    private String place;
    private Calendar date;

    public InvitedListItem() {
    }

    public InvitedListItem(String id, String title, MannaUser leader, String place, Calendar date) {
        this.id = id;
        this.title = title;
        this.leader = leader;
        this.place = place;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MannaUser getLeader() {
        return leader;
    }

    public void setLeader(MannaUser leader) {
        this.leader = leader;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}