package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MannaUser{

    static final String TAG = "MANNAYC";

    private String name;
    private String nickName;
    private String Uid;
    private String eMail;
    private ArrayList<Routine> routineList;

    public MannaUser(){
    }
    public MannaUser(DataSnapshot dataSnapshot){        //dataSnapshot 넘겨줄때의 생성자
        routineList = new ArrayList<>();
        DataSnapshot routineSnapshot = dataSnapshot.child("Routines");
        for(DataSnapshot postSnapshot : routineSnapshot.getChildren()){
            routineList.add(new Routine(postSnapshot));
        }
        this.name = dataSnapshot.child("Name").getValue(String.class);
        this.Uid = dataSnapshot.child("Uid").getValue(String.class);
        this.eMail =dataSnapshot.child("E-mail").getValue(String.class);
        this.nickName = dataSnapshot.child("NickName").getValue(String.class);
    }

    public MannaUser(String name, String eMail, String Uid){
        this.name = name;
        this.Uid = Uid;
        this.eMail = eMail;
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


    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("Name", this.name);
        result.put("NickName", this.nickName);
        result.put("Uid", this.Uid);
        result.put("E-ail", this.eMail);
        result.put("Routines",this.routineList);
        return result;
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
        public Routine(DataSnapshot dataSnapshot){
            this.startTime = dataSnapshot.child("StartTime").getValue(Integer.class);
            this.endTime = dataSnapshot.child("EndTime").getValue(Integer.class);
            this.day = dataSnapshot.child("Day").getValue(Integer.class);
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


