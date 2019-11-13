package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;

public class EventsOfDay {
    Events events;

    public EventsOfDay(Events events) {
        this.events = events;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }
}
