package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MannaUser {
    private String name;
    private String nickName;
    private String Uid;
    private String eMail;
    private ArrayList<String> friendList;
    private ArrayList<Routine> routineList;

    public MannaUser(){
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        this.Uid = mAuth.getUid();
        this.name = mAuth.getDisplayName();
        this.eMail = mAuth.getEmail();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public void addFriend(MannaUser user){
        this.friendList.add(user.getUid());
    }
    public void addFriend(String friendUid){
        this.friendList.add(friendUid);
    }


    public void addRoutine(Routine r){
        this.routineList.add(r);
    }
    public void setRoutineList(ArrayList<Routine> routineList) {
        this.routineList = routineList;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }


    public class Routine{
        private int startTime;
        private int endTime;
        private int day;

        public Routine(int start, int end, int day){
            this.startTime = start;
            this.endTime = end;
            this.day = day;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }
}


