package com.example.eventbooking;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.Testing.SampleTable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestOrganizerFragment {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS
    );

    private ActivityScenario<MainActivity> scenario;
    private User user;
    private Event event;
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



    }

    @After
    public void tearDown() {

//        deleteTestData();
        UniversalProgramValues.getInstance().resetInstance();

    }


    @Test
    public void testOrganizerNoFacility() {
        List <String> role = user.getRoles();
        role.remove(Role.ORGANIZER);
        user.setRoles(role);
        UserManager.getInstance().setCurrentUser(user);
        UniversalProgramValues.getInstance().setEventList(new ArrayList<>());
        UniversalProgramValues.getInstance().getUserList().add(user);


        assertEquals(UniversalProgramValues.getInstance().queryFacilityOrganizer(user.getDeviceID()), false);

        onView(withId(R.id.nav_organizer_bottom))
                .perform(click());

        onView(withId(R.id.btn_add_facility)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_add_facility))
                .perform(click());

        onView(withId(R.id.facility_name)).check(matches(isDisplayed()));

        onView(withId(R.id.button_back_organizer))
                .perform(click());

        onView(withId(R.id.btn_add_facility)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_add_facility))
                .perform(click());

        onView(withId(R.id.facility_name)).check(matches(isDisplayed()));

        onView(withId(R.id.button_cancel))
                .perform(click());

        onView(withId(R.id.btn_add_facility)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_add_facility))
                .perform(click());

        onView(withId(R.id.facility_name)).check(matches(isDisplayed()));

        onView(withId(R.id.facility_name))
                .perform(click());
        onView(withId(R.id.facility_name)).perform(typeText("new_facility"));

        onView(withId(R.id.facility_id))
                .perform(click());
        onView(withId(R.id.facility_id)).perform(typeText("new_facility_id"));

        onView(withId(R.id.facility_location))
                .perform(click());
        onView(withId(R.id.facility_location)).perform(typeText("new facility location"));

        onView(withId(R.id.button_create_facility))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_view_facility)).check(matches(isDisplayed()));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(UniversalProgramValues.getInstance().queryFacilityOrganizer(user.getDeviceID()), true);


        onView(withId(R.id.btn_view_facility)).perform(click());

        onView(withText("new_facility"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.cancel_button_facility))
                .perform(click());

        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_view_facility)).perform(click());

        onView(withText("new_facility"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.go_back))
                .perform(click());

        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_view_facility)).perform(click());

        onView(withText("new_facility"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("new_facility_id"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("new facility location"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.facility_edit_name))
                .perform(click());
        onView(withId(R.id.facility_edit_name))
                .perform(clearText())
                .perform(typeText("additional new_facility"));

        onView(withId(R.id.facility_edit_facilityID))
                .perform(click());
        onView(withId(R.id.facility_edit_facilityID))
                .perform(clearText())
                .perform(typeText("additional new_facility_id"));

        onView(withId(R.id.facility_edit_location))
                .perform(click());
        onView(withId(R.id.facility_edit_location))
                .perform(clearText())
                .perform(typeText("additional new facility location"));

        onView(withId(R.id.save_button_facility))
                .perform(click());

        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_add_event)).perform(click());

        onView(withId(R.id.event_create_scroll_view))
                .perform(swipeUp());

        onView(withId(R.id.event_description))
                .perform(click());
        onView(withId(R.id.event_description)).perform(typeText("new event description"));
        onView(withId(R.id.event_name))
                .perform(click());
        onView(withId(R.id.event_name)).perform(typeText("new event title"));
        onView(withId(R.id.event_location))
                .perform(click());
        onView(withId(R.id.event_location)).perform(typeText("new event location"));
        onView(withId(R.id.max_participants))
                .perform(click());
        onView(withId(R.id.max_participants)).perform(typeText("20"));
