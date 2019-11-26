package com.example.manna_project.MainAgreementActivity_Util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.manna_project.MainAgreementActivity;
import com.google.firebase.database.DataSnapshot;

public class Routine implements Parcelable {
    private int startTime;
    private int endTime;
    private int day;

    public Routine(int start, int end, int day){
        this.startTime = start;
        this.endTime = end;
        this.day = day;
    }
    public Routine(DataSnapshot dataSnapshot){
        Log.d("JS", "Routine: " + dataSnapshot.toString());
        this.startTime = dataSnapshot.child("startTime").getValue(Integer.class);
        this.endTime = dataSnapshot.child("endTime").getValue(Integer.class);
        this.day = dataSnapshot.child("day").getValue(Integer.class);
    }

    protected Routine(Parcel in) {
        startTime = in.readInt();
        endTime = in.readInt();
        day = in.readInt();
    }

    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeInt(day);
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