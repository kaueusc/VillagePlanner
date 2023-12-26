package com.dr34mt34m.v1ll4g3pl4nn3r.components;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;


public class Reminder {
    private String id;
    private String place;
    private String message;
//    private int year;
//    private int day;
//    private int month;
//    private int hour;
//    private int minute;
    private Timestamp timestamp;

    public Reminder() {
    }

    public Reminder(String place, String message, int year, int day, int month, int hour, int minute) {
        this.place = place;
        this.message = message;

//        this.year = year;
//        this.day = day;
//        this.month = month;
//        this.hour = hour;
//        this.minute = minute;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute,0);
        Date date = cal.getTime();
        timestamp = new Timestamp(date);
    }

    public Reminder(String place, String message, Timestamp timestamp){
        this.place = place;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() { return timestamp; }

//    public int getYear() {
//        return year;
//    }
//
//    public int getDay() {
//        return day;
//    }
//
//    public int getMonth() {
//        return month;
//    }
//
//    public int getHour() {
//        return hour;
//    }
//
//    public int getMinute() {
//        return minute;
//    }
}
