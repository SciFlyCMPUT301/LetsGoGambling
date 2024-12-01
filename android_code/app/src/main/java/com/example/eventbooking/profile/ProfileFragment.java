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

public class ProfileFragment extends Fragment {

    private EditText editName, editEmail, editPhone;
    private TextView profileTitle;
    private Button editButton, uploadButton, backButton, removeImageButton, saveButton, notification_button;
    private Switch notificationsSwitch, geolocationSwitch;
    private ImageView userImage;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;

    private User currentUser;
    private boolean isEditing = false;
    private boolean isNewUser = false;
    private String deviceId;
    private String eventIDFromQR;

    public static ProfileFragment newInstance(boolean isNewUser, String eventId, String deviceId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("isNewUser", isNewUser);
        args.putString("eventId", eventId);
        args.putString("deviceId", deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        if (getArguments() != null) {
            Log.d("Profile Fragment", "Arguments passed");
            isNewUser = getArguments().getBoolean("isNewUser", false);
            eventIDFromQR = getArguments().getString("eventId", null);
//            deviceId = getArguments().getString("deviceId", null);
            deviceId = getArguments().getString("deviceId", UserManager.getInstance().getUserId());
        }

        Log.d("Profile Fragment", "New User: " + isNewUser);

        initializeUI(view);
        if(isNewUser)
            hideUIButtons();

        // Load user data
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
        uploadButton.setVisibility(View.GONE);
        removeImageButton.setVisibility(View.GONE);

        saveButton.setOnClickListener(v -> saveUserProfile());
        backButton.setOnClickListener(v -> goToHome());
        editButton.setOnClickListener(v -> toggleEditMode());
        uploadButton.setOnClickListener(v -> uploadPhoto());
        removeImageButton.setOnClickListener(v -> removeImage());
        notification_button.setOnClickListener(v -> navigateNotif());

    }

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

    private void saveUserProfile() {
        Log.d("Profile Fragment", "eventID" + eventIDFromQR);
        currentUser.setUsername(editName.getText().toString().trim());
        currentUser.setEmail(editEmail.getText().toString().trim());
        currentUser.setPhoneNumber(editPhone.getText().toString().trim());
        currentUser.setNotificationAsk(notificationsSwitch.isChecked());
        currentUser.setGeolocationAsk(geolocationSwitch.isChecked());
        if(isNewUser){
            if(!UniversalProgramValues.getInstance().getTestingMode()){
                currentUser.defaultProfilePictureUrl(currentUser.getUsername()).addOnSuccessListener(aVoid -> {
                    Log.d("Profile Fragment", "Setting new User");
                    UserManager.getInstance().setCurrentUser(currentUser);
                }).addOnFailureListener(e -> Log.d("User Manager", "Failed to Update Geopoint"));
    //            String profileURL = currentUser.defaultProfilePictureUrl(currentUser.getUsername()).toString();
    //            currentUser.setdefaultProfilePictureUrl(profileURL);
    //            currentUser.setProfilePictureUrl(profileURL);
            }
            else{
                currentUser.setProfilePictureUrl("NewDefaultTestURL");
                currentUser.setdefaultProfilePictureUrl("NewDefaultTestURL");
                UserManager.getInstance().setCurrentUser(currentUser);
            }
        }

        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            currentUser.saveUserDataToFirestore().addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Profile saved successfully.", Toast.LENGTH_SHORT).show();
                setEditMode(false);
//                if (isNewUser) {
//                    if (currentUser.isGeolocationAsk()) {
//                        UserManager.getInstance().updateGeolocation();
//                    }
//
//                }
                UserManager.getInstance().setCurrentUser(currentUser);
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

    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
    }

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

    private void goToHome() {
        Log.d("Profile Fragment", "Navigating to HomeFragment");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void navigateNotif(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NotificationFragment())
                .commit();
    }

    private void hideUIButtons(){
        editButton.setVisibility(View.GONE);
        uploadButton.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        removeImageButton.setVisibility(View.GONE);
        notification_button.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        ((MainActivity) getActivity()).hideNavigationUI();
    }

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
