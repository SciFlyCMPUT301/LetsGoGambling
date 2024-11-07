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

import com.example.eventbooking.UserManager;
import com.google.android.material.navigation.NavigationView;

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

    public static ProfileEntrantFragment newInstance(boolean isNewUser, String eventIdFromQR, String deviceId) {
        ProfileEntrantFragment fragment = new ProfileEntrantFragment();
        Bundle args = new Bundle();
        args.putBoolean("isNewUser", isNewUser);
        args.putString("eventId", eventIdFromQR);
        args.putString("deviceId", deviceId);

        fragment.setArguments(args);
        return fragment;
    }

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
        //loadUserProfile(); user data is already loaded
        if (!isNewUser) {
            onProfileLoaded(UserManager.getInstance().getCurrentUser());
        }


        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        //backButton.setOnClickListener(v -> requireActivity().onBackPressed()); // this crashes right now for some reason
        backButton.setOnClickListener(v -> goToHome());
        editButton.setOnClickListener(v -> toggleEditMode());

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

    private void goToHome() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void loadUserProfile() {
        String deviceID = getDeviceID();
        profileManager.getProfile(deviceID, this::onProfileLoaded);
    }

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

        UserManager.getInstance().setCurrentUser(savingUser);

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
//        profileManager.createOrUpdateProfile(deviceID, currentProfile);

        Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
        setEditMode(false);

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

    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
        Toast.makeText(getContext(), isEditing ? "Edit mode enabled" : "Edit mode disabled", Toast.LENGTH_SHORT).show();
    }

    private void setEditMode(boolean enable) {
        editName.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPhone.setEnabled(enable);
        notificationsSwitch.setEnabled(enable);
        saveButton.setEnabled(enable);
        editButton.setText(enable ? "Cancel" : "Edit");
    }

    private String getDeviceID() {
        return Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}