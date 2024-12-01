package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;


import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.Testing.SampleTable;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestAdminFragment {


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
    public void disableAnimations() {
        // Disable all system animations on the device for the test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                // Disable window transition scale
                Settings.Global.putInt(
                        InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                        Settings.Global.TRANSITION_ANIMATION_SCALE, 0);

                // Disable animator duration scale
                Settings.Global.putInt(
                        InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(),
                        Settings.Global.ANIMATOR_DURATION_SCALE, 0);

                // Disable window animation scale
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

        Log.d("Admin Test", "Calling Instance");
        UniversalProgramValues.getInstance();
        Log.d("Admin Test", "Calling Instance set testing true");
        UniversalProgramValues.getInstance().setTestingMode(true);
        scenario = ActivityScenario.launch(MainActivity.class);
//        scenario = activityRule.getScenario();
        EventList = new ArrayList<>();
        user = new User();
        initalizeUserAndEvents();
        Log.d("Test Admin Fragment", "Done Initalizing Data");

//        initalizeUserAndEvents();
        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("Test Admin Fragment", "Launching Login Fragment");
//        scenario.onActivity(activity -> {
//            LoginFragment loadingFragment = new LoginFragment();
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, loadingFragment)
//                    .commitNow();
//        });

//        Espresso.onIdle();
        onView(withId(R.id.button_normal))
                .check(ViewAssertions.matches(isDisplayed()));
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
    }



    @Test
    public void testNavigateToUserEditView() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.users_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_list))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("deviceID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("deviceID3"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_view_deviceID)).check(matches(isDisplayed()));

        onView(withId(R.id.user_view_deviceID)).check(matches(withText("deviceID3")));


        onView(withId(R.id.admin_view_user_scrollview))
                .perform(swipeUp());
        onView(withId(R.id.cancel_button_user))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_list))
                .check(matches(isDisplayed()));

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.users_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();
    }

    @Test
    public void testNavigateToUserDelete() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.users_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_list))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("deviceID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("deviceID3"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_view_deviceID)).check(matches(isDisplayed()));

        onView(withId(R.id.user_view_deviceID)).check(matches(withText("deviceID3")));


        onView(withId(R.id.admin_view_user_scrollview))
                .perform(swipeUp());
        onView(withId(R.id.delete_button_user))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.user_list))
                .check(matches(isDisplayed()));
        for(int i = 0; i < UniversalProgramValues.getInstance().getUserList().size(); i++){
            assert(UniversalProgramValues.getInstance().getUserList().get(i).getDeviceID() != "deviceID3");
        }

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.users_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();

    }

    @Test
    public void testNavigateToEventEditView() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.events_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.event_list))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("eventID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("eventID3"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.view_text_event_title)).check(matches(isDisplayed()));

        onView(withId(R.id.view_text_event_title)).check(matches(withText("Event Title 3")));


        // Move to the QR code
        onView(withId(R.id.qrcode_image_view))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.hashed_qrcode_event))
                .check(matches(isDisplayed()));

        String stored_hash = UniversalProgramValues.getInstance().getSpecificEventHash("eventID3");
        assertNotNull(stored_hash);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(stored_hash))
                .check(ViewAssertions.matches(isDisplayed()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.remove_qrcode))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String new_hash = UniversalProgramValues.getInstance().getSpecificEventHash("eventID3");
        assertNotNull(new_hash);
        assertNotEquals(new_hash, stored_hash);

        onView(withId(R.id.admin_go_back_event))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.admin_view_event_scrollview))
                .perform(swipeUp());

        onView(withId(R.id.button_cancel))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.event_list))
                .check(matches(isDisplayed()));

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.events_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();
    }

    @Test
    public void testNavigateToEventDelete() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.events_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.event_list))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("eventID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("eventID3"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.view_text_event_title)).check(matches(isDisplayed()));

        onView(withId(R.id.view_text_event_title)).check(matches(withText("Event Title 3")));

        onView(withId(R.id.admin_view_event_scrollview))
                .perform(swipeUp());

        onView(withId(R.id.button_remove_event))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.event_list))
                .check(matches(isDisplayed()));

        for(int i = 0; i < UniversalProgramValues.getInstance().getEventList().size(); i++){
            assert(UniversalProgramValues.getInstance().getEventList().get(i).getEventId() != "eventID3");
        }

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.events_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();
    }

    @Test
    public void testNavigateToFacilityDelete() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.facilities_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.facility_list))
                .check(ViewAssertions.matches(isDisplayed()));

        Boolean facility_check_exists = UniversalProgramValues.getInstance().doesFacilityExist("facilityID2");

        assertEquals(facility_check_exists, true);

        onView(withText("Facility2"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Facility2"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.remove_facility_button)).check(matches(isDisplayed()));

        onView(withId(R.id.remove_facility_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Boolean facility_check_not_exists = UniversalProgramValues.getInstance().doesFacilityExist("facilityID2");

        assertEquals(facility_check_not_exists, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.facilities_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();
    }

    @Test
    public void testNavigateToImageDelete() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.images_button)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.image_list))
                .check(ViewAssertions.matches(isDisplayed()));

        Boolean image_check_exists = UniversalProgramValues.getInstance().doesImageExist("Testing Profile URL3");

        assertEquals(image_check_exists, true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("User: User3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("User: User3"))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.remove_image_button)).check(matches(isDisplayed()));

        onView(withId(R.id.remove_image_button)).perform(click());

        Boolean image_check_not_exists = UniversalProgramValues.getInstance().doesImageExist("Testing Profile URL3");

        assertEquals(image_check_not_exists, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.admin_go_back))
                .perform(click());

        onView(withId(R.id.images_button))
                .check(ViewAssertions.matches(isDisplayed()));

        UniversalProgramValues.getInstance().resetInstance();
    }

//    @Test
//    public void testNavigateToEventEditView() {
//
//
////        viewUsersButton = view.findViewById(R.id.users_button);
////        viewEventsButton = view.findViewById(R.id.events_button);
////        viewFacilitiesButton = view.findViewById(R.id.facilities_button);
////        viewImagesButton = view.findViewById(R.id.images_button);
////        viewTestPageButton = view.findViewById(R.id.test_page_button);
////        backHomeButton = view.findViewById(R.id.home_button);
//
//        onView(withId(R.id.events_button)).perform(click());
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onView(withId(R.id.search_bar))
//                .check(matches(isDisplayed()));
//
//    }




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
        testTable.makeImageList();
        Log.d("Test Admin Fragment", "Made Event List");
        Log.d("Test Admin Fragment", "Setting Event List");
        UniversalProgramValues.getInstance().setEventList(testTable.getEventList());
        Log.d("Test Admin Fragment", "Setting User List");
        UniversalProgramValues.getInstance().setUserList(testTable.getUserList());
        Log.d("Test Admin Fragment", "Setting Facility List");
        UniversalProgramValues.getInstance().setFacilityList(testTable.getFacilityList());
        UniversalProgramValues.getInstance().setImageList(testTable.getImageList());
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
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        UniversalProgramValues.getInstance().setCurrentUser(user);
    }

}
