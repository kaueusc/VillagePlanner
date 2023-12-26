package com.dr34mt34m.v1ll4g3pl4nn3r;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignInTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule = new ActivityScenarioRule<>(SignInActivity.class);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    public static void signIn(String email, String password) {
        try {
            signOut();
        } catch (NoMatchingViewException e) {

        }

        onView(withId(R.id.signin_email)).perform(scrollTo(), typeText(email));
        onView(withId(R.id.signin_password)).perform(scrollTo(), typeText(password));
        onView(withId(R.id.signin_submit_btn)).perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void signOut() {
        onView(withId(R.id.tab_profile)).perform(click());
        onView(withId(R.id.profile_sign_out_btn)).perform(click());
    }

    @Test
    public void testValidSignIn() {
        signIn("Espresso@gmail.com", "testEspresso");
        onView(withId(R.id.main_container)).check(matches(isDisplayed()));
        signOut();
    }

    @Test
    public void testInvalidSignIn() {
        signIn("Espresso@gmail.com", "wrongPassword");
        onView(withId(R.id.signin_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testMissingFields1() {
        signIn("", "");
        onView(withId(R.id.signin_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testMissingFields2() {
        signIn("Espresso@gmail.com", "");
        onView(withId(R.id.signin_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testMissingFields3() {
        signIn("", "testEspresso");
        onView(withId(R.id.signin_activity)).check(matches(isDisplayed()));
    }
}
