package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.manna_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MannaUser implements Parcelable {

    static final String TAG = "MANNAYC";

    private String name;
    private String nickName;
    private String Uid;
    private String eMail;
    private ArrayList<Routine> routineList;

    public MannaUser(){
    }



    public MannaUser(DataSnapshot dataSnapshot){        //dataSnapshot 넘겨줄때의 생성자
     //   Log.d(TAG, "MannaUser: " + dataSnapshot.toString());

        routineList = new ArrayList<>();
        DataSnapshot routineSnapshot = dataSnapshot.child("Routines");
        for(DataSnapshot postSnapshot : routineSnapshot.getChildren()){
            Routine routine = new Routine(postSnapshot);
            if (routine != null)
                routineList.add(routine);
        }
        this.name = dataSnapshot.child("Name").getValue(String.class);
        this.Uid = dataSnapshot.child("Uid").getValue(String.class);
//        임시
//        this.Uid = "XJJkpZ6ojhQ0ttiF9OgfDEzC00K2";
        this.eMail =dataSnapshot.child("E-mail").getValue(String.class);
        this.nickName = dataSnapshot.child("NickName").getValue(String.class);
    }

    public MannaUser(String name, String eMail, String Uid){
        this.name = name;
        this.Uid = Uid;
        this.eMail = eMail;
    }

    protected MannaUser(Parcel in) {
        name = in.readString();
        nickName = in.readString();
        Uid = in.readString();
        eMail = in.readString();
        routineList = in.readArrayList(Routine.class.getClassLoader());
    }

    public static final Creator<MannaUser> CREATOR = new Creator<MannaUser>() {
        @Override
        public MannaUser createFromParcel(Parcel in) {
            return new MannaUser(in);
        }

        @Override
        public MannaUser[] newArray(int size) {
            return new MannaUser[size];
        }
    };

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

    public ArrayList<Routine> getRoutineList() {
        return routineList;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("Name", this.name);
        result.put("NickName", this.nickName);
        result.put("Uid", this.Uid);
        result.put("E-mail", this.eMail);
        result.put("Routines",this.routineList);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(nickName);
        dest.writeString(Uid);
        dest.writeString(eMail);
        dest.writeList(routineList);
    }

    @Override
    public String toString() {
        return "MannaUser{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", Uid='" + Uid + '\'' +
                ", eMail='" + eMail + '\'' +
                '}';
    }
}


