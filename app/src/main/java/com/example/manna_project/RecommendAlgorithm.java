package com.example.manna_project;

import com.example.manna_project.MainAgreementActivity_Util.RecommendDate.RecommendDate;
import com.example.manna_project.MainAgreementActivity_Util.Routine;
import com.example.manna_project.MainAgreementActivity_Util.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class RecommendAlgorithm {

    private Calendar startBound;
    private Calendar endBound;
    private ArrayList<Schedule> schedules;
    private ArrayList<Routine> routines;
    private ArrayList<RecommendDate> recommendDates;
    private int table[][];
    private int totalDay;

    public RecommendAlgorithm(Calendar startBound, Calendar endBound) {
        this.startBound = startBound;
        this.endBound = endBound;
        this.totalDay = endBound.get(Calendar.DAY_OF_YEAR) - startBound.get(Calendar.DAY_OF_YEAR);
        if (endBound.get(Calendar.YEAR) != startBound.get(Calendar.YEAR))
            totalDay += 365;
        this.table = new int[totalDay][24];
        Arrays.fill(table, 0);
    }

    public RecommendAlgorithm(ArrayList<Schedule> schedules, ArrayList<Routine> routines) {
        this.schedules = schedules;
        this.routines = routines;
    }
    public void routinesToSchedules(){
        for(Routine routine : routines){
            int dayGap = routine.getDay()+1 - startBound.get(Calendar.DAY_OF_WEEK);
            if(dayGap<0){
                dayGap+=7;
            }
            Calendar temp = (Calendar)startBound.clone();
            temp.add(Calendar.DAY_OF_WEEK,dayGap);
            temp.set(Calendar.HOUR_OF_DAY,routine.getStartTime());
            temp.set(Calendar.MINUTE,0);
            temp.set(Calendar.SECOND,0);
            temp.set(Calendar.MILLISECOND,0);
            long start = temp.getTimeInMillis();
            temp.set(Calendar.HOUR_OF_DAY,routine.getEndTime());
            long end = temp.getTimeInMillis();
            while(start<endBound.getTimeInMillis()){
                if(end <=endBound.getTimeInMillis())
                    end = endBound.getTimeInMillis();
                schedules.add(new Schedule(start,end));
                start += 7*24*60*60*1000;
                end += 7*24*60*60*1000;
            }
        }
    }
    public void startAlgorithm(){

    }

    public Calendar getStartBound() {
        return startBound;
    }

    public void setStartBound(Calendar startBound) {
        this.startBound = startBound;
    }

    public Calendar getEndBound() {
        return endBound;
    }

    public void setEndBound(Calendar endBound) {
        this.endBound = endBound;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public ArrayList<Routine> getRoutines() {
        return routines;
    }

    public void setRoutines(ArrayList<Routine> routines) {
        this.routines = routines;
    }

    public ArrayList<RecommendDate> getRecomendDates() {
        return recommendDates;
    }

    public void setRecommendDates(ArrayList<RecommendDate> recommendDates) {
        this.recommendDates = recommendDates;
    }

    public int[][] getTable() {
        return table;
    }

    public void setTable(int[][] table) {
        this.table = table;
    }

    public int numberOfDayInMonth(int month) {
        return  0;
    }
}