package com.example.manna_project.MainAgreementActivity_Util;

import androidx.annotation.NonNull;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Schedule {

    long startTime;
    long endTime;
    Calendar startCalendar;
    Calendar endCalendar;

    public Schedule(Event event) {
        startTime = event.getStart().getDateTime().getValue();
        endTime = event.getEnd().getDateTime().getValue();
        startCalendar = getStartCalendar();
        endCalendar = getEndCalendar();
    }
    public Schedule(DataSnapshot dataSnapshot){
        startTime = dataSnapshot.child("startTime").getValue(Long.class);
        endTime = dataSnapshot.child("endTime").getValue(Long.class);
        startCalendar = getStartCalendar();
        endCalendar = getEndCalendar();
    }

    public Schedule(long startTime, long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        this.startCalendar = getStartCalendar();
        this.endCalendar = getEndCalendar();
    }

    public static Schedule eventToSchdule(Event event) {
        long startTime = event.getStart().getDateTime().getValue();
        long endTime = event.getEnd().getDateTime().getValue();
        return new Schedule(startTime,endTime);
    }

    public Calendar getStartCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return calendar;
    }
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public Calendar getEndCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        return calendar;
    }
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ "+
                "StartTime : "+
                startTime+
                ", EndTime : "+
                endTime+
                "}";
    }
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("startTime",startTime);
        result.put("endTime",endTime);
        return result;
    }
}
