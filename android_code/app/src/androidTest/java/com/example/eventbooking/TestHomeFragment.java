package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.Testing.SampleTable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

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

//    @Rule
//    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

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
    private List<User> loadingUserList;
    private List<Event> loadingEventList;
    private List<Facility> loadingFacilityList;

    @Before
    public void setUp() {
        EventList = new ArrayList<>();
        user = new User();
        initalizeUserAndEvents();
        scenario = ActivityScenario.launch(MainActivity.class);
//        scenario = activityRule.getScenario();

        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        // Launch HomeFragment

    }

    @After
    public void tearDown() {

//        deleteTestData();
        UniversalProgramValues.getInstance().resetInstance();
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
    public void testNavigateToEvent() {
        scenario.onActivity(activity -> {
//            HomeFragment homeFragment = HomeFragment.newInstance(true, "testUserId", EventList);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow(); // Ensure fragment is attached immediately
        });

        onView(withId(R.id.user_events_list))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.user_events_list))
                .perform(ViewActions.click());

        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Simulate clicking "Go to Event" button
//        onView(withId(R.id.button_event)).perform(click());

        // Verify EventFragment is displayed
        onView(withId(R.id.event_title_text))
                .check(matches(isDisplayed()));
        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Cancel"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Cancel"))
                .perform(ViewActions.click());

        onView(withId(R.id.user_events_list))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testNavigateToProfile() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        onView(withContentDescription("Open Navigation Drawer"))
                .perform(click());

        onView(withId(R.id.standard_nav_profile))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.button_back_home))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.button_back_home))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToProfileBottom() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

//        onView(withContentDescription("Open Navigation Drawer"))
//                .perform(click());

        onView(withId(R.id.nav_profile_bottom))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.button_back_home))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.button_back_home))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nav_profile_bottom))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));

        onView(withId(R.id.nav_home_bottom))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testNavigateToAdmin() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow();
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
//        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Or
        onView(withContentDescription("Open Navigation Drawer"))
                .perform(click());

        onView(withId(R.id.standard_nav_admin))
                .perform(click());

        onView(withId(R.id.home_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.home_button))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToNotifications() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow();
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
//        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Or
        onView(withContentDescription("Open Navigation Drawer"))
                .perform(click());

        onView(withId(R.id.standard_nav_notifications))
                .perform(click());

        onView(withId(R.id.button_back_home))
                .check(matches(isDisplayed()));

        onView(withId(R.id.button_back_home))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToHome() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow();
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
//        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Or
        onView(withContentDescription("Open Navigation Drawer"))
                .perform(click());

        onView(withId(R.id.standard_nav_event_menu))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToOrganizer() {
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        scenario.onActivity(activity -> {
            LoginFragment loadingFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loadingFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        Espresso.onIdle();

        onView(withId(R.id.button_normal)).perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        onView(withContentDescription("Open Navigation Drawer"))
                .perform(click());

        onView(withId(R.id.nav_organizer_bottom))
                .perform(click());

        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.nav_profile_bottom))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));
    }


    private void initalizeUserAndEvents(){
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
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
//        UserManager.getInstance().setCurrentUser(user);
//        user.saveUserDataToFirestore();
        SampleTable testTable = new SampleTable();
        testTable.makeUserList();
        Log.d("Test Admin Fragment", "Made User List");
        Log.d("Test Admin Fragment", "Made Facility List");
        testTable.makeFacilityList();

        testTable.makeEventList();
        Log.d("Test Admin Fragment", "Made Event List");
        Log.d("Test Admin Fragment", "Setting Event List");
        UniversalProgramValues.getInstance().setEventList(testTable.getEventList());
        Log.d("Test Admin Fragment", "Setting User List");
        UniversalProgramValues.getInstance().setUserList(testTable.getUserList());
        Log.d("Test Admin Fragment", "Setting Facility List");
        UniversalProgramValues.getInstance().setFacilityList(testTable.getFacilityList());
        user.setDeviceID("testingDeviceID100");
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setNotificationAsk(false);
        user.setGeolocationAsk(false);
        user.setdefaultProfilePictureUrl("testURL");
        user.setProfilePictureUrl("testURL");
        List<String> addSingleRoles = new ArrayList<>();
        addSingleRoles.add(Role.ENTRANT);
        addSingleRoles.add(Role.ORGANIZER);
        addSingleRoles.add(Role.ADMIN);
        user.setRoles(addSingleRoles);
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setCurrentUser(user);
    }




}
