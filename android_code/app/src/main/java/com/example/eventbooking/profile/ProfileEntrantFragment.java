package com.example.eventbooking.profile;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;

public class ProfileEntrantFragment extends Fragment {

    private EditText editName, editEmail, editPhone;
    private TextView profileTitle;
    private Button saveButton, backButton, editButton;
    private Switch notificationsSwitch;
    private EntrantProfileManager profileManager;
    private EntrantProfile currentProfile;
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileTitle = view.findViewById(R.id.profile_title);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        saveButton = view.findViewById(R.id.button_save_profile);
        backButton = view.findViewById(R.id.button_back_home);
        editButton = view.findViewById(R.id.button_edit_profile);

        // Initialize EntrantProfileManager
        profileManager = new EntrantProfileManager();

        // Load existing profile data
        loadUserProfile();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveUserProfile());
        backButton.setOnClickListener(v -> requireActivity().onBackPressed()); // Updated line
        editButton.setOnClickListener(v -> toggleEditMode());

        // Initially, set save button and switch to be disabled
        setEditMode(false);

        return view;
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

    private void saveUserProfile() {
        if (currentProfile == null) {
            currentProfile = new EntrantProfile();
        }

        currentProfile.setName(editName.getText().toString().trim());
        currentProfile.setEmail(editEmail.getText().toString().trim());
        currentProfile.setPhoneNumber(editPhone.getText().toString().trim());
        currentProfile.setNotificationsEnabled(notificationsSwitch.isChecked());

        String deviceID = getDeviceID();
        profileManager.createOrUpdateProfile(deviceID, currentProfile);

        Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
        setEditMode(false);
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
