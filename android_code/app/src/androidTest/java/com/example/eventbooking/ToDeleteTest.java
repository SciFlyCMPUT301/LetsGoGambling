package com.example.eventbooking;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


@RunWith(AndroidJUnit4.class)
public class ToDeleteTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Tests creating a user, the user is saved based on device ID to firestore
     * This tests US 01.02.01 and US 01.07.01
     */
    @Test
    public void testCreateUser() {
        // Delete the current devices user if it exists
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.delete();

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("123-456-7890"), closeSoftKeyboard());

        Espresso.onView(withId(R.id.account_save_button)).perform(click());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Doe", document.getString("name"));
                            assertEquals("john.doe@example.com", document.getString("email"));
                            assertEquals("123-456-7890", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));

                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Doe")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("123-456-7890")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }

    /**
     * Tests editing user, the edits should be reflected in both firebase and the UI
     * This tests US 01.02.02
     */
    @Test
    public void testEditUser() {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", false);
            put("admin", false);
            put("profileImage", "");
        }});

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Tester")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));
        onView(withId(R.id.account_save_button)).check(matches(not(isDisplayed())));

        // Edit name
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("Jane User"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("john@example.com", document.getString("email"));
                            assertEquals("999-999-9999", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));

        // Edit email
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("jane.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("jane.doe@example.com", document.getString("email"));
                            assertEquals("999-999-9999", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("jane.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));

        // Edit phone
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("123-456-7890"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("jane.doe@example.com", document.getString("email"));
                            assertEquals("123-456-7890", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("jane.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("123-456-7890")));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    /**
     * Tests creating a user without a phone number, the user is saved based on device ID to firestore
     * This tests US 01.02.01
     */
    @Test
    public void testCreateUserNoPhone() {
        // Delete the current devices user if it exists
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.delete();

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.account_save_button)).perform(click());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Doe", document.getString("name"));
                            assertEquals("john.doe@example.com", document.getString("email"));
                            assertEquals("", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Doe")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }

    // There are no automated tests for uploading images, this requires a human eye to test, and a specific device to select different images, therefore no point in automating
    // This is US 01.03.01 that will not be tested

    /**
     * Tests creating that the profile image is created from the users initials
     * This tests US 01.03.03
     */
    @Test
    public void testGeneratedProfileImage() {
        // Delete the current devices user if it exists
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.delete();

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.imagePlaceholder))
                .check(matches(withText("")));
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("123-456-7890"), closeSoftKeyboard());

        Espresso.onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Doe", document.getString("name"));
                            assertEquals("john.doe@example.com", document.getString("email"));
                            assertEquals("123-456-7890", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));

                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Doe")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("123-456-7890")));
        onView(withId(R.id.imagePlaceholder))
                .check(matches(withText("JD")));

        // Edit name
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("Billy Jim"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        onView(withId(R.id.imagePlaceholder))
                .check(matches(withText("BJ")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }

    /**
     * Tests deleting a profile image from an existing user. The image should be deleted. The user should not. The image should be replaced with
     * This tests US 01.03.02
     * This somewhat tests US 01.03.01 the best we can, it checks that the custom profile image is displayed
     */
    @Test
    public void testDeleteProfileImage() throws IOException, ExecutionException, InterruptedException {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", false);
            put("admin", false);
            put("profileImage", "");
        }});

        Uri newUri = copyFirebaseImage();
        FirebaseFirestore.getInstance().collection("users").document(deviceId)
                .update("profileImage", newUri.toString());

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.imagePlaceholder))
                .check(doesNotExist());
        onView(withId(R.id.account_delete_image))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.imagePlaceholder))
                .check(matches(isDisplayed()))
                .check(matches(withText("JT")));

        assertTrue(isImageDeleted());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Tester", document.getString("name"));
                            assertEquals("john@example.com", document.getString("email"));
                            assertEquals("999-999-9999", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
    }

    private Uri copyFirebaseImage() throws IOException, ExecutionException, InterruptedException {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference sourceRef = storage.getReference().child("profileImages/test_profile_image.jpg");
        StorageReference destinationRef = storage.getReference().child("profileImages/new_test_profile_image.jpg");

        Context context = ApplicationProvider.getApplicationContext();
        File tempFile = new File(context.getCacheDir(), "temp_image.webp");

        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }

        Task<byte[]> downloadTask = sourceRef.getBytes(Long.MAX_VALUE);
        byte[] imageData = Tasks.await(downloadTask);

        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(imageData);
        }

        Uri tempFileUri = Uri.fromFile(tempFile);
        UploadTask uploadTask = destinationRef.putFile(tempFileUri);
        Tasks.await(uploadTask);

        Task<Uri> getDownloadUriTask = destinationRef.getDownloadUrl();
        Uri downloadUri = Tasks.await(getDownloadUriTask);

        return downloadUri;
    }

    private boolean isImageDeleted() {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference imageRef = storage.getReference().child("profileImages/new_test_profile_image.jpg");

        try {
            Tasks.await(imageRef.getMetadata());
            return false;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StorageException) {
                StorageException storageException = (StorageException) e.getCause();
                if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
