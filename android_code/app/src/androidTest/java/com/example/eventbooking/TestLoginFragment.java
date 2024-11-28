package com.example.eventbooking;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.User;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestLoginFragment {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private LoginFragment loginFragment;
    private User user;

    private Handler handler;

    @Before
    public void setUp() {
        // Start the activity
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();

        // Create an instance of LoginFragment
        loginFragment = new LoginFragment();
        user = new User();

        // Launch the fragment
        scenario.onActivity(activity -> {
            loginFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        // Wait for the fragment to load
//        onView(isRoot()).perform(waitFor(1000));
    }

    // Helper method for waiting
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

//    New retooled code here
    /**
     * US 01.07.01
     * Single Login For Device Closed App and Restarting
     * Geolocation True
     */
    @Test
    public void testExistingUserRefreshGeoTrue() {
        // Saves the user to firebase
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        List<String> roles = new ArrayList<>();
        roles.add(Role.ADMIN);
        roles.add(Role.ORGANIZER);
        roles.add(Role.ENTRANT);
        GeoPoint passed_location = new GeoPoint(-15.0, -15.0);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("deviceId", deviceId);
            put("username", "UITestUserName");
            put("email", "UITestLogin@example.com");
            put("phoneNumber", "12345");
            put("profilePictureUrl", "testImage");
            put("defaultProfilePictureUrl", "testImage");
            put("location", "NoWhereVille");
            put("adminLevel", true);
            put("facilityAssociated", true);
            put("notificationAsk", true);
            put("geolocationAsk", true);
            put("geolocation", passed_location);
            put("roles", roles);
        }});

//        DocumentReference userRef = db.collection("Users").document(deviceId);
//        userRef.delete();
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.button_normal))
                .check(matches(isDisplayed()))
                .perform(click());
//        text_login_deviceid"text_login_welcome
        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));

        onView(withId(R.id.text_login_deviceid))
                .check(matches(withText(deviceId)));

       // Checking to see if UserManager has loaded the user
        assertEquals(UserManager.getInstance().getUserId(), deviceId);
        // If the above is true and geolocation is on then we should get the updated location
        assertNotEquals(UserManager.getInstance().getGeolocation(), passed_location);

        userRef.delete();
    }

    /**
     * US 01.07.01
     * Single Login For Device Closed App and Restarting
     * Geolocation False
     */
    @Test
    public void testExistingUserRefreshGeoFalse() {
        // Saves the user to firebase
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        List<String> roles = new ArrayList<>();
        roles.add(Role.ADMIN);
        roles.add(Role.ORGANIZER);
        roles.add(Role.ENTRANT);
        GeoPoint passed_location = new GeoPoint(-15.0, -15.0);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("deviceId", deviceId);
            put("username", "UITestUserName");
            put("email", "UITestLogin@example.com");
            put("phoneNumber", "12345");
            put("profilePictureUrl", "testImage");
            put("defaultProfilePictureUrl", "testImage");
            put("location", "NoWhereVille");
            put("adminLevel", true);
            put("facilityAssociated", true);
            put("notificationAsk", true);
            put("geolocationAsk", false);
            put("geolocation", passed_location);
            put("roles", roles);
        }});

//        DocumentReference userRef = db.collection("Users").document(deviceId);
//        userRef.delete();
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.button_normal))
                .check(matches(isDisplayed()))
                .perform(click());
//        text_login_deviceid"text_login_welcome
        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));

        onView(withId(R.id.text_login_deviceid))
                .check(matches(withText(deviceId)));

        // Checking to see if UserManager has loaded the user
        assertEquals(UserManager.getInstance().getUserId(), deviceId);
        // If the above is true and geolocation is on then we should get the updated location
        assertEquals(UserManager.getInstance().getGeolocation(), passed_location);

        userRef.delete();
    }


    /**
     * US 01.07.01
     * Single Login For Device Closed App and Restarting
     * Geolocation False
     */
    @Test
    public void testExistingUserQRCodeScanned() {
        // Saves the user to firebase
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        List<String> roles = new ArrayList<>();
        roles.add(Role.ADMIN);
        roles.add(Role.ORGANIZER);
        roles.add(Role.ENTRANT);
        GeoPoint passed_location = new GeoPoint(-15.0, -15.0);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("deviceId", deviceId);
            put("username", "UITestUserName");
            put("email", "UITestLogin@example.com");
            put("phoneNumber", "12345");
            put("profilePictureUrl", "testImage");
            put("defaultProfilePictureUrl", "testImage");
            put("location", "NoWhereVille");
            put("adminLevel", true);
            put("facilityAssociated", true);
            put("notificationAsk", true);
            put("geolocationAsk", false);
            put("geolocation", passed_location);
            put("roles", roles);
        }});

//        DocumentReference userRef = db.collection("Users").document(deviceId);
//        userRef.delete();
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.button_normal))
                .check(matches(isDisplayed()))
                .perform(click());
//        text_login_deviceid"text_login_welcome
        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));

        onView(withId(R.id.text_login_deviceid))
                .check(matches(withText(deviceId)));

        // Checking to see if UserManager has loaded the user
        assertEquals(UserManager.getInstance().getUserId(), deviceId);
        // If the above is true and geolocation is on then we should get the updated location
        assertEquals(UserManager.getInstance().getGeolocation(), passed_location);
        userRef.delete();
    }


//    Old code below, retooling it so it can use Firebase to verify

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null); // Cancel all pending callbacks
//        }
//    }

//    @Test
//    public void testNewUserLogin() {
//        // Simulate clicking on "Normal Mode" button
//        onView(withId(R.id.button_normal)).perform(click());
//
////        MainActivity mockMainActivity = mock(MainActivity.class);
////        doNothing().when(mockMainActivity).showNavigationUI();
////        loginFragment.onAttach(mockMainActivity);
//
//        // Simulate the app flow for a new user
//        simulateNewUserFlow();
//
//        // Verify that the profile creation fragment is displayed
//        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
////        verify(mockMainActivity).showNavigationUI();
//    }
//
//    @Test
//    public void testReturningUserLogin() {
//        // Simulate clicking on "Normal Mode" button
//        onView(withId(R.id.button_normal)).perform(click());
//
////        MainActivity mockMainActivity = mock(MainActivity.class);
////        doNothing().when(mockMainActivity).showNavigationUI();
////        loginFragment.onAttach(mockMainActivity);
//
//        // Simulate the app flow for a returning user
//        simulateReturningUserFlow();
//
//        // Verify that the home fragment is displayed
////        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));
//    }
//
//    // Simulate new user behavior
//    private void simulateNewUserFlow() {
//        // Replace Firestore interaction with a delay to simulate async behavior
//        onView(isRoot()).perform(waitFor(3000));
//        // Replace fragment to simulate new user navigation
//        loginFragment.getParentFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, new ProfileFragment())
//                .commitAllowingStateLoss();
//    }
//
//    // Simulate returning user behavior
//    private void simulateReturningUserFlow() {
////        MainActivity mockMainActivity = mock(MainActivity.class);
////        doNothing().when(mockMainActivity).showNavigationUI();
//        user.setDeviceID("deviceID100");
//        UserManager.getInstance().setCurrentUser(user);
////        loginFragment.onAttach(mockMainActivity);
//        // Replace Firestore interaction with a delay to simulate async behavior
//        onView(isRoot()).perform(waitFor(3000));
//        // Replace fragment to simulate returning user navigation
//        loginFragment.getParentFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, new HomeFragment())
//                .commitAllowingStateLoss();
//    }
//




}