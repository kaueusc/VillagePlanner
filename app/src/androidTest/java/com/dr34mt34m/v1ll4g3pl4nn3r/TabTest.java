package com.dr34mt34m.v1ll4g3pl4nn3r;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TabTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityScenarioRule
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
    public void Tabbing(){
        //Upon logging in, start off in map page. Check map is displayed.
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        //Navigate to Reminder tab, check to see CreateReminderButton is displayed
        onView(withId(R.id.tab_reminders)).perform(click());
        onView(withId(R.id.CreateReminderButton)).check(matches(isDisplayed()));

        //Navigate to profile tab, check to see signoutbtn is displayed
        onView(withId(R.id.tab_profile)).perform(click());
        onView(withId(R.id.profile_sign_out_btn)).check(matches(isDisplayed()));

        //Tab back to map page, check map is displayed.
        onView(withId(R.id.tab_map)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}
