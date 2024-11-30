package com.example.eventbooking;

import static android.app.PendingIntent.getActivity;

import androidx.core.app.ActivityCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.User;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;


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

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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
    private String currentDeviceID;

    private Handler handler;

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollection;
    @Mock
    private DocumentReference mockDocument;
    @Mock
    private FirebaseStorage mockStorage;
    @Mock
    private StorageReference mockStorageReference;

    private FirestoreAccess mockFirestoreAccess;
//    private MockitoSession mockitoSession;


    @Before
    public void setUp() {

//        MockitoAnnotations.initMocks(this);
//        mockitoSession = Mockito.mockitoSession()
//                .initMocks(this)
//                .strictness(Strictness.LENIENT)
//                .startMocking();
        MockitoAnnotations.initMocks(this);
        mockFirestore = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockDocument = mock(DocumentReference.class);


        // Mock Firestore and CollectionReference
        when(mockFirestore.collection("Users")).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        Task<Void> mockSetTask = mock(Task.class);

        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> onSuccessListener = invocation.getArgument(0);
            onSuccessListener.onSuccess(null); // Simulate successful Firestore write
            return mockSetTask;
        });

        // Simulate addOnFailureListener behavior
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(0);
            onFailureListener.onFailure(new Exception("Simulated Firestore failure")); // Simulate failure
            return mockSetTask;
        });

        // Ensure mockDocument.set() returns the mocked Task
        when(mockDocument.set(anyMap())).thenReturn(mockSetTask);

        when(mockCollection.get()).thenAnswer(invocation -> {
            Task<QuerySnapshot> mockQueryTask = mock(Task.class);
            when(mockQueryTask.isSuccessful()).thenReturn(true);
            QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
            when(mockQueryTask.getResult()).thenReturn(mockSnapshot);
            when(mockSnapshot.size()).thenReturn(100); // Simulate 100 existing documents
            return mockQueryTask;
        });

        user = new User(mockStorageReference, mockFirestore);
        user.setDeviceID("deviceID100");
        onView(isRoot()).perform(waitFor(10000));
        // Start the activity
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        onView(isRoot()).perform(waitFor(5000));
        // Create an instance of LoginFragment
        loginFragment = new LoginFragment();
//        user = new User();
//        currentDeviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Launch the fragment
        scenario.onActivity(activity -> {
            loginFragment = new LoginFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commitNow(); // Ensure fragment is attached immediately
        });

        onView(isRoot()).perform(waitFor(10000));

        // Wait for the fragment to load
//        onView(isRoot()).perform(waitFor(1000));
    }

    @After
    public void tearDown() {

        // Example: Reset Mockito mocks
        Mockito.reset(mockFirestore, mockDocument, mockCollection);
//        assertNotNull(mockFirestore);  // Example: ensure mockFirestore is not null
//        assertNotNull(mockCollection); // Ensure mockCollection is not null
//
//        // Optionally verify that no unexpected interactions occurred
//        verifyNoMoreInteractions(mockFirestore, mockCollection, mockDocument);
        user = null;
        mockFirestore = null;
        mockCollection = null;
        mockDocument = null;
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
//
//        onView(withId(R.id.text_login_welcome))
//                .check(matches(withText("Welcome, ")));
//        onView(withId(R.id.text_login_deviceid))
//                .check(matches(withText("Welcome, ")));
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


    @Test
    public void testNewUser() {
        // Mock FirestoreAccess to simulate no user document
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
            when(mockDocumentSnapshot.exists()).thenReturn(false); // Simulate new user
            listener.onSuccess(mockDocumentSnapshot);
            return mockTask;
        });
        when(mockFirestoreAccess.getUser(anyString())).thenReturn(mockTask);


        onView(isRoot()).perform(waitFor(1000));

        // Click the "Normal Mode" button to trigger login flow
        onView(withId(R.id.button_normal)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        // Verify welcome text for new users
        onView(withId(R.id.text_login_welcome)).check(matches(withText("Welcome new user")));
        onView(isRoot()).perform(waitFor(4000));
        // Verify navigation to ProfileFragment
        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
    }


    @Test
    public void testReturningUser() {
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
            DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
            when(mockDocumentSnapshot.exists()).thenReturn(true);
            User mockUser = new User();
            mockUser.setDeviceID("testDeviceID");
            mockUser.setUsername("ReturningUser");
            mockUser.setEmail("user@example.com");
            mockUser.setPhoneNumber("1234567890");
            when(mockDocumentSnapshot.toObject(User.class)).thenReturn(mockUser);
            listener.onSuccess(mockDocumentSnapshot);
            return mockTask;
        });
        when(mockFirestoreAccess.getUser(anyString())).thenReturn(mockTask);


        // Replace FirestoreAccess instance in LoginFragment
        FirestoreAccess.setInstance(mockFirestoreAccess);
        onView(isRoot()).perform(waitFor(1000));
        // Click the "Normal Mode" button to trigger login flow
        onView(withId(R.id.button_normal)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        // Verify welcome text for returning users
        onView(withId(R.id.text_login_welcome)).check(matches(withText("Welcome, ReturningUser")));
        onView(isRoot()).perform(waitFor(4000));
        // Verify navigation to HomeFragment
        onView(withId(R.id.user_events_list)).check(matches(isDisplayed()));
    }


    @Test
    public void testQrCodeScannedOffline() {
        // Mock FirestoreAccess to simulate no user document and set QR code event
        String mockEventId = "mockEvent123";
        FirestoreAccess mockFirestoreAccess = mock(FirestoreAccess.class);
        when(mockFirestoreAccess.getUser(anyString())).thenReturn(Tasks.forResult(null));

        // Replace FirestoreAccess instance in LoginFragment
        FirestoreAccess.setInstance(mockFirestoreAccess);
        onView(isRoot()).perform(waitFor(1000));
        // Set up fragment arguments for QR code
        Bundle args = new Bundle();
        args.putString("eventIdFromQR", mockEventId);
        loginFragment.setArguments(args);

        // Click the "Normal Mode" button to trigger login flow
        onView(withId(R.id.button_normal)).perform(click());
        onView(isRoot()).perform(waitFor(4000));
        // Verify navigation to ProfileFragment with QR code event ID
        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
        // Add additional checks for event ID usage in ProfileFragment if applicable
    }




    private void initializeDefaultUser() {
        User defaultUser = new User();
        defaultUser.setDeviceID("defaultDeviceID");
        defaultUser.setUsername("DefaultUser");
        defaultUser.setEmail("default@example.com");
        defaultUser.setPhoneNumber("000-000-0000");
        defaultUser.setGeolocationAsk(false); // Disable geolocation

        // Set this user in the UserManager
        UserManager.getInstance().setCurrentUser(defaultUser);

        Log.d("LoginFragment", "Initialized default user with geolocation disabled.");
    }


//    private void requestGpsPermissionIfNeeded() {
//        if (UserManager.getInstance().getCurrentUser().isGeolocationAsk()) {
//            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//            }
//        }
//    }





}