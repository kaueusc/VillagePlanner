package com.dr34mt34m.v1ll4g3pl4nn3r;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;


import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Reminder;
import com.dr34mt34m.v1ll4g3pl4nn3r.components.User;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;

import java.util.Calendar;
import java.util.Date;

public class CreateReminderActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);
    }

    int myYear = -1;
    int myday = -1;
    int myMonth = -1;

    int myHour = -1;
    int myMinute = -1;

    public void onCreateClick(View v) {
        if (myYear == -1 || myHour == -1) {
            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateReminderActivity.this);
            // Set the message
            builder.setMessage("Fill out date and time to set reminder.");
            // Set Alert Title
            builder.setTitle("Date or Time not set yet!");
            // Set Ok button
            builder.setPositiveButton("OK",null);
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();

            return;
        }

        long currentTime = Calendar.getInstance().getTime().getTime();
        long one_minute = 1*60*1000;
        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(myYear,myMonth,myday,myHour,myMinute,0);
        long reminderTime = reminderCalendar.getTime().getTime();
        //currentTime is ahead of reminderTime by two minutes
        //= Trying to set reminder for two minutes ago or before
        //If trying to do so, set Alert and don't create.
        if(currentTime-reminderTime >= 2*one_minute){
            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateReminderActivity.this);
            // Set the message
            builder.setMessage("Fill out date and time with more recent time values.");
            // Set Alert Title
            builder.setTitle("Cannot set Reminder in the past!");
            // Set Ok button
            builder.setPositiveButton("OK",null);
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();

            return;
        }


        Spinner mySpinner = findViewById(R.id.PlaceDropdown);
        String place = mySpinner.getSelectedItem().toString();

        EditText myMessage = findViewById(R.id.MessageText);
        String message = myMessage.getText().toString();

        //Instead of sending date & time object, get values of year, day, month, hour, & minute.

        // Send this reminder to firebase
        Reminder r = new Reminder(place, message, myYear, myday, myMonth, myHour, myMinute);
        FirebaseHelper.createReminder(r);

        //After sending, go back to viewReminders
        Intent intent = new Intent();
        setResult(0,intent);
        this.finish();
    }

    public void onCancelClick(View v) {
        Intent intent = new Intent();
        setResult(1,intent);
        this.finish();
    }

    public void onDateClick(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateReminderActivity.this, CreateReminderActivity.this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        myYear = year;
        myday = day;
        myMonth = month;

        EditText myDate = findViewById(R.id.editTextDate);
        //Increment month by one to get correct display month
        myDate.setText((month+1)+"/"+day+"/"+year);
    }

    public void onTimeClick(View v) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateReminderActivity.this, CreateReminderActivity.this, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        String ampm = "am";
        int displayHour = myHour;
        if (displayHour == 0) {
            displayHour = 12;
        } else if (displayHour > 12) {
            displayHour -= 12;
            ampm = "pm";
        }

        EditText myTime = findViewById(R.id.editTextTime);
        myTime.setText(String.format("%02d:%02d %s", displayHour, myMinute, ampm));
    }
}