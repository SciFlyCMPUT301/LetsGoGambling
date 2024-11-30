package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.util.Log;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class TestHomeFragment {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
    );

    private ActivityScenario<MainActivity> scenario;
    private User user;
    public List<Event> EventList;

    @Before
    public void setUp() {
        scenario = activityRule.getScenario();
        EventList = new ArrayList<>();
        initalizeUserAndEvents();


        // Launch HomeFragment

    }

    @After
    public void tearDown() {

        deleteTestData();

    }

    @Test
    public void testInitialUI() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance("testUserId");
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });
        // Verify HomeFragment initial UI
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_spinner)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToEventCreate() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });
        // Simulate clicking "Go to EventCreate" button
//        onView(withId(R.id.button_event_create)).perform(click());

        // Verify EventCreateFragment is displayed
        onView(withId(R.id.event_create_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToEvent() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });
        // Simulate clicking "Go to Event" button
//        onView(withId(R.id.button_event)).perform(click());

        // Verify EventFragment is displayed
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToNotification() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });
        // Simulate clicking "Go to Notification" button
        onView(withId(R.id.button_notification)).perform(click());

        // Verify NotificationFragment is displayed
        onView(withId(R.id.notification_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToProfile() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });
        // Simulate clicking "Go to Profile" button
//        onView(withId(R.id.button_profile)).perform(click());

        // Verify ProfileEntrantFragment is displayed
        onView(withId(R.id.profile_title)).check(matches(isDisplayed()));
    }




    private void initalizeUserAndEvents(){
        user.setDeviceID("testingDeviceID100");
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setNotificationAsk(false);
        user.setGeolocationAsk(false);
        user.setdefaultProfilePictureUrl("testURL");
        user.setProfilePictureUrl("testURL");
        List<String> addRoles = new ArrayList<>();
        addRoles.add(Role.ENTRANT);
        addRoles.add(Role.ORGANIZER);
        addRoles.add(Role.ADMIN);
        user.setRoles(addRoles);
        UserManager.getInstance().setCurrentUser(user);
//        user.saveUserDataToFirestore();

        // Create 10 events and put the user in parts of the test
        for (int i = 1; i <= 10; i++) {
            Event event = new Event();
            event.setEventId("testEventID" + i);
            event.setEventTitle("Event Title " + i);
            event.setDescription("Description for event " + i);
            event.setTimestamp(System.currentTimeMillis() + i * 100000);
            event.setMaxParticipants(20);
            event.setOrganizerId("testDeviceID"+i);
            event.setLocation("testAddress" + i);
            QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
            String hashInput = event.getEventId() + Calendar.getInstance().getTime();
            String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
            event.setQRcodeHash(qrCodeHash);

            if(i%4 == 0)
                event.addAcceptedParticipantId(user.getDeviceID());
            else if(i%4 == 1)
                event.addCanceledParticipantIds(user.getDeviceID());
            else if(i%4 == 2)
                event.addWaitingParticipantIds(user.getDeviceID());
            else
                event.addSignedUpParticipantIds(user.getDeviceID());

            EventList.add(event);
        }

//        for (Event event : EventList) {
//            event.saveEventDataToFirestore();
//        }

        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setCurrentUser(user);
        UniversalProgramValues.getInstance().setEventList(EventList);

    }

    private void deleteTestData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document("testingDeviceID100");
        userRef.delete();

        for (Event event : EventList) {
            DocumentReference eventRef = db.collection("Events").document(event.getEventId());
            userRef.delete();
        }

        user = null;
        EventList = null;
        UserManager.getInstance().setCurrentUser(new User());
        UniversalProgramValues.getInstance().resetInstance();
    }



}
