package com.example.manna_project.MainAgreementActivity_Util.RecommendDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecommendDate {

    String recommendStarttime;
    String recommendEndTime;
    long startTime;
    long endTime;
    Calendar startCalendar;
    Calendar endCalendar;
    int time;

    public RecommendDate(Calendar start, Calendar end){
        this.startCalendar = start;
        this.endCalendar = end;
        startTime = start.getTimeInMillis();
        endTime = end.getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        recommendStarttime = simpleDateFormat.format(start.getTime());
        recommendEndTime = simpleDateFormat.format(end.getTime());
        time = end.get(Calendar.HOUR_OF_DAY)- start.get(Calendar.HOUR_OF_DAY);
    }
    public void getStringDate(){
    }

    public String getRecommendStarttime() {
        return recommendStarttime;
    }

    public void setRecommendStarttime(String recommendStarttime) {
        this.recommendStarttime = recommendStarttime;
    }

    public String getRecommendEndTime() {
        return recommendEndTime;
    }

    public void setRecommendEndTime(String recommendEndTime) {
        this.recommendEndTime = recommendEndTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartCalendar() {
        return startCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public Calendar getEndCalendar() {
        return endCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }
}
