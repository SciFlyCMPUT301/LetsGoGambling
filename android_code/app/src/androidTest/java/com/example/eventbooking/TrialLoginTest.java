package com.example.eventbooking;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TrialLoginTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    private LoginFragment loginFragment;
    private User user;
    private ActivityScenario<MainActivity> scenario;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
    );

    @Before
    public void setUp() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("testingMode", true);  // Add a boolean
        bundle.putString("testingDeviceID", "testingDeviceID100");
//        scenario = activityRule.getScenario();
        // Create an instance of LoginFragment
        loginFragment = new LoginFragment();
        user = new User();

        // Launch the fragment
//        scenario.onActivity(activity -> {
//            loginFragment = new LoginFragment();
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, loginFragment)
//                    .commitNow(); // Ensure fragment is attached immediately
//        });

    }




    @Test
    public void testNewUserWithoutEventID() throws UiObjectNotFoundException{
        UniversalProgramValues.getInstance().setExistingLogin(false);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
//        UniversalProgramValues.getInstance().setCurrentUser(user);
        scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("testingMode", true);
//            bundle.putString("testingDeviceID", "testingDeviceID100");
//            Log.d("Login Trial", "Putting data into bundle");
//            loadingFragment.setArguments(bundle);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });


        // Wait for the fragment to load
        Espresso.onIdle();




        // Simulate clicking the Normal button
        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.text_login_welcome))
                .check(matches(withText("Welcome new user")));

        try {
            Thread.sleep(3000); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Simulate entering a username
        onView(withId(R.id.edit_name)).perform(typeText("new_user"));
        onView(withId(R.id.edit_email)).perform(typeText("new_user@example.com"));
//        onView(withId(R.id.edit_phone)).perform(typeText("new_user"));

        // Simulate confirming the user as new and navigating to ProfileCreation
        onView(withId(R.id.button_save_profile)).perform(click());
        // Assert that the welcome text reflects a new user
        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document("testingDeviceID100");
//        userRef.delete();

    }

    @Test
    public void testExistingUserWithEventID() {

//            newDeviceId = testing_device_id;
        user.setDeviceID("testingDeviceID100");
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setNotificationAsk(false);
        user.setGeolocationAsk(false);
        List<String> addRoles = new ArrayList<>();
        addRoles.add(Role.ENTRANT);
        addRoles.add(Role.ORGANIZER);
        addRoles.add(Role.ADMIN);
        user.setRoles(addRoles);
        Event event = new Event();
        event.setEventId("event123");
        event.setEventTitle("Event Title ");
        event.setDescription("Description for event ");
        event.setTimestamp(System.currentTimeMillis() + 100000);
        event.setMaxParticipants(20);
        event.setOrganizerId("testDeviceID10002");
        event.setLocation("testAddress");
        event.setSignedUpParticipantIds(new ArrayList<>());
        event.setWaitingParticipantIds(new ArrayList<>());
        event.setAcceptedParticipantIds(new ArrayList<>());
        event.setCanceledParticipantIds(new ArrayList<>());
        QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
        String hashInput = event.getEventId() + Calendar.getInstance().getTime();
        String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
        event.setQRcodeHash(qrCodeHash);
        UniversalProgramValues.getInstance().setCurrentEvent(event);
//        user.saveUserDataToFirestore();
        // Simulate entering the LoginFragment with an event ID passed via arguments
//        LoginFragment fragment = new LoginFragment();
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setCurrentUser(user);
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setEventList(new ArrayList<>());
        scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            LoginFragment loginArgs = LoginFragment.newInstance("event123");
//            loginFragment = new LoginFragment();
//            Bundle bundle = new Bundle();
////            bundle.putBoolean("testingMode", true);
////            bundle.putString("testingDeviceID", "testingDeviceID100");
//            bundle.putString("eventIdFromQR", "event123");
//            loginFragment.setArguments(bundle);
            Log.d("Login Trial", "Putting data into bundle");
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginArgs)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        // Wait for the fragment to load
        Espresso.onIdle();

        // Simulate clicking the Normal button
        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        // Simulate logging in with a predefined user document ID
//        onView(withId(R.id.input_document_id)).perform(typeText("existing_user_id"));
//        try {
//            Thread.sleep(3000); // Pause for 3 seconds
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // Simulate navigating to EventViewFragment
//        onView(withId(R.id.button_set_by_document_id)).perform(click());
        // Assert that the welcome text reflects the user's name (mocked)
