package com.example.eventbooking.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeActivity;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    protected EditText editName, editEmail, editPhone;
    private TextView profileTitle;
    private Button editButton, uploadButton, backButton, removeImageButton, saveButton;
    protected Switch notificationsSwitch;
    private ImageView userImage;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;

    // Start of Testing values
    private Switch testingSwitch;
    private EditText deviceIDEntry;
    // End of Testing Values

    private EntrantProfileManager profileManager;
    private EntrantProfile currentProfile;
    public User currentUser;
    public boolean isEditing = false;
    public boolean isNewUser = false;
    private String deviceId;
    private String eventIDFromQR = "";
    private boolean testing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Log.d("Profile Activity", "Launched Activity");
        // Unpacking the bundle here
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isNewUser = bundle.getBoolean("isNewUser");
            eventIDFromQR = bundle.getString("eventId");
            deviceId = bundle.getString("deviceId");
        }
        else {
            Log.e("Profile Activity", "No extras found in intent");
            isNewUser = false;
            eventIDFromQR = null;
            deviceId = UserManager.getInstance().getUserId();
            Log.e("Profile Activity", "deviceID: " + deviceId);
        }
        Log.d("Profile Activity", "Bundle New User " + isNewUser);
        Log.d("Profile Activity", "Bundle Event ID " + eventIDFromQR);
        Log.d("Profile Activity", "Bundle Device ID " + deviceId);

        // Initialize views
        initalizeUI();

        // Initialize EntrantProfileManager
        profileManager = new EntrantProfileManager();

        // Load existing profile data
        //loadUserProfile(); user data is already loaded
        if (!isNewUser) {
            Log.d("Profile", "Loading the profile");
//            String testDeviceID = "deviceID1";
//            loadUserByDeviceID(testDeviceID);
            onProfileLoaded(UserManager.getInstance().getCurrentUser());
        }else{
            Log.d("Profile", "New profile");
            currentUser = new User();
            editName.setText("");
            editEmail.setText("");
            editPhone.setText("");
        }
        uploadButton.setVisibility(View.GONE);





        // Handle testing switch
