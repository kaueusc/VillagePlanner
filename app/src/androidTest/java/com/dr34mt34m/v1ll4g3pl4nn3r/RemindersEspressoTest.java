package com.dr34mt34m.v1ll4g3pl4nn3r;

import android.app.Activity;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;


public class RemindersEspressoTest {
    @Rule public ActivityScenarioRule<SignInActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SignInActivity.class);
    //Login and logout needed to test reminders
    @Before
    public void Login(){
        //assumes user is in SignInActivity; thus USER MUST BE LOGGED OUT.
        onView(withId(R.id.signin_email)).check(matches(isDisplayed()));

        onView(withId(R.id.signin_email)).perform(typeText("Espresso@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.signin_password)).perform(typeText("testEspresso"), closeSoftKeyboard());
        onView(withId(R.id.signin_submit_btn)).perform(click());

        //wait for app to finish loading after sign-in
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @After
    public void Logout(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //assumes user is in mainActivity; this USER MUST STILL BE LOGGED IN
        onView(withId(R.id.tab_profile)).check(matches(isDisplayed()));

        //dont use first one
        //onView( allOf( withId(R.id.tab_profile), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );

        onView(withId(R.id.tab_profile)).perform(click());
        onView(withId(R.id.profile_sign_out_btn)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CreateReminder(){
        //not best implementation but it works since Reminders is in the middle
        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());
        //This also works, but now we can just click on tab_reminders withId.
        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
        onView(withId(R.id.tab_reminders)).perform(click());

        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Trader Joe's"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Buy a lot of food for party tomorrow!"), closeSoftKeyboard());

        //Create reminder for current time
        onView(withId(R.id.editTextDate)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2022, 11, 30));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        //onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(17,53));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check to see Reminder was created. Most likely check if View withText("message") exists (or just check id)
//        onView(withText("Buy a lot of food for party tomorrow!")).check(matches(isDisplayed()));
        onView(withId(R.id.MessageView)).check(matches(isDisplayed()));

        //wait 9 more seconds to see AlertDialog
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check if alertdialog is displayed, and hit OK
        //Cannot check the text within message itself, can only identify the OK
        //onView(withText("Time to leave")).check(matches(isDisplayed()));
        onView(withText("OK")).check(matches(isDisplayed())).perform(click());

        //Check to see Reminder is not there anymore.
        onView(withId(R.id.MessageView)).check(doesNotExist());
    }

    @Test
    public void NoCreateMissingFieldsReminder(){
        //not best implementation but it works since Reminders is in the middle
        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());

        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
        onView(withId(R.id.tab_reminders)).perform(click());

        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Trader Joe's"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("OK"), closeSoftKeyboard());

        //Don't choose date or time

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        //Check to see AlertDialog was created. Most likely check for OK button
        onView(withText("OK")).check(matches(isDisplayed())).perform(click());
        //Check we're still in create reminder
        onView(withId(R.id.SubmitCreateReminderButton)).check(matches(isDisplayed()));
        onView(withId(R.id.CancelReminder)).perform(click());
    }

    @Test
    public void NoCreatePastReminder(){
        //not best implementation but it works since Reminders is in the middle
        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());

        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
        onView(withId(R.id.tab_reminders)).perform(click());

        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Trader Joe's"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Past!"), closeSoftKeyboard());

        //Create Reminder one month from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        int myYear = calendar.get(Calendar.YEAR);
        int myMonth = calendar.get(Calendar.MONTH);
        int myDay = calendar.get(Calendar.DATE);
        int myHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mySubtractTwoMinutes = calendar.get(Calendar.MINUTE);

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(myYear, myMonth, myDay));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(myHour,mySubtractTwoMinutes));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        //Check to see AlertDialog was created. Most likely check for OK button
        onView(withText("OK")).check(matches(isDisplayed())).perform(click());
        //Check we're still in create reminder
        onView(withId(R.id.SubmitCreateReminderButton)).check(matches(isDisplayed()));
        onView(withId(R.id.CancelReminder)).perform(click());
    }

    @Test
    public void DeleteReminder(){
        //not best implementation but it works since Reminders is in the middle
        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());

        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
        onView(withId(R.id.tab_reminders)).perform(click());

        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Target"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Delete"), closeSoftKeyboard());

        //Create Reminder one month from now
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        //month is 0 indexed, while pickeractions is 1 indexed
        //If nextmonth=13, PickerActions will help go to next year.
        int nextmonth = (calendar.get(Calendar.MONTH)+1)+1;

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, nextmonth, 25));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(16,53));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check Reminder exists
        //onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withId(R.id.MessageView)).check(matches(isDisplayed()));

        //Now delete the Reminder
        onView(withId(R.id.DeleteButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check see that Reminder does not exist anymore
        onView(withId(R.id.MessageView)).check(doesNotExist());
    }

//    @Test
//    public void Create2Reminders(){
//        //not best implementation but it works since Reminders is in the middle
//        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());
//
//        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
//        onView(withId(R.id.tab_reminders)).perform(click());
//
//        onView(withId(R.id.CreateReminderButton)).perform(click());
//        onView(withId(R.id.PlaceDropdown)).perform(click());
//        onData(allOf(is(instanceOf(String.class)), is("CAVA"))).perform(click());
//        onView(withId(R.id.MessageText)).perform(typeText("First Reminder"), closeSoftKeyboard());
//
//        onView(withId(R.id.editTextDate)).perform(click());
//        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2022, 11, 30));
//        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'
//
//        onView(withId(R.id.editTextTime)).perform(click());
//        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setTime(17,53));
//        onView(withId(android.R.id.button1)).perform(click());
//
//        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());
//
//        //Second reminder
//        onView(withId(R.id.CreateReminderButton)).perform(click());
//
//        onView(withId(R.id.PlaceDropdown)).perform(click());
//        onData(allOf(is(instanceOf(String.class)), is("Insomnia Cookies"))).perform(click());
//        onView(withId(R.id.MessageText)).perform(typeText("Yum cookies"), closeSoftKeyboard());
//
//        onView(withId(R.id.editTextDate)).perform(click());
//        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2022, 11, 30));
//        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'
//
//        onView(withId(R.id.editTextTime)).perform(click());
//        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setTime(17,53));
//        onView(withId(android.R.id.button1)).perform(click());
//
//        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());
//
//        //TOD: Check to see both Reminders created. Most likely check if View withText("message") exists
//
//        //wait 10 seconds to see AlertDialogs
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //TOD: Check to see AlertDialog was created. Most likely check for OK button
//        //unable to check if alertdialog is displayed
////        onView(withText("Time to leave")).check(matches(isDisplayed()));
//
//        //We have 2 reminders go off, so click "ok" twice.
//        onView(withId(android.R.id.button1)).perform(click());
//        onView(withId(android.R.id.button1)).perform(click());
//        //onView(withText("OK")).perform(click());
//
//        //TOD: Check to see Reminder is not there anymore.
//    }

    @Test
    public void Delete2Reminders(){
        //not best implementation but it works since Reminders is in the middle
        //onView(withId(R.id.main_bottom_navigation_view)).perform(click());

        //onView( allOf( withId(R.id.tab_reminders), isDescendantOfA(withId(R.id.main_bottom_navigation_view)) )).perform( click() );
        onView(withId(R.id.tab_reminders)).perform(click());

        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CAVA"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Del First R"), closeSoftKeyboard());

        //Create Reminder one year from now
        Calendar calendar = Calendar.getInstance();
        int nextyear = calendar.get(Calendar.YEAR)+1;
        int month = (calendar.get(Calendar.MONTH)+1);

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(nextyear, month, 2));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        //onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(0,59));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        //Second reminder
        onView(withId(R.id.CreateReminderButton)).perform(click());

        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Insomnia Cookies"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("cookies message"), closeSoftKeyboard());

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(nextyear, month, 2));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        //onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(0,59));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check both Reminders are displayed
        onView(withText("Del First R")).check(matches(isDisplayed()));
        onView(withText("cookies message")).check(matches(isDisplayed()));

        //Now delete the Reminders
        //To determine which one to click, check the messagetext of the reminder to determine
        onView(allOf(
                withId(R.id.DeleteButton),
                //withParent(withChild(withText("CAVA")))))
                withParent(withChild(withText("Del First R")))))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(allOf(
                withId(R.id.DeleteButton),
                withParent(withChild(withText("cookies message")))))
                .check(matches(isDisplayed()))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check that neither reminders exist anymore
        onView(withText("Del First R")).check(doesNotExist());
        onView(withText("cookies message")).check(doesNotExist());
    }

    @Test
    public void Create2Delete1Reminder() {

        onView(withId(R.id.tab_reminders)).perform(click());

        //Create first reminder one year from now
        Calendar calendar = Calendar.getInstance();
        int nextyear = calendar.get(Calendar.YEAR)+1;
        int month = (calendar.get(Calendar.MONTH)+1);

        onView(withId(R.id.CreateReminderButton)).perform(click());

        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Insomnia Cookies"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Del 1 R"), closeSoftKeyboard());

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(nextyear, month, 2));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        //onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(0,59));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        //Create Second Reminder now
        onView(withId(R.id.CreateReminderButton)).perform(click());
        onView(withId(R.id.PlaceDropdown)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CAVA"))).perform(click());
        onView(withId(R.id.MessageText)).perform(typeText("Keep Second R"), closeSoftKeyboard());

        onView(withId(R.id.editTextDate)).perform(click());
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(nextyear, month, 2));
        onView(withId(android.R.id.button1)).perform(click()); //should click 'ok'

        onView(withId(R.id.editTextTime)).perform(click());
        //onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(0,59));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.SubmitCreateReminderButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check both Reminders are displayed
        onView(withText("Del 1 R")).check(matches(isDisplayed()));
        onView(withText("Keep Second R")).check(matches(isDisplayed()));

        //Delete First Reminder
        onView(allOf(
                withId(R.id.DeleteButton),
                withParent(withChild(withText("Del 1 R")))))
                .perform(click());

        //Check first reminder doesn't exist
        onView(withText("Del First R")).check(doesNotExist());

        //Wait 9 more seconds
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check if alertdialog is displayed, and hit OK
        onView(withText("OK")).check(matches(isDisplayed())).perform(click());

        //Check to see Reminder is not there anymore.
        onView(withText("Keep Second R")).check(doesNotExist());
    }
}
