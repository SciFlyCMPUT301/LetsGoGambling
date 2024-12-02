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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.QRCode.ScannedFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;

import com.example.eventbooking.UserManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * ProfileEntrantFragment is a Fragment that handles the display and editing of an entrant's profile.
 * It allows users to view and update their personal details such as name, email, phone number, and notification preferences.
 * It also handles logic for new user creation, editing modes, and saving data to Firestore.
 */

public class ProfileEntrantFragment extends Fragment {

    protected EditText editName;
    protected EditText editEmail;
    protected EditText editPhone;
    private TextView profileTitle;
    protected Button saveButton;
    private Button removeImageButton;
    private Button backButton;
    private Button uploadButton;
    protected Button editButton;
    protected Switch notificationsSwitch;
    private Switch geolocationSwitch;
    private ImageView userImage;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;


    private EntrantProfileManager profileManager;
    private EntrantProfile currentProfile;
    public User currentUser;
    public boolean isEditing = false;
    public boolean isNewUser = false;
    private String deviceId;
    private String eventIDFromQR = "";
    private boolean testing = true;
    /**
     * Creates a new instance of the ProfileEntrantFragment with arguments for new user status, event ID, and device ID.
     *
     * @param isNewUser Flag indicating if the user is new
     * @param eventIdFromQR Event ID obtained from QR scan (if any)
     * @param deviceId Device ID of the user
     * @return A new instance of ProfileEntrantFragment
     */
    public static ProfileEntrantFragment newInstance(boolean isNewUser, String eventIdFromQR, String deviceId) {
        ProfileEntrantFragment fragment = new ProfileEntrantFragment();
        Bundle args = new Bundle();
        args.putBoolean("isNewUser", isNewUser);
        args.putString("eventId", eventIdFromQR);
        args.putString("deviceId", deviceId);

        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Initializes the fragment with arguments passed during creation.
     * Retrieves the new user status, event ID, and device ID from the arguments.
     *
     * @param savedInstanceState Saved state bundle for the fragment
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Profile On Create", "Before arg");
        if (getArguments() != null) {
            isNewUser = getArguments().getBoolean("isNewUser");
            eventIDFromQR = getArguments().getString("eventId");
            deviceId = getArguments().getString("deviceId");
            Log.d("Profile On Create", "After arg: " + isNewUser + " " + eventIDFromQR + " " + deviceId);

        }
    }

