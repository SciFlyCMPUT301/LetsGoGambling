package com.example.eventbooking;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;

import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ScrollView;

@RunWith(AndroidJUnit4.class)
public class TestProfile {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Mock
    User mockUser;

    private ProfileFragment profileFragment;

    @Before
    public void setUp() {
        // Initialize mocks using Mockito 2
        MockitoAnnotations.initMocks(this);

        // Mock UserManager.getInstance() and UserManager.getCurrentUser()
        UserManager mockUserManagerInstance = mock(UserManager.class);
        when(UserManager.getInstance()).thenReturn(mockUserManagerInstance);
        when(mockUserManagerInstance.getCurrentUser()).thenReturn(mockUser);

        // Mock User methods
        when(mockUser.getUsername()).thenReturn("TestUser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getPhoneNumber()).thenReturn("1234567890");
        when(mockUser.isNotificationAsk()).thenReturn(true);
        when(mockUser.isGeolocationAsk()).thenReturn(true);
        when(mockUser.getProfilePictureUrl()).thenReturn("");

        // Start the activity
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();

        // Create an instance of ProfileFragment
        profileFragment = new ProfileFragment();

        // Launch the fragment
        scenario.onActivity(activity -> {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .commitAllowingStateLoss();
        });

        // Wait for the fragment to load
        onView(isRoot()).perform(waitFor(1000));
    }

    @After
    public void tearDown() {
        // No need for static mocking cleanup in Mockito 2, but if using PowerMock, use PowerMockito.closeStaticMocks()
        // PowerMockito.closeStaticMocks();
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


    @Test
    public void testProfileInformationDisplayed() {
        // Check that the user's name is displayed
        onView(withId(R.id.edit_name))
                .check(matches(withText("TestUser")));

        // Check that the user's email is displayed
        onView(withId(R.id.edit_email))
                .check(matches(withText("test@example.com")));

        // Check that the user's phone number is displayed
        onView(withId(R.id.edit_phone))
                .check(matches(withText("1234567890")));

        // Check that the notification switch is enabled
        onView(withId(R.id.notifications_switch))
                .check(matches(isChecked()));

        // Check that the geolocation switch is enabled
        onView(withId(R.id.geolocation_switch))
                .check(matches(isChecked()));
    }

//    @Test
//    public void testEditAndSaveProfile() {
//        // Click on the edit button
//        onView(withId(R.id.button_edit_profile)).perform(click());
//
//        // Update the name
//        onView(withId(R.id.edit_name))
//                .perform(clearText(), typeText("NewTestUser"), closeSoftKeyboard());
//
//        // Update the email
//        onView(withId(R.id.edit_email))
//                .perform(clearText(), typeText("newtest@example.com"), closeSoftKeyboard());
//
//        // Update the phone number
//        onView(withId(R.id.edit_phone))
//                .perform(clearText(), typeText("0987654321"), closeSoftKeyboard());
//
//        // Toggle notification switch
//        onView(withId(R.id.notifications_switch)).perform(click());
//
//        // Click on save button
//        onView(withId(R.id.button_save_profile)).perform(click());
//
//        // Verify that the user's set methods were called
//        verify(mockUser).setUsername("NewTestUser");
//        verify(mockUser).setEmail("newtest@example.com");
//        verify(mockUser).setPhoneNumber("0987654321");
//        verify(mockUser).setNotificationAsk(false); // Since we toggled it
//
//        // Verify that saveUserDataToFirestore was called
//        verify(mockUser).saveUserDataToFirestore();
//
//        // Optionally, check for a success toast message
//        // Note: Toast messages can be tricky to test and may require additional setup
//    }

//    @Test
//    public void testUploadProfilePicture() {
//        // Mock the uploadImage method to simulate success
//        doAnswer(invocation -> {
//            Uri uri = invocation.getArgument(0);
//            // Simulate successful upload
//            return null;
//        }).when(mockUser).uploadImage(any(Uri.class));
//
//        // Click on upload photo button
//        onView(withId(R.id.button_upload_photo)).perform(click());
//
//        // Since we cannot actually pick an image in a test, we'll simulate the result
//        // You might need to mock the ActivityResultLauncher or adjust your code to allow for testing
//
//        // Verify that uploadImage was called
//        // Note: This part can be complex due to the way image picking works in Android tests
//    }


    @Test
    public void testRemoveProfilePicture() {
        // Assume the user has a custom profile picture
        when(mockUser.getProfilePictureUrl()).thenReturn("https://example.com/profile.jpg");
        when(mockUser.isDefaultURLMain()).thenReturn(false);

        // Click on remove image button
        onView(withId(R.id.button_remove_photo)).perform(click());

        // Verify that deleteSelectedImageFromFirebase was called
        verify(mockUser).deleteSelectedImageFromFirebase("https://example.com/profile.jpg");

        // Verify that the profile image is set to the placeholder
        onView(withId(R.id.user_image))
                .check(matches(withTagValue(equalTo(R.drawable.placeholder_image_foreground))));

        // Optionally, check for a success toast message
    }


    @Test
    public void testEditAndSaveProfile() {
        // Mock the saveUserDataToFirestore method
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockTask;
        });
        when(mockTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            // Do nothing or simulate failure if needed
            return mockTask;
        });
        when(mockUser.saveUserDataToFirestore()).thenReturn(mockTask);

        // Rest of the test code
    }


    @Test
    public void testUploadProfilePicture() {
        // Initialize Intents
        Intents.init();

        // Stub the image picker intent
        intending(IntentMatchers.hasAction(Intent.ACTION_PICK))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent()));

        // Click on upload photo button
        onView(withId(R.id.button_upload_photo)).perform(click());

        // Verify that the intent was sent
        intended(IntentMatchers.hasAction(Intent.ACTION_PICK));

        // Verify that uploadImage was called
        verify(mockUser).uploadImage(any(Uri.class));

        // Release Intents
        Intents.release();
    }

    @Test
    public void testEditProfile() {
//        onView(withId(R.id.text_login_welcome)).check(matches(isDisplayed()));
//        SystemClock.sleep(5000);
//        onView(withId(R.id.home_title)).check(matches(isDisplayed()));
//        onView(withId(R.id.button_profile)).perform(click());
        onView(withId(R.id.button_edit_profile)).perform(click());
        onView(withId(R.id.edit_name)).perform(clearText(), typeText("test1"), closeSoftKeyboard());
        onView(withId(R.id.edit_email)).perform(clearText(), typeText("test2"), closeSoftKeyboard());
        onView(withId(R.id.edit_phone)).perform(clearText(), typeText("test3"), closeSoftKeyboard());
        onView(withClassName(containsString(ScrollView.class.getSimpleName()))).perform(swipeUp());
        //onView(withId(R.id.button_save_profile)).perform(scrollTo());
        onView(withId(R.id.button_save_profile)).perform(click());
        onView(withId(R.id.edit_name)).check(matches(withText("test1")));
        onView(withId(R.id.edit_email)).check(matches(withText("test2")));
        onView(withId(R.id.edit_phone)).check(matches(withText("3")));
    }



}
