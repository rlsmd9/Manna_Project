package com.example.manna_project.MainAgreementActivity_Util;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;

public class MannaUser {
    private String name;
    private String nickName;
    private String uId;
    private ArrayList<String> friendList;
    private ArrayList<Routine> routineList;

    public MannaUser(){

    }
    public MannaUser(String uid){

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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void addFriend(MannaUser user){
        this.friendList.add(user.getuId());
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
    public class Routine{
        private int startTime;
        private int endTime;
        private int day;

        public Routine(int start, int end, int day){
            this.startTime = start;
            this.endTime = end;
            this.day = day;
        }
    }
}