//        onView(withId(R.id.event_location))
//                .perform(click());
//        onView(withId(R.id.event_location)).perform(typeText("new event location"));
        onView(withId(R.id.waiting_list_limit))
                .perform(click());
        onView(withId(R.id.waiting_list_limit)).perform(typeText("40"));

        onView(withId(R.id.geolocation_switch)).perform(click());


        onView(withId(R.id.button_create_event)).perform(click());

        onView(withText("eventID1"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.btn_view_facility)).perform(click());

        onView(withText("additional new_facility"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("new_facility_id"))
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("additional new facility location"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.delete_button_facility))
                .perform(click());

        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));

        assertEquals(UniversalProgramValues.getInstance().queryFacilityOrganizer(user.getDeviceID()), false);
    }

    @Test
    public void testOrganizerFacilityListsWaiting() {

        UserManager.getInstance().setCurrentUser(user);
        UniversalProgramValues.getInstance().getFacilityList().get(0).setOrganizer(UserManager.getInstance().getUserId());
        setFirstEventLists();

        onView(withId(R.id.nav_organizer_bottom))
                .perform(click());

        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));

        onView(withText("eventID1"))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("more action"))
                .perform(click());

        onView(withId(R.id.waitingListButton))
                .perform(click());

        onView(withId(R.id.waiting_list_view))
                .check(matches(isDisplayed()));

        onView(withText("userID1"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID2"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.back_button))
                .perform(click());

        onView(withId(R.id.sampleAttendeesButton))
                .perform(click());

        onView(withId(R.id.accptedParticipantButton))
                .perform(click());

        onView(withText("userID1"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID2"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID3"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.back_button))
                .perform(click());

        onView(withId(R.id.canceledParticipantButton))
                .perform(click());

        onView(withText("userID7"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID8"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID9"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.button_back_to_menu))
                .perform(click());

        onView(withId(R.id.SignedParticipantButton))
                .perform(click());

        onView(withText("userID10"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID11"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID12"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.back_button))
                .perform(click());

    }

    @Test
    public void testOrganizerFacilityListsCancel() {

        UserManager.getInstance().setCurrentUser(user);
        UniversalProgramValues.getInstance().getFacilityList().get(0).setOrganizer(UserManager.getInstance().getUserId());
        setFirstEventLists();

        onView(withId(R.id.nav_organizer_bottom))
                .perform(click());

        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));

        onView(withText("eventID1"))
                .perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("more action"))
                .perform(click());

        onView(withId(R.id.accptedParticipantButton))
                .perform(click());

        onView(withText("userID4"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID5"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID6"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.back_button))
                .perform(click());

        onView(withId(R.id.cancel_entrant))
                .perform(click());

        onView(withText("userID4"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID5"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withText("userID6"))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.back_button))
                .perform(click());


//        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
//        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
//        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
//        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
//        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
//        backToButton = rootView.findViewById(R.id.BackToButton);
//
//        viewAcceptedListButton=rootView.findViewById(R.id.accptedParticipantButton);
//
//        generateQRCode = rootView.findViewById(R.id.generate_qr_code);
//        QRImage = rootView.findViewById(R.id.QR_image);
//        CancelNonSignUp = rootView.findViewById(R.id.cancel_entrant);
//        removePosterButton = rootView.findViewById(R.id.button_remove_poster);
//        uploadPosterButton = rootView.findViewById(R.id.button_upload_poster);
//        posterImageView = rootView.findViewById(R.id.poster_image_view);

    }

//    @Test
//    public void testOrganizerFacilityListsReplacement() {
//
//        UserManager.getInstance().setCurrentUser(user);
//        UniversalProgramValues.getInstance().getFacilityList().get(0).setOrganizer(UserManager.getInstance().getUserId());
//        setFirstEventLists();
//
//        onView(withId(R.id.nav_organizer_bottom))
//                .perform(click());
//
//        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));
//
//        onView(withText("eventID1"))
//                .perform(click());
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        onView(withText("more action"))
//                .perform(click());
//
//        onView(withId(R.id.waitingListButton))
//                .perform(click());
//
//        onView(withId(R.id.waiting_list_view))
//                .check(matches(isDisplayed()));
//
//        onView(withText("userID1"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withText("userID2"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withText("userID3"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withId(R.id.back_button))
//                .perform(click());
//
//        onView(withId(R.id.DrawReplacementButton))
//                .perform(click());
//
//        Espresso.onView(withId(android.R.id.message))
//                .perform(ViewActions.replaceText("3"));
//
//        Espresso.onView(withText("OK")).perform(click());
//
//        onView(withId(R.id.SignedParticipantButton))
//                .perform(click());
//
//        onView(withText("userID1"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withText("userID2"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withText("userID3"))
//                .check(ViewAssertions.matches(isDisplayed()));
//
//        onView(withId(R.id.back_button))
//                .perform(click());
//
//
////        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
////        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
////        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
////        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
////        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
////        backToButton = rootView.findViewById(R.id.BackToButton);
////
////        viewAcceptedListButton=rootView.findViewById(R.id.accptedParticipantButton);
////
////        generateQRCode = rootView.findViewById(R.id.generate_qr_code);
////        QRImage = rootView.findViewById(R.id.QR_image);
////        CancelNonSignUp = rootView.findViewById(R.id.cancel_entrant);
////        removePosterButton = rootView.findViewById(R.id.button_remove_poster);
////        uploadPosterButton = rootView.findViewById(R.id.button_upload_poster);
////        posterImageView = rootView.findViewById(R.id.poster_image_view);
//
//    }





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

        event = new Event();
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
        UniversalProgramValues.getInstance().changeAllEventsOrganizer("testingDeviceID100");

//        UniversalProgramValues.getInstance().setUploadProfileURL("https://fastly.picsum.photos/id/213/200/200.jpg?hmac=Jzh2fbzIE1nc6J8qLi_ljVCRz0AITXxCC1Z8t2sD4jU");
        UniversalProgramValues.getInstance().setExistingLogin(true);
        UniversalProgramValues.getInstance().setDeviceID("testingDeviceID100");
        UniversalProgramValues.getInstance().setCurrentUser(user);

        Log.d("Profile Photo Test", "Universal Default: " + UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl());
        Log.d("Profile Photo Test", "Universal Profile: " + UniversalProgramValues.getInstance().getSingle_user().getProfilePictureUrl());
    }

    private void setFirstEventLists(){
        List <String> list1 = new ArrayList<>();
        list1.add("userID1");
        list1.add("userID2");
        list1.add("userID3");
        List <String> list2 = new ArrayList<>();
        list2.add("userID4");
        list2.add("userID5");
        list2.add("userID6");
        List <String> list3 = new ArrayList<>();
        list3.add("userID7");
        list3.add("userID8");
        list3.add("userID9");
        List <String> list4 = new ArrayList<>();
        list4.add("userID10");
        list4.add("userID11");
        list4.add("userID12");
        UniversalProgramValues.getInstance().getEventList().get(0).setWaitingParticipantIds(list1);
        UniversalProgramValues.getInstance().getEventList().get(0).setAcceptedParticipantIds(list2);
        UniversalProgramValues.getInstance().getEventList().get(0).setCanceledParticipantIds(list3);
        UniversalProgramValues.getInstance().getEventList().get(0).setSignedUpParticipantIds(list4);
    }
}
