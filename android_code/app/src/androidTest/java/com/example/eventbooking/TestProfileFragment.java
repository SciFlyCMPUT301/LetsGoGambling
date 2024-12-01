package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.Testing.SampleTable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestProfileFragment {

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
        Intents.init();
        EventList = new ArrayList<>();
        user = new User();
        initalizeUserAndEvents();
        scenario = ActivityScenario.launch(MainActivity.class);

        try {
            Thread.sleep(500); // Pause for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        UserManager.getInstance().setCurrentUser(user);

        onView(withId(R.id.standard_nav_profile))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));

    }

    @After
    public void tearDown() {

//        deleteTestData();
        UniversalProgramValues.getInstance().resetInstance();
    }


    @Test
    public void testProfileEdit() {
        onView(withId(R.id.button_edit_profile))
                .perform(click());

        onView(withId(R.id.profile_page_scroll_view))
                .perform(swipeUp());

        onView(withId(R.id.edit_name))
                .perform(clearText())
                .perform(typeText("totally_new_name"));
        onView(withId(R.id.edit_email))
                .perform(clearText())
                .perform(typeText("totally_new_email@example.com"));
        onView(withId(R.id.edit_phone))
                .perform(clearText())
                .perform(typeText("99999999999"));
        onView(withId(R.id.notifications_switch)).perform(click());
        onView(withId(R.id.geolocation_switch)).perform(click());
        onView(withId(R.id.button_save_profile)).perform(click());

        onView(withId(R.id.nav_home_bottom))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nav_profile_bottom))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_page_scroll_view))
                .perform(swipeUp());

        onView(withId(R.id.button_edit_profile))
                .perform(click());
        onView(withId(R.id.button_edit_profile))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("totally_new_name"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("totally_new_email@example.com"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("99999999999"))
                .check(ViewAssertions.matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.notifications_switch))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.geolocation_switch))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()));

    }


    @Test
    public void testProfileUploadAndRemove() {

        Uri mockImageUri = Uri.parse("content://mock/image");
        Intent resultData = new Intent();
        resultData.setData(mockImageUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        onView(withId(R.id.button_edit_profile))
                .perform(click());
        onView(withId(R.id.profile_page_scroll_view))
                .perform(swipeUp());

        onView(withId(R.id.button_upload_photo))
                .check(matches(isDisplayed()))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//
        onView(withId(R.id.button_save_profile)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Profile Photo Test", "User Manager Default: " + UserManager.getInstance().getCurrentUser().getdefaultProfilePictureUrl());
        Log.d("Profile Photo Test", "User Manager Profile: " + UserManager.getInstance().getCurrentUser().getProfilePictureUrl());
        Log.d("Profile Photo Test", "Universal Default: " + UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl());
        Log.d("Profile Photo Test", "Universal Profile: " + UniversalProgramValues.getInstance().getSingle_user().getProfilePictureUrl());
        assertNotEquals(UserManager.getInstance().getCurrentUser().getdefaultProfilePictureUrl(),
                UserManager.getInstance().getCurrentUser().getProfilePictureUrl());

        assertNotEquals(UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl(),
                UniversalProgramValues.getInstance().getSingle_user().getProfilePictureUrl());

        onView(withId(R.id.nav_home_bottom))
                .perform(click());

        onView(withId(R.id.search_bar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.nav_profile_bottom))
                .perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));

//        onView(withId(R.id.user_image)).check(matches(isDisplayed()));
//        onView(allOf(withId(R.id.user_image), isDisplayed()));

        onView(withId(R.id.button_edit_profile))
                .perform(click());

        onView(withId(R.id.button_edit_profile))
                .perform(click());

        onView(withId(R.id.profile_page_scroll_view))
                .perform(swipeUp());


        onView(withId(R.id.button_remove_photo))
                .perform(click());

        assertEquals(UserManager.getInstance().getCurrentUser().getdefaultProfilePictureUrl(),
                UserManager.getInstance().getCurrentUser().getProfilePictureUrl());
        assertEquals(UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl(),
                UniversalProgramValues.getInstance().getSingle_user().getProfilePictureUrl());
    }




    private void initalizeUserAndEvents(){
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setTestingMode(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
//        user.setDeviceID("testingDeviceID100");
//        user.setUsername("testUser");
//        user.setEmail("testUser@example.com");
//        user.setNotificationAsk(false);
//        user.setGeolocationAsk(false);
//        user.setdefaultProfilePictureUrl("testURL");
//        user.setProfilePictureUrl("testURL");
//        List<String> addRoles = new ArrayList<>();
//        addRoles.add(Role.ENTRANT);
//        addRoles.add(Role.ORGANIZER);
//        addRoles.add(Role.ADMIN);
//        user.setRoles(addRoles);
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
        user.setdefaultProfilePictureUrl("https://fastly.picsum.photos/id/1033/200/300.jpg?hmac=856_WOyaGXSjI4FWe3_NCHU7frPtAEJaHnAJja5TMNk");
        user.setProfilePictureUrl("https://fastly.picsum.photos/id/532/200/200.jpg?hmac=PPwpqfjXOagQmhd_K7H4NXyA4B6svToDi1IbkDW2Eos");
        List<String> addSingleRoles = new ArrayList<>();
        addSingleRoles.add(Role.ENTRANT);
        addSingleRoles.add(Role.ORGANIZER);
        addSingleRoles.add(Role.ADMIN);
        user.setRoles(addSingleRoles);
        UniversalProgramValues.getInstance().setUploadProfileURL("https://fastly.picsum.photos/id/213/200/200.jpg?hmac=Jzh2fbzIE1nc6J8qLi_ljVCRz0AITXxCC1Z8t2sD4jU");
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        UniversalProgramValues.getInstance().setCurrentUser(user);
        Log.d("Profile Photo Test", "Universal Default: " + UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl());
        Log.d("Profile Photo Test", "Universal Profile: " + UniversalProgramValues.getInstance().getSingle_user().getProfilePictureUrl());
    }
}
