package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;

import android.os.SystemClock;
import android.widget.ScrollView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestProfile {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testNavToProfile() {
        // Because this is not the first test for the login and therefore
        // Login already occurs
        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));
//        onView(withText("Welcome")).check(matches(isDisplayed()));
        SystemClock.sleep(5000);
        onView(withId(R.id.home_title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_profile)).perform(click());
        //onView(withId(R.id.button_edit_profile)).check(matches(isDisplayed()));
        onView(withClassName(containsString(ScrollView.class.getSimpleName()))).perform(swipeUp());
        SystemClock.sleep(1000);
        onView(withId(R.id.button_back_home)).perform(click());
        onView(withId(R.id.home_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfile() {
//        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));
//        SystemClock.sleep(5000);
//        onView(withId(R.id.home_title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_profile)).perform(click());
        //onView(withId(R.id.button_edit_profile)).perform(click());
        onView(withId(R.id.edit_full_name)).perform(clearText(), typeText("test1"), closeSoftKeyboard());
        onView(withClassName(containsString(ScrollView.class.getSimpleName()))).perform(swipeUp());
        //onView(withId(R.id.button_save_profile)).perform(scrollTo());
        onView(withId(R.id.button_sign_in)).perform(click());
        //onView(withId(R.id.edit_name)).check(matches(withText("test1")));
    }

}
