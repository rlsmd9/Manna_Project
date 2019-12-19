package com.example.manna_project.MainAgreementActivity_Util.Calendar;



import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

public class EventVO extends RealmObject {
    private String mId;
    private String mSummery;
    private String mDescription;
    private String mLocation;
    private String mEtag;
    private String mUpdated;
    private String mStart;
    private String mEnd;

    public EventVO() {
    }

    public void setEventVO(Event event) {
        this.mId = event.getId();
        this.mSummery = event.getSummary();
        this.mDescription = event.getDescription();
        this.mLocation = event.getLocation();
        this.mEtag = event.getEtag();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.mUpdated = simpleDateFormat.format(new Date(event.getUpdated().getValue()));
        if (event.getStart().getDate() == null)
            this.mStart = simpleDateFormat.format(new Date(event.getStart().getDateTime().getValue()));
        else
            this.mStart = simpleDateFormat.format(new Date(event.getStart().getDate().getValue()));

        if (event.getEnd().getDate() == null)
            this.mEnd = simpleDateFormat.format(new Date(event.getEnd().getDateTime().getValue()));
        else
            this.mEnd = simpleDateFormat.format(new Date(event.getEnd().getDate().getValue()));
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmSummery() {
        return mSummery;
    }

    public void setmSummery(String mSummery) {
        this.mSummery = mSummery;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmEtag() {
        return mEtag;
    }

    public void setmEtag(String mEtag) {
        this.mEtag = mEtag;
    }

    public String getmUpdated() {
        return mUpdated;
    }

    public void setmUpdated(String mUpdated) {
        this.mUpdated = mUpdated;
    }

    public String getmStart() {
        return mStart;
    }

    public void setmStart(String mStart) {
        this.mStart = mStart;
    }

    public String getmEnd() {
        return mEnd;
    }

    public void setmEnd(String mEnd) {
        this.mEnd = mEnd;
    }

    public Event getEvent() {
        Event event =  new Event();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        event.setId(mId);
        event.setSummary(mSummery);
        event.setDescription(mDescription);
        event.setLocation(mLocation);
        event.setEtag(mEtag);

        try {
            event.setStart(new EventDateTime().setDateTime(new DateTime(simpleDateFormat.parse(mStart))));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(simpleDateFormat.parse(mEnd))));
            event.setUpdated(new DateTime(simpleDateFormat.parse(mUpdated)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return event;
    }

    @Override
    public String toString() {
        return "EventVO{" +
                "mId='" + mId + '\'' +
                ", mSummery='" + mSummery + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mEtag='" + mEtag + '\'' +
                ", mUpdated='" + mUpdated + '\'' +
                ", mStart='" + mStart + '\'' +
                ", mEnd='" + mEnd + '\'' +
                '}';
    }
}