    /**
     * Inflates the layout for this fragment and sets up the views, buttons, and listeners.
     *
     * @param inflater The LayoutInflater used to inflate the layout
     * @param container The parent view group
     * @param savedInstanceState Saved state bundle for the fragment
     * @return The view for this fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_profile, container, false);

        // Initialize views
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
        // Initialize EntrantProfileManager
        profileManager = new EntrantProfileManager();

        if (!isNewUser) {
            Log.d("Profile", "Loading the profile");
            onProfileLoaded(UserManager.getInstance().getCurrentUser());
        }else{
            Log.d("Profile", "New profile");
            currentUser = new User();
        }
        uploadButton.setVisibility(View.GONE);

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        //backButton.setOnClickListener(v -> requireActivity().onBackPressed()); // this crashes right now for some reason
        backButton.setOnClickListener(v -> goToHome());
        editButton.setOnClickListener(v -> toggleEditMode());
        uploadButton.setOnClickListener(v-> uploadPhoto());
        removeImageButton.setOnClickListener(v-> removeImage());
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
            setEditMode(true);
            editButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            NavigationView sidebar = getActivity().findViewById(R.id.nav_view);
            sidebar.setVisibility(View.GONE);
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
            View nav = getActivity().findViewById(R.id.bottom_navigation);
            nav.setVisibility(View.GONE);
        } else {
            setEditMode(false);
        }
        return view;
    }

    /**
     * Navigates to the home fragment.
     */
    private void goToHome() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
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
            Toast.makeText(getContext(), "No profile data found.", Toast.LENGTH_SHORT).show();
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
            geolocationSwitch.setChecked(loadingUser.isGeolocationAsk());
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
            Toast.makeText(getContext(), "No profile data found.", Toast.LENGTH_SHORT).show();
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
        currentUser.setEmail(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());
        currentUser.setNotificationAsk(notificationsSwitch.isChecked());
        currentUser.setGeolocationAsk(geolocationSwitch.isChecked());
        Log.d("Profile save", "Profile " + currentUser.getdefaultProfilePictureUrl());
        UserManager.getInstance().setCurrentUser(currentUser);
        String deviceID = getDeviceID();
        Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
        setEditMode(false);
        // Handle navigation after saving profile
        if (isNewUser) {
            Bitmap bitmap = currentUser.generateProfileBitmap(currentUser.getUsername());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            String imageFileName = "defaultProfilePictures/" + UUID.randomUUID() + ".png";
            StorageReference imageRef = currentUser.storageReference.child(imageFileName);

            imageRef.putBytes(imageBytes).addOnSuccessListener(taskSnapshot ->
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                currentUser.setProfilePictureUrl(downloadUrl);
                                currentUser.setdefaultProfilePictureUrl(downloadUrl);

                                // Save user data to Firestore
                                currentUser.saveUserDataToFirestore().addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                                    setEditMode(false);

                                    // Update profile image in UI
                                    if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                        Picasso.get().load(downloadUrl)
                                                .placeholder(R.drawable.placeholder_image_foreground)
                                                .error(R.drawable.error_image_foreground)
                                                .into(userImage);
                                    }

                                    // Update UI elements visibility
                                    editButton.setVisibility(View.VISIBLE);
                                    backButton.setVisibility(View.VISIBLE);
                                    NavigationView sidebar = getActivity().findViewById(R.id.nav_view);
                                    sidebar.setVisibility(View.VISIBLE);
                                    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                                    toolbar.setVisibility(View.VISIBLE);
                                    View nav = getActivity().findViewById(R.id.bottom_navigation);
                                    nav.setVisibility(View.VISIBLE);

                                    if(eventIDFromQR == null){
                                        Log.d("ProfileEntrant", "Nothing found");
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, HomeFragment.newInstance(getDeviceID()))
                                                .commit();
                                    } else {
                                        Log.d("ProfileEntrant", "Found QR link: " + eventIDFromQR);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIDFromQR, deviceId))
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to save profile.", Toast.LENGTH_SHORT).show();
                                    Log.e("ProfileEntrantFragment", "Error saving profile", e);
                                });
                            }).addOnFailureListener(e -> {
                                Log.e("Firebase", "Failed to retrieve download URL", e);
                                Toast.makeText(getContext(), "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                            })
            ).addOnFailureListener(e -> {
                Log.e("Firebase", "Image upload failed", e);
                Toast.makeText(getContext(), "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
            });

        } else{
            currentUser.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                        setEditMode(false);
                        // Handle navigation or other logic after saving
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save profile.", Toast.LENGTH_SHORT).show();
                    });
        }



    }
    /**
     * Toggles between editing and viewing mode for the profile.
     */
    public void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
        Toast.makeText(getContext(), isEditing ? "Edit mode enabled" : "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }
    /**
     * Enables or disables the editing mode of the profile.
     *
     * @param enable Flag to enable or disable edit mode
     */
    public void setEditMode(boolean enable) {
        editName.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPhone.setEnabled(enable);
        notificationsSwitch.setEnabled(enable);
        geolocationSwitch.setEnabled(enable);
        saveButton.setEnabled(enable);
        editButton.setText(enable ? "Cancel" : "Edit");
        uploadButton.setVisibility(View.VISIBLE);
    }
    /**
     * Retrieves the device ID from the settings.
     *
     * @return The device ID
     */

    private String getDeviceID() {
        if(testing)
            return "deviceID1";
        return Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * Starts the activity to upload a profile photo.
     */
    public void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    /**
     * Removes the profile picture from the user profile.
     */
    public void removeImage() {
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
            Toast.makeText(getContext(), "Profile image removed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Default image is already set.", Toast.LENGTH_SHORT).show();
        }
    }

}