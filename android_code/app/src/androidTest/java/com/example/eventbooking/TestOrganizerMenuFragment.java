package com.example.eventbooking;

import static android.os.Trace.isEnabled;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.function.Predicate.not;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventbooking.Testing.SampleTable;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.waitinglist.ViewAcceptedListFragment;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import androidx.test.core.app.ActivityScenario;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestOrganizerMenuFragment {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
    );


    private ActivityScenario<MainActivity> scenario;
    private User user;
    private String testEventId = "eventID3";

    @Before
    public void disableAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                Settings.Global.putInt(
                        InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                        Settings.Global.TRANSITION_ANIMATION_SCALE, 0);
                Settings.Global.putInt(
                        InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                        Settings.Global.ANIMATOR_DURATION_SCALE, 0);
                Settings.Global.putInt(
                        InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                        Settings.Global.WINDOW_ANIMATION_SCALE, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Before
    public void setUp() {
        // Launch MainActivity
        scenario = ActivityScenario.launch(MainActivity.class);

        // Simulate navigation to OrganizerMenuFragment
        onView(withId(R.id.button_normal)) // Assuming this is the login button
                .perform(click());

        try {
            Thread.sleep(2000); // Wait for the main screen to load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Navigate to OrganizerMenuFragment by clicking on the "viewWaitingListButton"
        onView(withId(R.id.waitingListButton))
                .check(matches(isDisplayed()))
                .perform(click());
    }


private void initializeUserAndEvents() {
    // Set up test user
    user.setDeviceID("testingDeviceID100");
    user.setUsername("testUser");
    user.setEmail("testUser@example.com");
    user.setNotificationAsk(false);
    user.setGeolocationAsk(false);
    user.setdefaultProfilePictureUrl("testURL");
    user.setProfilePictureUrl("testURL");
    List<String> roles = new ArrayList<>();
    roles.add(Role.ENTRANT);
    roles.add(Role.ORGANIZER);
    user.setRoles(roles);

    // Set up test data
    SampleTable testTable = new SampleTable();
    testTable.makeUserList();
    testTable.makeFacilityList();
    testTable.makeEventList();
    testTable.makeImageList();

    UniversalProgramValues.getInstance().setEventList(testTable.getEventList());
    UniversalProgramValues.getInstance().setUserList(testTable.getUserList());
    UniversalProgramValues.getInstance().setFacilityList(testTable.getFacilityList());
    UniversalProgramValues.getInstance().setImageList(testTable.getImageList());

    // Set current user
    UniversalProgramValues.getInstance().setExistingLogin(true);
    UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
    UniversalProgramValues.getInstance().setCurrentUser(user);
}

@Test
public void testUIComponentsAreDisplayed() {
    onView(withId(R.id.waitingListButton)).check(matches(isDisplayed()));
    onView(withId(R.id.sampleAttendeesButton)).check(matches(isDisplayed()));
    onView(withId(R.id.SignedParticipantButton)).check(matches(isDisplayed()));
    onView(withId(R.id.canceledParticipantButton)).check(matches(isDisplayed()));
    onView(withId(R.id.DrawReplacementButton)).check(matches(isDisplayed()));
    onView(withId(R.id.BackToButton)).check(matches(isDisplayed()));
    onView(withId(R.id.accptedParticipantButton)).check(matches(isDisplayed()));
    onView(withId(R.id.generate_qr_code)).check(matches(isDisplayed()));
    onView(withId(R.id.cancel_entrant)).check(matches(isDisplayed()));
    onView(withId(R.id.button_remove_poster)).check(matches(isDisplayed()));
    onView(withId(R.id.button_upload_poster)).check(matches(isDisplayed()));
    onView(withId(R.id.poster_image_view)).check(matches(isDisplayed()));
}

@Test
public void testNavigateToViewWaitingList() {
        // Navigate to ViewWaitingListFragment
        onView(withId(R.id.waitingListButton)).perform(click());

        try {
            Thread.sleep(1000); // Wait for the fragment to load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the waiting list is displayed
        onView(withId(R.id.waiting_list_view)).check(matches(isDisplayed()));
    }


@Test

public void testNavigateToViewAcceptedList() {
    // Navigate to ViewAcceptedListFragment
    onView(withId(R.id.accptedParticipantButton)).perform(click());

    try {
        Thread.sleep(1000); // Wait for the fragment to load
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Verify that the accepted participant list is displayed
    onView(withId(R.id.accepted_list_view)).check(matches(isDisplayed()));
}

@Test
public void testGenerateQRCode() {
    onView(withId(R.id.generate_qr_code)).perform(click());

    try {
        Thread.sleep(1000); // Wait for QR code generation
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    onView(withId(R.id.QR_image)).check(matches(isDisplayed()));
}

@Test
public void testUploadPosterButton() {
    Intents.init();
    try {
        onView(withId(R.id.button_upload_poster)).perform(click());

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_PICK));
    } finally {
        Intents.release();
    }
}

@Test
public void testRemovePosterButton() {
    onView(withId(R.id.button_remove_poster)).perform(click());

    try {
        Thread.sleep(500); // Wait for poster removal
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Verify that the poster image view displays the placeholder image
    // Assuming placeholder_image_foreground is the default image
    onView(withId(R.id.poster_image_view))
            .check(matches(isDisplayed()));
    // Additional checks for the image can be added if necessary
}

@Test
public void testSampleAttendeesButton() {
    onView(withId(R.id.sampleAttendeesButton)).perform(click());

    try {
        Thread.sleep(500); // Wait for action to complete
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

}

@Test
public void testDrawReplacementButton() {
    onView(withId(R.id.DrawReplacementButton)).perform(click());

    try {
        Thread.sleep(500); // Wait for dialog
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Enter number in the dialog
    onView(withClassName(Matchers.equalTo(EditText.class.getName())))
            .perform(typeText("2"), closeSoftKeyboard());

    // Click "OK"
    onView(withText("OK")).perform(click());

    try {
        Thread.sleep(500); // Wait for action to complete
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Verify that replacements were drawn (additional checks can be added)
}

@Test
public void testCancelEntrant() {
    onView(withId(R.id.cancel_entrant)).perform(click());

    try {
        Thread.sleep(500); // Wait for action to complete
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Optionally verify Toast message or UI changes
}

@After
public void tearDown() {
    UniversalProgramValues.getInstance().resetInstance();
}



}