//        onView(withId(R.id.text_login_welcome))
//                .check(matches(withText("Welcome back, existing_user_id")));
//        onView(withId(R.id.event_title_text))
//                .check(matches(withText("Event Name")));
        onView(withId(R.id.event_title_text))
                .check(matches(isDisplayed()));

        onView(withText("Join Waitlist"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Join Waitlist"))
                .perform(ViewActions.click());

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document("testingDeviceID100");
//        userRef.delete();
    }

    @Test
    public void testTestModeActivation() {
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setExistingLogin(true);
//        UniversalProgramValues.getInstance().setCurrentUser(user);
        // Simulate entering the LoginFragment
        scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            loginFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        // Wait for the fragment to load
        Espresso.onIdle();

        // Activate Test Mode
        onView(withId(R.id.button_test_mode)).perform(click());

        // Assert that Test Mode UI is visible
        onView(withId(R.id.test_mode_layout))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNewUserWithEventID() throws UiObjectNotFoundException{
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setExistingLogin(false);
        UniversalProgramValues.getInstance().setEventList(new ArrayList<>());
        Event event = new Event();
        event.setEventId("event123");
        event.setEventTitle("Event Title ");
        event.setDescription("Description for event ");
        event.setTimestamp(System.currentTimeMillis() + 100000);
        event.setMaxParticipants(20);
        event.setOrganizerId("testDeviceID10002");
        event.setLocation("testAddress");
        QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
        String hashInput = event.getEventId() + Calendar.getInstance().getTime();
        String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
        event.setQRcodeHash(qrCodeHash);
        UniversalProgramValues.getInstance().setCurrentEvent(event);
//        UniversalProgramValues.getInstance().setCurrentUser(user);
        scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            LoginFragment loginArgs = LoginFragment.newInstance("event123");

//            LoginFragment loadingFragment = new LoginFragment();
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("testingMode", true);
//            bundle.putString("testingDeviceID", "testingDeviceID100");
//            bundle.putString("eventIdFromQR", "event123");
//            Log.d("Login Trial", "Putting data into bundle");
//            loadingFragment.setArguments(bundle);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginArgs)
                    .commitNow(); // Ensure fragment is attached immediately
        });


        // Wait for the fragment to load
        Espresso.onIdle();




        // Simulate clicking the Normal button
        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.text_login_welcome))
                .check(matches(withText("Welcome new user")));

        try {
            Thread.sleep(3000); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Simulate entering a username
        onView(withId(R.id.edit_name)).perform(typeText("new_user"));
        onView(withId(R.id.edit_email)).perform(typeText("new_user@example.com"));
//        onView(withId(R.id.edit_phone)).perform(typeText("new_user"));

        // Simulate confirming the user as new and navigating to ProfileCreation
        onView(withId(R.id.button_save_profile)).perform(click());
        // Assert that the welcome text reflects a new user
        try {
            Thread.sleep(5000); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.event_title_text))
                .check(matches(isDisplayed()));
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document("testingDeviceID100");
//        userRef.delete();

    }

    @Test
    public void testLoginWithoutEventID() {
        User passingUser = new User();
//            newDeviceId = testing_device_id;
        passingUser.setDeviceID("testingDeviceID100");
        passingUser.setUsername("testUser");
        passingUser.setEmail("testUser@example.com");
        passingUser.setNotificationAsk(false);
        passingUser.setGeolocationAsk(false);
        List<String> addRoles = new ArrayList<>();
        addRoles.add(Role.ENTRANT);
        addRoles.add(Role.ORGANIZER);
        addRoles.add(Role.ADMIN);
        passingUser.setRoles(addRoles);
//        passingUser.saveUserDataToFirestore();


        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setCurrentUser(passingUser);
        UniversalProgramValues.getInstance().setEventList(new ArrayList<>());
        // Simulate entering the LoginFragment without an event ID
        scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            loginFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        // Wait for the fragment to load
        Espresso.onIdle();

        // Simulate clicking the Normal button
        onView(withId(R.id.button_normal)).perform(click());

        // Simulate logging in as an existing user
//        onView(withId(R.id.input_document_id)).perform(typeText("existing_user_id"));
//
//        // Simulate confirming user login
//        onView(withId(R.id.button_set_by_document_id)).perform(click());
        // Assert that the welcome text reflects an existing user login
        onView(withId(R.id.text_login_welcome))
                .check(matches(isDisplayed()));
        try {
            Thread.sleep(4000); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document("testingDeviceID100");
//        userRef.delete();
    }
}
