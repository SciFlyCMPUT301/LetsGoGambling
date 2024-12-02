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
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Login.LoginFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.notification.NotificationFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
/**
 * ProfileFragment handles the display and management of a user's profile.
 * It allows users to view and edit their profile information such as username, email, phone number,
 * and notifications preferences. It also allows users to upload or remove profile images.
 */
public class ProfileFragment extends Fragment {
    // UI components
    private EditText editName, editEmail, editPhone;
    private TextView profileTitle;
    private Button editButton, uploadButton, backButton, removeImageButton, saveButton, notification_button;
    private Switch notificationsSwitch, geolocationSwitch;
    private ImageView userImage;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
// User data and flags

    private User currentUser;
    private boolean isEditing = false;
    private boolean isNewUser = false;
    private String deviceId;
    private String eventIDFromQR;
    /**
     * Creates a new instance of ProfileFragment with the provided parameters.
     *
     * @param isNewUser Flag indicating if the user is new.
     * @param eventId   Event ID for navigating to an event.
     * @param deviceId  Device ID for identifying the user.
     * @return A new instance of ProfileFragment.
     */
    public static ProfileFragment newInstance(boolean isNewUser, String eventId, String deviceId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("isNewUser", isNewUser);
        args.putString("eventId", eventId);
        args.putString("deviceId", deviceId);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Called when the fragment's view is created.
     * Initializes the UI components and loads user data.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        // Retrieve arguments passed to the fragment
        if (getArguments() != null) {
            Log.d("Profile Fragment", "Arguments passed");
            isNewUser = getArguments().getBoolean("isNewUser", false);
            eventIDFromQR = getArguments().getString("eventId", null);
//            deviceId = getArguments().getString("deviceId", null);
            deviceId = getArguments().getString("deviceId", UserManager.getInstance().getUserId());
        }

        Log.d("Profile Fragment", "New User: " + isNewUser);

        initializeUI(view);
        // Hide UI components for new users
        if(isNewUser)
            hideUIButtons();

        // Load user data if not a new user
        if (!isNewUser) {
            Log.d("Profile Fragment", "Loading User Data");
            currentUser = UserManager.getInstance().getCurrentUser();
            onProfileLoaded(currentUser);
            setEditMode(false);
        } else {
            Log.d("Profile Fragment", "Getting New User");
            currentUser = UserManager.getInstance().getCurrentUser();
            setEditMode(true);
        }
        // Image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        currentUser.uploadImage(selectedImageUri);
                        userImage.setImageURI(selectedImageUri);
                    }
                });

        return view;
    }
    /**
     * Initializes the UI components and sets up click listeners.
     *
     * @param view The root view of the fragment.
     */
    private void initializeUI(View view) {
        profileTitle = view.findViewById(R.id.profile_title);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        geolocationSwitch = view.findViewById(R.id.geolocation_switch);
        saveButton = view.findViewById(R.id.button_save_profile);
        backButton = view.findViewById(R.id.button_back_home);
        editButton = view.findViewById(R.id.button_edit_profile);
        uploadButton = view.findViewById(R.id.button_upload_photo);
        userImage = view.findViewById(R.id.user_image);
        removeImageButton = view.findViewById(R.id.button_remove_photo);
        notification_button = view.findViewById(R.id.button_notification);
        // Hide buttons for image upload and removal initially
        uploadButton.setVisibility(View.GONE);
        removeImageButton.setVisibility(View.GONE);
        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        backButton.setOnClickListener(v -> goToHome());
        editButton.setOnClickListener(v -> toggleEditMode());
        uploadButton.setOnClickListener(v -> uploadPhoto());
        removeImageButton.setOnClickListener(v -> removeImage());
        notification_button.setOnClickListener(v -> navigateNotif());

    }
    /**
     * Populates the UI fields with the loaded user data.
     *
     * @param user The user whose profile data is being loaded.
     */
    private void onProfileLoaded(User user) {
        if (user != null) {
            editName.setText(user.getUsername());
            editEmail.setText(user.getEmail());
            editPhone.setText(user.getPhoneNumber());
            notificationsSwitch.setChecked(user.isNotificationAsk());
            geolocationSwitch.setChecked(user.isGeolocationAsk());

            String profilePictureUrl = user.getProfilePictureUrl();
            Log.d("Profile Fragment", "Loading User Photo: " + profilePictureUrl);
            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                Picasso.get().load(profilePictureUrl).into(userImage);
            }
        }
    }
    /**
     * Saves the edited user profile data.
     */
    private void saveUserProfile() {
        Log.d("Profile Fragment", "eventID" + eventIDFromQR);
        currentUser.setUsername(editName.getText().toString().trim());
        currentUser.setEmail(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());
        currentUser.setNotificationAsk(notificationsSwitch.isChecked());
        currentUser.setGeolocationAsk(geolocationSwitch.isChecked());
        // Handle new user profile creation
        if(isNewUser){
            if(!UniversalProgramValues.getInstance().getTestingMode()){
                currentUser.defaultProfilePictureUrl(currentUser.getUsername()).addOnSuccessListener(aVoid -> {
                    Log.d("Profile Fragment", "Setting new User");
                    UserManager.getInstance().setCurrentUser(currentUser);
                }).addOnFailureListener(e -> Log.d("User Manager", "Failed to Update Geopoint"));
            }
            else{
                currentUser.setProfilePictureUrl("NewDefaultTestURL");
                currentUser.setdefaultProfilePictureUrl("NewDefaultTestURL");
                LoginFragment.isLoggedIn = true;
                UserManager.getInstance().setCurrentUser(currentUser);
            }
        }
// Save user data to Firestore if not in testing mode
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            currentUser.saveUserDataToFirestore().addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                setEditMode(false);
                UserManager.getInstance().setCurrentUser(currentUser);
                LoginFragment.isLoggedIn = true;
                if (currentUser.isGeolocationAsk()) {
                    UserManager.getInstance().updateGeolocation();
                }
                ((MainActivity) getActivity()).showNavigationUI();
                if(eventIDFromQR == null)
                    goToHome();
                else
                    goToEvent();
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save profile.", Toast.LENGTH_SHORT).show());
        }
        else{
            UniversalProgramValues.getInstance().setCurrentUser(currentUser);
            UserManager.getInstance().setCurrentUser(currentUser);
            if (currentUser.isGeolocationAsk()) {
                UserManager.getInstance().updateGeolocation();
            }
            ((MainActivity) getActivity()).showNavigationUI();
            Log.d("Profile Fragment", "Test Mode eventID: " + eventIDFromQR);
            if(eventIDFromQR == null)
                goToHome();
            else
                goToEvent();
        }
        isNewUser = false;
    }
    /**
     * Toggles between edit and view mode for the profile.
     */
    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
    }
    /**
     * Sets the profile in either edit mode or view mode.
     *
     * @param enable True to enable edit mode, false to disable it.
     */
    private void setEditMode(boolean enable) {
        if(!isNewUser && enable){
            uploadButton.setVisibility(View.VISIBLE);
            removeImageButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }
        if(!isNewUser && !enable){
            uploadButton.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }
        editName.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPhone.setEnabled(enable);
        notificationsSwitch.setEnabled(enable);
        geolocationSwitch.setEnabled(enable);
        saveButton.setEnabled(enable);
        editButton.setText(enable ? "Cancel" : "Edit");
    }
    /**
     * Opens the image picker to upload a profile photo.
     */
    private void uploadPhoto() {
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        }
        else{
//            currentUser.uploadImage(selectedImageUri);
            String URI = UniversalProgramValues.getInstance().getUploadProfileURL();
            selectedImageUri = Uri.parse(URI);
            currentUser.setProfilePictureUrl(URI);
//            profilePictureUrl = imageURL;
            userImage.setImageURI(selectedImageUri);
        }

    }
    /**
     * Removes the profile image and resets to the default image.
     */
    private void removeImage() {
        if (!currentUser.isDefaultURLMain()) {
            currentUser.deleteSelectedImageFromFirebase(currentUser.getProfilePictureUrl());
            userImage.setImageResource(R.drawable.placeholder_image_foreground);
            userImage.setImageURI(Uri.parse(currentUser.getdefaultProfilePictureUrl()));
            Toast.makeText(getContext(), "Profile image removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Default image is already set.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Navigates to the HomeFragment.
     */
    private void goToHome() {
        Log.d("Profile Fragment", "Navigating to HomeFragment");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }
    /**
     * Navigates to the NotificationFragment.
     */
    private void navigateNotif(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NotificationFragment())
                .commit();
    }
    /**
     * Hides UI elements for new users during profile setup.
     */
    private void hideUIButtons(){
        editButton.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        removeImageButton.setVisibility(View.GONE);
        notification_button.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        ((MainActivity) getActivity()).hideNavigationUI();
    }

    /**
     * Navigates to the EventViewFragment.
     */
    private void goToEvent(){
        Log.d("Profile Fragment", "Navigating to Event View Fragment");
        if(UniversalProgramValues.getInstance().getTestingMode()){
            EventViewFragment eventFragment = EventViewFragment.newInstance(
                    UniversalProgramValues.getInstance().getCurrentEvent(),
                    UniversalProgramValues.getInstance().getDeviceID());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, eventFragment)
                    .commit();
        }
        else{
            EventViewFragment eventFragment = EventViewFragment.newInstance(eventIDFromQR, deviceId);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, eventFragment)
                    .commit();
        }

    }
}
