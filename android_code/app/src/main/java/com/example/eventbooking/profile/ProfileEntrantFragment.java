package com.example.eventbooking.profile;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.QRCode.ScannedFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;

import com.google.android.material.navigation.NavigationView;
/**
 * ProfileEntrantFragment is a Fragment that handles the display and editing of an entrant's profile.
 * It allows users to view and update their personal details such as name, email, phone number, and notification preferences.
 * It also handles logic for new user creation, editing modes, and saving data to Firestore.
 */

public class ProfileEntrantFragment extends Fragment {

    private EditText editName, editEmail, editPhone;
    private TextView profileTitle;
    private Button saveButton, backButton, editButton;
    private Switch notificationsSwitch;

    // Start of Testing values
    private Switch testingSwitch;
    private EditText deviceIDEntry;
    private boolean testing;
    // End of Testing Values

    private EntrantProfileManager profileManager;
    private EntrantProfile currentProfile;
    private User currentUser;
    private boolean isEditing = false;
    private boolean isNewUser = false;
    private String deviceId;
    private String eventIDFromQR = "";
    /**
     * Creates a new instance of the ProfileEntrantFragment with arguments for new user status, event ID, and device ID.
     *
     * @param isNewUser   Flag indicating if the user is new
     * @param eventIdFromQR Event ID obtained from QR scan (if any)
     * @param deviceId     Device ID of the user
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
     * @param inflater           The LayoutInflater used to inflate the layout
     * @param container          The parent view group
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
        testingSwitch = view.findViewById(R.id.testing_switch);
        saveButton = view.findViewById(R.id.button_save_profile);
        backButton = view.findViewById(R.id.button_back_home);
        editButton = view.findViewById(R.id.button_edit_profile);
        deviceIDEntry = view.findViewById(R.id.device_id_entry);
        // Initialize EntrantProfileManager
        profileManager = new EntrantProfileManager();

        // Load existing profile data
        loadUserProfile();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        backButton.setOnClickListener(v -> requireActivity().onBackPressed()); // Updated line
        editButton.setOnClickListener(v -> toggleEditMode());
        // Handle testing switch
        testingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    testing = true;
                } else {
                    testing = false;
                }
            }
        });

        // Initially, set save button and switch to be disabled
        // Set up the fragment for new users
        if (isNewUser) {
            setEditMode(true);
            editButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
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
            currentUser = loadingUser;
            editName.setText(loadingUser.getUsername());
            editEmail.setText(loadingUser.getEmail());
            editPhone.setText(loadingUser.getPhoneNumber());
            notificationsSwitch.setChecked(loadingUser.isNotificationAsk());
        } else {
            Toast.makeText(getContext(), "No profile data found.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Saves the user profile data to Firestore, either creating or updating the profile.
     * Also, handles saving user data for new users.
     */

    private void saveUserProfile() {
        if (currentProfile == null) {
            currentProfile = new EntrantProfile();
        }

        currentProfile.setName(editName.getText().toString().trim());
        currentProfile.setEmail(editEmail.getText().toString().trim());
        currentProfile.setPhoneNumber(editPhone.getText().toString().trim());
        currentProfile.setNotificationsEnabled(notificationsSwitch.isChecked());

        User savingUser = new User();
        savingUser.setUsername(editName.getText().toString().trim());
        savingUser.setEmail(editEmail.getText().toString().trim());
        savingUser.setPhoneNumber(editPhone.getText().toString().trim());
        savingUser.setNotificationAsk(notificationsSwitch.isChecked());
        savingUser.addRole("entrant");
        if(testing == true){
            savingUser.setDeviceID(deviceIDEntry.getText().toString().trim());
        }

        savingUser.saveUserDataToFirestore(new User.OnUserIDGenerated() {
            @Override
            public void onUserIDGenerated(String userID) {
                if (userID != null) {
                    // Handle successful save operation here
                } else {
                    // Handle failure (e.g., show an error message to the user)
                }
            }
        });

        String deviceID = getDeviceID();
// profileManager.createOrUpdateProfile(deviceID, currentProfile);

        Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
        setEditMode(false);
        // Handle navigation after saving profile
        if (isNewUser) {
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
//                      .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIdFromQR, deviceId))
                        .addToBackStack(null)
                        .commit();
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, ScannedFragment.newInstance(eventIDFromQR))
//                        .commit();
            }


        }
    }
    /**
     * Toggles the edit mode on and off. When enabled, the fields become editable, and the user can save changes.
     */

    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
        Toast.makeText(getContext(), isEditing ? "Edit mode enabled" : "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }
    /**
     * Sets the edit mode for the profile. When enabled, the user can edit the profile details.
     *
     * @param enable Flag indicating whether edit mode should be enabled or not
     */

    private void setEditMode(boolean enable) {
        editName.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPhone.setEnabled(enable);
        notificationsSwitch.setEnabled(enable);
        saveButton.setEnabled(enable);
        editButton.setText(enable ? "Cancel" : "Edit");
    }
    /**
     * Retrieves the device ID of the current device.
     *
     * @return The device ID
     */

    private String getDeviceID() {
        return Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}