//        testingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    testing = true;
//                } else {
//                    testing = false;
//                }
//            }
//        });
        // Image launcher to get images and store them temporarially
        ///TODO: Confirm if this code is redundant
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            // Upload the image and update the profile picture URL
                            currentUser.uploadImage(selectedImageUri);
                            // Update the ImageView with the selected image
                            userImage.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        // Initially, set save button and switch to be disabled
        // Set up the fragment for new users
        if (isNewUser) {
            Log.d("Profile Activity", "New User End of OnCreate");
            setEditMode(true);
            editButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
//            NavigationView sidebar = getActivity().findViewById(R.id.nav_view);
//            sidebar.setVisibility(View.GONE);
//            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//            toolbar.setVisibility(View.GONE);
//            View nav = getActivity().findViewById(R.id.bottom_navigation);
//            nav.setVisibility(View.GONE);
        } else {
            setEditMode(false);
        }
    }


    /**
     * Will move to the home activity
     */
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("source_activity", "Profile Activity Returning");
        startActivity(intent);
    }

    /**
     * Loads the profile data from Firestore using the device ID.
     */
    private void loadUserProfile() {
        String deviceID = getDeviceID();
        profileManager.getProfile(deviceID, this::onProfileLoaded);
    }

    /**
     * Callback for when the profile is loaded from Firestore. Updates the UI with profile data.
     *
     * @param profile The loaded EntrantProfile object
     */

    private void onProfileLoaded(EntrantProfile profile) {
        if (profile != null) {
            currentProfile = profile;
            editName.setText(profile.getName());
            editEmail.setText(profile.getEmail());
            editPhone.setText(profile.getPhoneNumber());
            notificationsSwitch.setChecked(profile.isNotificationsEnabled());
        } else {
            Toast.makeText(this, "No profile data found.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for when the user profile is loaded.
     *
     * @param loadingUser The loaded User object
     */
    private void onProfileLoaded(User loadingUser) {
        if (loadingUser != null) {
//            currentUser.deepCopy(loadingUser);
            currentUser = loadingUser;
            Log.d("Profile load", "Loading profile " + currentUser.getdefaultProfilePictureUrl());
            Log.d("Profile load", "Loading profile " + loadingUser.getdefaultProfilePictureUrl());
            editName.setText(loadingUser.getUsername());
            editEmail.setText(loadingUser.getEmail());
            editPhone.setText(loadingUser.getPhoneNumber());
            notificationsSwitch.setChecked(loadingUser.isNotificationAsk());
            currentUser.setdefaultProfilePictureUrl(loadingUser.getdefaultProfilePictureUrl());
            String profilePictureUrl = loadingUser.getProfilePictureUrl();
            Log.d("Profile", "URL "+ profilePictureUrl);
            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                Picasso.get()
                        .load(profilePictureUrl) // URL of the image
                        .placeholder(R.drawable.placeholder_image_foreground) // Placeholder image while loading
                        .error(R.drawable.error_image_foreground) // Error image if loading fails
                        .into(userImage); // ImageView to load the image into
            }
        } else {
            Toast.makeText(this, "No profile data found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the user profile data to Firestore, either creating or updating the profile.
     * Also, handles saving user data for new users.
     */

    public void saveUserProfile() {
        if (currentProfile == null) {
            currentProfile = new EntrantProfile();
        }

        currentProfile.setName(editName.getText().toString().trim());
        currentProfile.setEmail(editEmail.getText().toString().trim());
        currentProfile.setPhoneNumber(editPhone.getText().toString().trim());
        currentProfile.setNotificationsEnabled(notificationsSwitch.isChecked());

        currentUser.setUsername(editName.getText().toString().trim());
        Log.d("Profile Activity", "Saving profile, name: " + editName.getText().toString().trim());
        currentUser.setEmail(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());
        currentUser.setNotificationAsk(notificationsSwitch.isChecked());

        // Added with making it an activity, want to make it this as we are passing
        // This value from LoginActivity into ProfileActivity
        currentUser.setDeviceID(deviceId);
        Log.d("Profile Activity", "Saving profile, deviceID: " + deviceId);
        currentUser.addRole(Role.ENTRANT);
//        Log.d("Profile save", "Profile " + currentUser.getdefaultProfilePictureUrl());
//        if(selectedImageUri != null){
//            currentUser.setProfilePictureUrl(selectedImageUri.toString());
//        }

//        if(testing){
//            currentUser.setDeviceID(deviceIDEntry.getText().toString().trim());
//        }

        UserManager.getInstance().setCurrentUser(currentUser);

        Toast.makeText(this, "Profile saved successfully.", Toast.LENGTH_SHORT).show();
        setEditMode(false);
        // Handle navigation after saving profile
        if (isNewUser) {
            Log.d("Profile save", "isNewUser start");
            // Generate a new profile photo for the user
            currentUser.defaultProfilePictureUrl(currentUser.getUsername())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Default Profile Picture saved successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save Default Profile Picture.", Toast.LENGTH_SHORT).show();
                    });

            // Delete this???
            editButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);

            // Putting this here to make code work, not sure if this is how to do it
            currentUser.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                        setEditMode(false);
                        // Handle navigation or other logic after saving
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
                    });

            if(eventIDFromQR == null){
                Log.d("ProfileEntrant", "Nothing found");
                goToHome();
            } else {
                Log.d("ProfileEntrant", "Found QR link: " + eventIDFromQR);
                Intent eventIntent = new Intent(this, EventViewFragment.class);
                eventIntent.putExtra("eventID", eventIDFromQR);
                startActivity(eventIntent);
            }

        } else{
            Log.d("Profile Activity", "isNewUser start");
            currentUser.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                        setEditMode(false);
                        // Handle navigation or other logic after saving
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save profile.", Toast.LENGTH_SHORT).show();
                    });
        }

    }


    /**
     * Toggles the edit mode on and off. When enabled, the fields become editable, and the user can save changes.
     */
    public void toggleEditMode() {
        Log.d("Profile Activity", "Toggle set Edit Mode");
        isEditing = !isEditing;
        setEditMode(isEditing);
        Toast.makeText(this, isEditing ? "Edit mode enabled" : "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }
    /**
     * Sets the edit mode for the profile. When enabled, the user can edit the profile details.
     *
     * @param enable Flag indicating whether edit mode should be enabled or not
     */
    public void setEditMode(boolean enable) {
        Log.d("Profile Activity", "Function set Edit Mode");
        editName.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPhone.setEnabled(enable);
        notificationsSwitch.setEnabled(enable);
        saveButton.setEnabled(enable);
        editButton.setText(enable ? "Cancel" : "Edit");
        uploadButton.setVisibility(View.VISIBLE);
    }
    /**
     * Retrieves the device ID of the current device.
     *
     * @return The device ID
     */

    private String getDeviceID() {
        Log.d("Profile Activity", "Function getDeviceID");
        if(testing)
            return "deviceID1";
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * Uploading a photo to firebase
     */
    public void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    public void removeImage() {
        Log.d("Profile Activity", "Remove Image Start");
        Log.d("Profile Activity", "Remove Image Default: " + currentUser.getdefaultProfilePictureUrl());
        Log.d("Profile Activity", "Remove Image Main: " + currentUser.getProfilePictureUrl());
        if (!currentUser.isDefaultURLMain()) {
            currentUser.deleteSelectedImageFromFirebase(currentUser.getProfilePictureUrl());

            // Update the UI to display the default image
            String defaultImageUrl = currentUser.getdefaultProfilePictureUrl();
            if (defaultImageUrl != null && !defaultImageUrl.isEmpty()) {
                Picasso.get()
                        .load(defaultImageUrl)
                        .placeholder(R.drawable.placeholder_image_foreground)
                        .error(R.drawable.error_image_foreground)
                        .into(userImage);
            } else {
                // If default image URL is not available, set a placeholder image
                userImage.setImageResource(R.drawable.placeholder_image_foreground);
            }
            Toast.makeText(this, "Profile image removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Default image is already set.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initalizeUI(){
        Log.d("Profile Activity", "Initalizing UI");
        // Set up elements in XML to objects
        profileTitle = findViewById(R.id.profile_title);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        notificationsSwitch = findViewById(R.id.notifications_switch);
//        testingSwitch = findViewById(R.id.testing_switch);
        saveButton = findViewById(R.id.button_save_profile);
        backButton = findViewById(R.id.button_back_home);
        editButton = findViewById(R.id.button_edit_profile);
//        deviceIDEntry = findViewById(R.id.device_id_entry);
        uploadButton = findViewById(R.id.button_upload_photo);
        userImage = findViewById(R.id.user_image);
        removeImageButton = findViewById(R.id.button_remove_photo);

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        //backButton.setOnClickListener(v -> requireActivity().onBackPressed()); // this crashes right now for some reason
        backButton.setOnClickListener(v -> goToHome());
        editButton.setOnClickListener(v -> toggleEditMode());
        uploadButton.setOnClickListener(v-> uploadPhoto());
        removeImageButton.setOnClickListener(v-> removeImage());
    }

}
