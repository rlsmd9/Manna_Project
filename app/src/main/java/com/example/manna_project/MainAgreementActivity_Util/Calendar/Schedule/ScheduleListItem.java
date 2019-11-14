package com.example.manna_project.MainAgreementActivity_Util.Calendar.Schedule;

public class ScheduleListItem {
    private String title;
    private String place;
    private String startDate;
    private String endDate;
    private int img[];

    public ScheduleListItem(String title, String place, String startDate, String endDate) {
        this.title = title;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ScheduleListItem(String title, String place, String startDate, String endDate, int[] img) {
        this.title = title;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int[] getImg() {
        return img;
    }

    public void setImg(int[] img) {
        this.img = img;
    }
}