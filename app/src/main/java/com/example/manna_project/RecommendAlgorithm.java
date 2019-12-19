package com.example.manna_project;

import android.util.Log;

import com.example.manna_project.MainAgreementActivity_Util.RecommendDate.RecommendDate;
import com.example.manna_project.MainAgreementActivity_Util.Routine;
import com.example.manna_project.MainAgreementActivity_Util.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class RecommendAlgorithm {
    public static final String TAG = "MANNA_YC";
    private Calendar startBound;
    private Calendar endBound;
    private ArrayList<Schedule> schedules;
    private ArrayList<Routine> routines;
    private ArrayList<RecommendDate> recommendDates;
    private int table[][];
    private int totalDay;
    private static final long oneMinute = 60*1000;
    private static final long oneHour = 60*60*1000;
    private static final long oneDay = 24*60*60*1000;
    private static final long oneWeek = 7*24*60*60*1000;
    private static final int maxBound = 1;

       public RecommendAlgorithm(Calendar startBound, Calendar endBound) {
        this.startBound = startBound;
        this.endBound = endBound;
        this.totalDay = endBound.get(Calendar.DAY_OF_YEAR) - startBound.get(Calendar.DAY_OF_YEAR)+1;
        if (endBound.get(Calendar.YEAR) != startBound.get(Calendar.YEAR))
            totalDay += 365;
        this.table = new int[totalDay][24];
    }

    public RecommendAlgorithm(ArrayList<Schedule> schedules, ArrayList<Routine> routines) {
        this.schedules = schedules;
        this.routines = routines;
    }

    public void routinesToSchedules() {
        for (Routine routine : routines) {
            int day = routine.getDay() +1;
            Calendar temp = (Calendar) startBound.clone();
            if (startBound.get(Calendar.DAY_OF_WEEK)>day) {
                temp.add(Calendar.DAY_OF_MONTH,7);
                temp.set(Calendar.DAY_OF_WEEK,day);
            }
            else
                temp.set(Calendar.DAY_OF_WEEK,day);

            temp.set(Calendar.HOUR_OF_DAY, routine.getStartTime());
            temp.set(Calendar.MINUTE, 0);
            temp.set(Calendar.SECOND, 0);
            temp.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long start = temp.getTimeInMillis();
            temp.set(Calendar.HOUR_OF_DAY, routine.getEndTime());
            long end = temp.getTimeInMillis();
           //  Log.d(TAG, "루틴 to schedule  start "+simpleDateFormat.format(new Date(start))+"// end "+simpleDateFormat.format(new Date(end)));
            while (start < endBound.getTimeInMillis()) {
                if (end > endBound.getTimeInMillis()) {
                    end = endBound.getTimeInMillis();
                }
                schedules.add(new Schedule(start, end));
                start += oneWeek;
                end += oneWeek;
            }
        }
    }

    public void startAlgorithm() {
        routinesToSchedules();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.d(TAG,"시작 범위  "+simpleDateFormat.format(startBound.getTime()));
        Log.d(TAG,"끝 범위  "+simpleDateFormat.format(endBound.getTime()));
        Log.d(TAG, "Tatal day "+totalDay);

        long startBoundMil = startBound.getTimeInMillis();
        for (Schedule schedule : schedules) {
            long gap = schedule.getStartTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(gap);
            Log.d(TAG, "스케쥴 start "+simpleDateFormat.format(schedule.getStartCalendar().getTime())+"// end "+simpleDateFormat.format(schedule.getEndCalendar().getTime()));
            while (gap < schedule.getEndTime()) {
                int day = (int)((gap - startBoundMil) / oneDay);
                Log.d(TAG, "start와 날짜 차이 : "+day);
                calendar.setTimeInMillis(gap);
              //  Log.d(TAG, "day : "+day+"    //  hour : "+gap/60);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                Log.d(TAG, "Hour : "+hour);
                table[day][hour]++;
                gap += 60 * 60 * 1000;
            }
        }
        for(int i=0;i<totalDay;i++)
            Log.d(TAG, Arrays.toString(table[i]));

        recommendDates = new ArrayList<>();
        int standard = 0;
        Calendar initCal = (Calendar)startBound.clone();
        initCal.set(Calendar.MINUTE,0);
        initCal.set(Calendar.SECOND,0);
        initCal.set(Calendar.MILLISECOND,0);
        while (standard <= maxBound) {
            for (int i = 0; i < totalDay; i++) {
                if(recommendDates.size()>100)
                    return;
                for (int j = 8; j < 22; j++) {
                    if (table[i][j] == standard) {
                        int k = j + 1;
                        int l = j-1;
                        while (k < 24) {
                            if (table[i][k] > standard)
                                break;
                            k++;
                        }
                        while(l>=8){
                            if(table[i][l] >=standard)
                                break;
                            l--;
                        }
                        Calendar start, end;
                        start = (Calendar) initCal.clone();
                        start.add(Calendar.DAY_OF_MONTH, i);
                        start.set(Calendar.HOUR_OF_DAY, j);
                        end = (Calendar) initCal.clone();
                        end.add(Calendar.DAY_OF_MONTH, i);
                        end.set(Calendar.HOUR_OF_DAY, k );
                        recommendDates.add(new RecommendDate(start, end));
                        j = k;
                    }
                }
            }
            standard++;
        }
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

    public int[][] getTable() {
        return table;
    }

    public void setTable(int[][] table) {
        this.table = table;
    }

    public ArrayList<RecommendDate> getRecommendDates() {
        return recommendDates;
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }
}
