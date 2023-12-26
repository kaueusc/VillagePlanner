package com.dr34mt34m.v1ll4g3pl4nn3r;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.dr34mt34m.v1ll4g3pl4nn3r.SignInTest.signOut;

import static org.hamcrest.Matchers.containsString;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpTest {
    private static final String TAG = SignUpTest.class.getName();

    @Before
    public void setup() {
        try {
            signOut();
        } catch (NoMatchingViewException e) {

        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Navigate to sign up page
        onView(withId(R.id.signin_sign_up_btn)).perform(click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }

    @After
    public void after() {
//        SignInTest.signOut();
    }

    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule = new ActivityScenarioRule<>(SignInActivity.class);

    @Test
    public void testSignUpValid() {
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText("first name"));
        onView(withId(R.id.signup_last_name)).perform(scrollTo(), typeText("last name"));
        onView(withId(R.id.signup_email)).perform(scrollTo(), typeText("email@email.com"));
        onView(withId(R.id.signup_password)).perform(scrollTo(), typeText("password123!"));
        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());

        //wait for app to finish loading after sign-in
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete();
        }

        onView(withId(R.id.main_container)).check(matches(isDisplayed()));
        signOut();
    }

    private void testProfileMatchesHelper(String firstName, String lastName, String email) {
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText(firstName));
        onView(withId(R.id.signup_last_name)).perform(scrollTo(), typeText(lastName));
        onView(withId(R.id.signup_email)).perform(scrollTo(), typeText(email));
        onView(withId(R.id.signup_password)).perform(scrollTo(), typeText("password123!"));
        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());

        //wait for app to finish loading after sign-in
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.main_container)).check(matches(isDisplayed()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onView(withId(R.id.tab_profile)).perform(click());
            onView(withId(R.id.profile_email)).check(matches(withText(containsString(email))));
            onView(withId(R.id.profile_name)).check(matches(withText(containsString(firstName + " " + lastName))));

            user.delete();
        }

        signOut();
    }

    @Test
    public void testProfileMatches1() {
        testProfileMatchesHelper("first name", "last name", "email@email.com");
    }

    @Test
    public void testProfileMatches2() {
        testProfileMatchesHelper("john", "doe", "john@doe.com");
    }

    @Test
    public void testSignUpInvalid1() {
        // Submit without filling out anything
        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpInvalid2() {
        // Only fill first name out
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText("first name"));

        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpInvalid3() {
        // Only fill first name + last name
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText("first name"));
        onView(withId(R.id.signup_last_name)).perform(scrollTo(), typeText("last name"));

        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpInvalid4() {
        // Only fill first name + last name + email
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText("first name"));
        onView(withId(R.id.signup_last_name)).perform(scrollTo(), typeText("last name"));
        onView(withId(R.id.signup_email)).perform(scrollTo(), typeText("email@email.com"));

        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpInvalid5() {
        // Only fill first name + last name + invalid email
        onView(withId(R.id.signup_first_name)).perform(scrollTo(), typeText("first name"));
        onView(withId(R.id.signup_last_name)).perform(scrollTo(), typeText("last name"));
        onView(withId(R.id.signup_email)).perform(scrollTo(), typeText("bademail"));
        onView(withId(R.id.signup_password)).perform(scrollTo(), typeText("password123!"));

        onView(withId(R.id.signup_submit_btn)).perform(scrollTo(), click());
        onView(withId(R.id.signup_activity)).check(matches(isDisplayed()));
    }
}
